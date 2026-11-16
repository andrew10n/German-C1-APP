package com.example.programme

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.C1AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class ProgrammeMigrationTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val databaseName = "programme-migration-test"
    private var database: C1AppDatabase? = null

    @After
    fun tearDown() {
        database?.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun `migration preserves every legacy table and validates the new Room schema`() = runBlocking {
        val before = createVersion4Database()
        val db = openDatabase()
        // Opening through Room executes the migration and validates all entity columns/defaults.
        val sql = db.openHelper.writableDatabase
        assertEquals(14, sql.version)
        assertIndexExists(sql, "programme_attempts", "index_programme_attempts_unitId_timestamp")
        assertIndexExists(sql, "real_life_experiences", "index_real_life_experiences_nextReviewAt")
        assertIndexExists(sql, "vocabulary_items", "index_vocabulary_items_nextReviewTimestamp")
        before.forEach { (table, rows) ->
            sql.query("SELECT * FROM `$table`").use { cursor ->
                val after = mutableListOf<List<String?>>()
                while (cursor.moveToNext()) {
                    after.add(List(cursor.columnCount) { cursor.getString(it) })
                }
                val preserved = if (table == "vocabulary_items") after.map { it.dropLast(1) } else after
                assertEquals("Legacy records changed in $table", rows, preserved)
            }
        }
        sql.query("SELECT sourceUnitId FROM vocabulary_items WHERE id = 'word'").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("", cursor.getString(0))
        }
        assertTrue(db.programmeAttemptDao().observeAttempts().first().isEmpty())
        assertTrue(db.realLifeExperienceDao().observeAll().first().isEmpty())

        // Omit version columns to exercise SQL defaults, not just Kotlin constructor defaults.
        sql.execSQL(
            """
            INSERT INTO programme_attempts
            (id, unitId, taskId, timestamp, response, source, scoresJson,
             assisted, unfamiliar, independentProduction, context, errorsJson)
            VALUES ('migrated', 'unit', 'task', 42, 'Antwort', 'SELF_REFLECTION',
                    '{}', 0, 0, 0, 'GENERAL', '[]')
            """.trimIndent()
        )
        val row = db.programmeAttemptDao().observeAttempts().first().single()
        assertEquals(1, row.taskVersion)
        assertEquals(1, row.rubricVersion)
        assertEquals(EvidenceSource.SELF_REFLECTION, row.toAttempt().source)
        assertEquals(emptyMap<Dimension, Int>(), row.toAttempt().scores)

        val repository = ProgrammeRepository(db.programmeAttemptDao())
        val attempt = sampleAttempt("new")
        repository.record(attempt)
        db.close()
        database = null
        val reopened = ProgrammeRepository(openDatabase().programmeAttemptDao())
        assertEquals(attempt, reopened.attempts.first().first { it.id == "new" })
    }

    @Test
    fun `migration downgrades unverifiable local human reviews without deleting them`() = runBlocking {
        val db = openDatabase()
        val legacyAttempt = sampleAttempt("legacy-review").copy(
            source = EvidenceSource.HUMAN_REVIEW,
            unfamiliar = true,
            independentProduction = true
        )
        val legacyAssessmentAttempt = sampleAttempt("legacy-assessment").copy(
            taskId = "assessment:goethe:listening",
            source = EvidenceSource.SELF_REFLECTION,
            unfamiliar = true,
            independentProduction = true
        )
        ProgrammeRepository(db.programmeAttemptDao()).record(legacyAttempt)
        ProgrammeRepository(db.programmeAttemptDao()).record(legacyAssessmentAttempt)
        db.humanReviewDao().insert(
            HumanReviewEntity(
                id = "legacy-review-note",
                attemptId = legacyAttempt.id,
                reviewerId = "local-id",
                reviewerName = "Coach",
                reviewerVerified = true,
                rubricVersion = 1,
                scoresJson = "ACCURACY:4",
                comment = "Retained note",
                createdAt = 123
            )
        )

        C1AppDatabase.MIGRATION_11_12.migrate(db.openHelper.writableDatabase)

        val downgraded = ProgrammeRepository(db.programmeAttemptDao()).attempts.first().associateBy { it.id }
        val downgradedReview = downgraded.getValue(legacyAttempt.id)
        assertEquals(EvidenceSource.SELF_REFLECTION, downgradedReview.source)
        assertTrue(downgradedReview.scores.isEmpty())
        assertFalse(downgradedReview.unfamiliar)
        assertFalse(downgradedReview.independentProduction)
        assertFalse(downgraded.getValue(legacyAssessmentAttempt.id).unfamiliar)
        val retainedReview = HumanReviewRepository(db.humanReviewDao()).reviews.first().single()
        assertFalse(retainedReview.reviewer.verified)
        assertEquals("Retained note", retainedReview.comment)
    }

    @Test
    fun `restored legacy assessment sessions are normalized to familiar text practice`() = runBlocking {
        val repository = AssessmentSessionRepository(openDatabase().assessmentSessionDao())
        val session = AssessmentSession(
            id = "legacy-assessment-session",
            kind = AssessmentSessionKind.GOETHE_STYLE,
            createdAt = 1,
            durationMinutes = 200,
            tasks = listOf(
                DeliveredAssessmentTask(
                    id = "legacy-task",
                    unitId = "s1-explain",
                    taskId = "assessment:goethe:listening",
                    prompt = "Bekannte Kursaufgabe",
                    evidence = AssessmentEvidence.AUDIO_RESPONSE,
                    context = ContextTrack.GENERAL,
                    unfamiliar = true,
                    variationId = "legacy",
                    taskVersion = 1,
                    rubricVersion = 1
                )
            )
        )
        repository.save(session, emptyMap(), currentIndex = 0, completed = false, now = 1)

        val restored = repository.active.first()!!.session.tasks.single()

        assertEquals(AssessmentEvidence.TEXT_RESPONSE, restored.evidence)
        assertFalse(restored.unfamiliar)
    }

    @Test
    fun `versioned audio practice remains audio evidence when its session is restored`() = runBlocking {
        val repository = AssessmentSessionRepository(openDatabase().assessmentSessionDao())
        val task = DeliveredAssessmentTask(
            id = "spoken-task",
            unitId = "s1-explain",
            taskId = "assessment:speaking:recorded-response",
            prompt = "Antwort mündlich aufnehmen.",
            evidence = AssessmentEvidence.AUDIO_RESPONSE,
            context = ContextTrack.GENERAL,
            unfamiliar = false,
            variationId = "local-audio",
            taskVersion = 3,
            rubricVersion = 1
        )
        repository.save(
            AssessmentSession(
                id = "audio-assessment",
                kind = AssessmentSessionKind.SPEAKING_PRACTICE,
                createdAt = 1,
                durationMinutes = 20,
                tasks = listOf(task)
            ),
            responses = mapOf(ASSESSMENT_AUDIO_RESPONSE_PREFIX + task.id to "voice.m4a"),
            currentIndex = 0,
            completed = false,
            now = 1
        )

        val restored = repository.active.first()!!

        assertEquals(AssessmentEvidence.AUDIO_RESPONSE, restored.session.tasks.single().evidence)
        assertEquals(
            "voice.m4a",
            restored.responses[ASSESSMENT_AUDIO_RESPONSE_PREFIX + task.id]
        )
        assertFalse(restored.session.tasks.single().unfamiliar)
    }

    @Test
    fun `feedback challenge migration preserves legacy decisions and adds empty source snapshots`() = runBlocking {
        val db = openDatabase()
        val sql = db.openHelper.writableDatabase
        sql.execSQL("DROP TABLE `feedback_challenges`")
        sql.execSQL(
            """
            CREATE TABLE `feedback_challenges` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `claimId` TEXT NOT NULL,
                `learnerDecision` TEXT,
                `correctedFeedback` TEXT,
                `status` TEXT NOT NULL
            )
            """.trimIndent()
        )
        sql.execSQL(
            """
            INSERT INTO `feedback_challenges` (`id`, `claimId`, `learnerDecision`, `correctedFeedback`, `status`)
            VALUES ('legacy-challenge', 'legacy-claim', 'DISAGREE', 'Eigene Notiz', 'OPEN')
            """.trimIndent()
        )

        C1AppDatabase.MIGRATION_12_13.migrate(sql)

        val restored = FeedbackChallengeRepository(db.feedbackChallengeDao()).challenges.first().single()
        assertEquals("legacy-claim", restored.claimId)
        assertEquals("DISAGREE", restored.learnerDecision)
        assertEquals(FeedbackChallengeStatus.OPEN, restored.status)
        assertEquals("", restored.responseExcerpt)
        assertEquals("", restored.proposedFeedback)
        assertEquals(ContextTrack.GENERAL, restored.context)
    }

    @Test
    fun `feedback challenges preserve cited source and allow only one terminal resolution`() = runBlocking {
        val unit = CurriculumCatalogue.units.first { it.id == "s1-explain" }
        val attempt = sampleAttempt("challenge-source").copy(
            unitId = unit.id,
            taskId = "writing",
            response = "Ich bleibe, weil ich gehe morgen.",
            source = EvidenceSource.SELF_REFLECTION,
            assisted = false,
            errors = emptyList()
        )
        val claim = AdaptiveLearning.recommend(unit, listOf(attempt), attempt.timestamp + 1)
            .feedbackClaims.single()
        val challenge = FeedbackEvidenceEngine.challenge(
            claim,
            FeedbackChallengeDecision.DISAGREE,
            "Bitte erläutere die Regel erneut.",
            now = 300
        )
        val repository = FeedbackChallengeRepository(openDatabase().feedbackChallengeDao())

        repository.record(challenge)
        assertEquals(listOf(challenge), repository.challenges.first())
        repository.resolve(
            challenge.id,
            FeedbackChallengeStatus.NEEDS_REASSESSMENT,
            "Neue unabhängige Prüfung nötig.",
            now = 400
        )

        val resolved = repository.challenges.first().single()
        assertEquals(FeedbackChallengeStatus.NEEDS_REASSESSMENT, resolved.status)
        assertEquals("Neue unabhängige Prüfung nötig.", resolved.correctedFeedback)
        assertEquals(400L, resolved.updatedAt)
        var repeatRejected = false
        try {
            repository.resolve(challenge.id, FeedbackChallengeStatus.ACCEPTED, null, now = 500)
        } catch (_: IllegalArgumentException) {
            repeatRejected = true
        }
        assertTrue("A completed challenge cannot be silently reclassified", repeatRejected)
    }

    @Test
    fun `repository round trips all attempt fields sources contexts and JSON escaping`() = runBlocking {
        val repository = ProgrammeRepository(openDatabase().programmeAttemptDao())
        val expected = EvidenceSource.values().flatMap { source ->
            ContextTrack.values().map { track ->
                sampleAttempt("${source.name}-${track.name}").copy(source = source, context = track)
            }
        }
        expected.reversed().forEach { repository.record(it) }
        assertEquals(expected.sortedBy { it.id }, repository.attempts.first())
        val empty = sampleAttempt("empty").copy(
            timestamp = 0,
            response = "",
            scores = emptyMap(),
            errors = emptyList(),
            assisted = false,
            unfamiliar = false,
            independentProduction = false
        )
        repository.record(empty)
        assertEquals(empty, repository.attempts.first().first())
    }

    @Test
    fun `duplicate attempt IDs abort without modifying existing evidence`() = runBlocking {
        val repository = ProgrammeRepository(openDatabase().programmeAttemptDao())
        val original = sampleAttempt("immutable")
        repository.record(original)
        var rejected = false
        try {
            repository.record(original.copy(response = "replacement", source = EvidenceSource.HUMAN_REVIEW))
        } catch (_: SQLiteConstraintException) {
            rejected = true
        }
        assertTrue("Duplicate IDs must fail instead of replacing or ignoring evidence", rejected)
        assertEquals(listOf(original), repository.attempts.first())
    }

    @Test
    fun `decoding rejects corrupted evidence instead of coercing scores or provenance`() {
        val row = sampleAttempt("invalid").toEntity()
        listOf(
            row.copy(scoresJson = "{\"ACCURACY\":\"4\"}"),
            row.copy(scoresJson = "{\"ACCURACY\":3.5}"),
            row.copy(scoresJson = "{\"ACCURACY\":null}"),
            row.copy(scoresJson = "{\"UNKNOWN_DIMENSION\":4}"),
            row.copy(errorsJson = "[null]"),
            row.copy(errorsJson = "[42]"),
            row.copy(source = "UNKNOWN_SOURCE"),
            row.copy(context = "UNKNOWN_CONTEXT")
        ).forEach { invalid ->
            assertThrows(IllegalArgumentException::class.java) { invalid.toAttempt() }
        }
        // Preserve invalid rubric values so mastery policy, rather than persistence, rejects them.
        val outOfRange = sampleAttempt("out-of-range").copy(scores = mapOf(Dimension.ACCURACY to 5))
        assertEquals(outOfRange, outOfRange.toEntity().toAttempt())
    }

    private fun sampleAttempt(id: String) = Attempt(
        id = id,
        unitId = "unit-ä",
        taskId = "writing",
        timestamp = 1_800_000_000_000L,
        response = "Zitat: \"Grüße\"\nZeile 2 \\ Ende 🗣",
        source = EvidenceSource.AUTOMATIC_QUIZ,
        scores = Dimension.values().mapIndexed { index, dimension -> dimension to index % 5 }.toMap(),
        assisted = true,
        unfamiliar = true,
        independentProduction = true,
        context = ContextTrack.ENGINEERING,
        errors = listOf("Komma, Semikolon; Doppelpunkt:", "\"Zitat\"\n\\", "", "Grüße 🗣", "doppelt", "doppelt")
    )

    private fun openDatabase(): C1AppDatabase = Room.databaseBuilder(
        context, C1AppDatabase::class.java, databaseName
    )
        .addMigrations(
            C1AppDatabase.MIGRATION_1_2,
            C1AppDatabase.MIGRATION_2_3,
            C1AppDatabase.MIGRATION_3_4,
            C1AppDatabase.MIGRATION_4_5,
            C1AppDatabase.MIGRATION_5_6,
            C1AppDatabase.MIGRATION_6_7,
            C1AppDatabase.MIGRATION_7_8,
            C1AppDatabase.MIGRATION_8_9,
            C1AppDatabase.MIGRATION_9_10,
            C1AppDatabase.MIGRATION_10_11,
            C1AppDatabase.MIGRATION_11_12,
            C1AppDatabase.MIGRATION_12_13,
            C1AppDatabase.MIGRATION_13_14
        )
        .allowMainThreadQueries()
        .build()
        .also { database = it }

    private fun assertIndexExists(
        database: androidx.sqlite.db.SupportSQLiteDatabase,
        table: String,
        indexName: String
    ) {
        database.query("PRAGMA index_list('$table')").use { cursor ->
            val names = mutableSetOf<String>()
            while (cursor.moveToNext()) names += cursor.getString(cursor.getColumnIndexOrThrow("name"))
            assertTrue(indexName, indexName in names)
        }
    }

    private fun createVersion4Database(): Map<String, List<List<String?>>> {
        val file = context.getDatabasePath(databaseName)
        file.parentFile?.mkdirs()
        return SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            // Frozen v4 schema: independent of the current Room-generated schema.
            db.execSQL("""
                CREATE TABLE baseline_profiles (
                    id TEXT NOT NULL PRIMARY KEY, listeningScore INTEGER NOT NULL,
                    speakingScore INTEGER NOT NULL, interactionScore INTEGER NOT NULL,
                    readingScore INTEGER NOT NULL, writingScore INTEGER NOT NULL,
                    grammarScore INTEGER NOT NULL, vocabularyScore INTEGER NOT NULL,
                    mediationScore INTEGER NOT NULL, overallC1Score INTEGER NOT NULL,
                    functionalReadinessPct INTEGER NOT NULL, primaryLimitingFactor TEXT NOT NULL,
                    recommendedStartingEngine TEXT NOT NULL, isCompleted INTEGER NOT NULL,
                    assessedAtTimestamp INTEGER NOT NULL)
            """.trimIndent())
            db.execSQL("""
                CREATE TABLE vocabulary_items (
                    id TEXT NOT NULL PRIMARY KEY, germanCollocation TEXT NOT NULL,
                    meaningDe TEXT NOT NULL, exampleSentence TEXT NOT NULL,
                    register TEXT NOT NULL, category TEXT NOT NULL, stage INTEGER NOT NULL,
                    easeFactor REAL NOT NULL, intervalDays INTEGER NOT NULL,
                    repetitions INTEGER NOT NULL, lastReviewedTimestamp INTEGER,
                    nextReviewTimestamp INTEGER NOT NULL, consecutiveErrors INTEGER NOT NULL,
                    isMastered INTEGER NOT NULL)
            """.trimIndent())
            db.execSQL("""
                CREATE TABLE proficiency_snapshots (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timestamp INTEGER NOT NULL,
                    periodLabel TEXT NOT NULL, overallScore INTEGER NOT NULL,
                    functionalScore INTEGER NOT NULL, listeningScore INTEGER NOT NULL,
                    speakingScore INTEGER NOT NULL, readingScore INTEGER NOT NULL,
                    writingScore INTEGER NOT NULL, grammarScore INTEGER NOT NULL,
                    vocabularyScore INTEGER NOT NULL)
            """.trimIndent())
            db.execSQL("""
                CREATE TABLE daily_streaks (
                    id TEXT NOT NULL PRIMARY KEY, currentStreak INTEGER NOT NULL,
                    longestStreak INTEGER NOT NULL, lastActiveEpochDay INTEGER NOT NULL,
                    streakFreezesAvailable INTEGER NOT NULL, totalSessionsCompleted INTEGER NOT NULL,
                    activeDaysBitmask INTEGER NOT NULL, lastActivityTimestamp INTEGER NOT NULL)
            """.trimIndent())
            db.execSQL("INSERT INTO baseline_profiles VALUES ('baseline', 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 'Grammatik', 'writing', 1, 1234)")
            db.execSQL("INSERT INTO vocabulary_items VALUES ('word', 'im Grunde', 'grundsätzlich', 'Im Grunde ja.', 'neutral', 'general', 1, 2.5, 7, 3, NULL, 9999, 1, 0)")
            db.execSQL("INSERT INTO proficiency_snapshots VALUES (7, 1234, 'Woche 1', 50, 51, 52, 53, 54, 55, 56, 57)")
            db.execSQL("INSERT INTO daily_streaks VALUES ('streak', 3, 7, 123, 2, 10, 5, 1234)")
            db.version = 4
            listOf("baseline_profiles", "vocabulary_items", "proficiency_snapshots", "daily_streaks")
                .associateWith { table ->
                    db.rawQuery("SELECT * FROM `$table`", null).use { cursor ->
                        val rows = mutableListOf<List<String?>>()
                        while (cursor.moveToNext()) {
                            rows.add(List(cursor.columnCount) { cursor.getString(it) })
                        }
                        rows
                    }
                }
        }
    }
}
