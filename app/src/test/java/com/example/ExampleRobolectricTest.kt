package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.C1AppDatabase
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.model.PriorityCalculator
import com.example.data.model.SkillCategory
import com.example.data.model.SkillScore
import com.example.data.repository.C1LearningRepository
import com.example.ui.diagnostic.DiagnosticEngine
import com.example.ui.diagnostic.DiagnosticUiState
import com.example.ui.diagnostic.DiagnosticUserAnswer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var application: Application
    private lateinit var inMemoryDb: C1AppDatabase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        application = ApplicationProvider.getApplicationContext()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, C1AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        inMemoryDb.close()
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("C1 Deutsch", appName)
    }

    @Test
    fun `test priority calculation incorporates weakness and recurring errors`() {
        val skill = SkillScore(
            category = SkillCategory.INTERAKTION,
            score = 50,
            lastTestedDaysAgo = 3,
            trend = 0,
            criticalWeakness = "Unterbrechungen parieren",
            masteredAreas = emptyList()
        )
        val rec = PriorityCalculator.calculate(
            skill = skill,
            unresolvedErrorCount = 4,
            daysSinceTrained = 3,
            functionalWeight = 10,
            fatigueScore = 3
        )
        assertTrue("Priority should be high for critical weakness with errors", rec.calculatedPriority > 70)
        assertEquals(SkillCategory.INTERAKTION, rec.category)
    }

    @Test
    fun `test Room persistence saves and retrieves baseline profile`() = runTest {
        val dao = inMemoryDb.baselineProfileDao()
        val initialProfile = BaselineProfileEntity(
            id = "primary_user_profile",
            listeningScore = 65,
            speakingScore = 55,
            interactionScore = 60,
            readingScore = 80,
            writingScore = 62,
            grammarScore = 70,
            vocabularyScore = 68,
            mediationScore = 58,
            overallC1Score = 65,
            functionalReadinessPct = 60,
            primaryLimitingFactor = "Spontanes Sprechen & Reaktionslatenz",
            recommendedStartingEngine = "Sprech- & Interaktions-Simulator",
            isCompleted = true,
            assessedAtTimestamp = 1000L
        )

        dao.insertOrUpdateProfile(initialProfile)
        val retrieved = dao.getBaselineProfile("primary_user_profile")

        assertNotNull(retrieved)
        assertEquals(65, retrieved?.listeningScore)
        assertEquals(55, retrieved?.speakingScore)
        assertEquals(80, retrieved?.readingScore)
        assertEquals("Spontanes Sprechen & Reaktionslatenz", retrieved?.primaryLimitingFactor)
        assertTrue(retrieved?.isCompleted == true)
    }

    @Test
    fun `test repeated diagnostic updates baseline profile without duplicate records`() = runTest {
        val dao = inMemoryDb.baselineProfileDao()

        val firstAssessment = BaselineProfileEntity(
            id = "primary_user_profile",
            listeningScore = 60,
            speakingScore = 50,
            interactionScore = 55,
            readingScore = 70,
            writingScore = 58,
            grammarScore = 65,
            vocabularyScore = 62,
            mediationScore = 52,
            overallC1Score = 59,
            functionalReadinessPct = 54,
            primaryLimitingFactor = "Spontanes Sprechen",
            recommendedStartingEngine = "Sprech- & Interaktions-Simulator",
            isCompleted = true,
            assessedAtTimestamp = 1000L
        )
        dao.insertOrUpdateProfile(firstAssessment)

        // User retakes diagnostic after 2 weeks of training with improved scores
        val retakeAssessment = BaselineProfileEntity(
            id = "primary_user_profile",
            listeningScore = 78,
            speakingScore = 72,
            interactionScore = 75,
            readingScore = 85,
            writingScore = 76,
            grammarScore = 82,
            vocabularyScore = 80,
            mediationScore = 74,
            overallC1Score = 78,
            functionalReadinessPct = 75,
            primaryLimitingFactor = "Präzise Registerkontrolle",
            recommendedStartingEngine = "Adaptives 18-Minuten Tagestraining",
            isCompleted = true,
            assessedAtTimestamp = 2000L
        )
        dao.insertOrUpdateProfile(retakeAssessment)

        val retrieved = dao.getBaselineProfile("primary_user_profile")
        assertNotNull(retrieved)
        assertEquals(78, retrieved?.listeningScore)
        assertEquals(72, retrieved?.speakingScore)
        assertEquals(85, retrieved?.readingScore)
        assertEquals(2000L, retrieved?.assessedAtTimestamp)
        assertEquals("Präzise Registerkontrolle", retrieved?.primaryLimitingFactor)
    }

    @Test
    fun `test complete MCQ screening reports recognition scores without claiming measured proficiency`() = runTest {
        val learningRepo = C1LearningRepository()
        val viewModel = DiagnosticEngine(application, inMemoryDb, learningRepo)

        // Caller-supplied scores and recording metadata must not become assessment evidence.
        val answers = mutableMapOf<Int, DiagnosticUserAnswer>()
        viewModel.tasks.forEachIndexed { index, task ->
            answers[index] = DiagnosticUserAnswer(
                selectedOptionIndex = task.correctOptionIndex,
                freeformText = null,
                recordedAudioLatencySeconds = 2, // swift reaction within 3s benchmark
                recordedDurationSeconds = 5,
                isEvaluatedCorrect = true,
                earnedScore = 90
            )
        }

        viewModel.completeAssessment(answers).join()

        val state = viewModel.uiState.value
        assertTrue("UI state should be Completed, was: $state", state is DiagnosticUiState.Completed)
        val completedState = state as DiagnosticUiState.Completed

        assertTrue("MCQ screening must remain provisional", completedState.isProvisional)
        assertEquals(0f, completedState.confidenceScore, 0f)
        assertEquals(8, completedState.answeredTaskCount)
        assertEquals(8, completedState.totalTaskCount)
        assertEquals(0, completedState.overallScore)
        assertEquals(0, completedState.profile.functionalReadinessPct)
        assertFalse(completedState.profile.isCompleted)
        assertNull(inMemoryDb.baselineProfileDao().getBaselineProfile())

        // 9-dimensional profile should have 9 distinct dimensions
        val profile9D = completedState.nineDimensionalProfile
        assertEquals(9, profile9D.size)
        assertTrue(profile9D.containsKey(SkillCategory.HOERVERSTEHEN))
        assertTrue(profile9D.containsKey(SkillCategory.SPONTANES_SPRECHEN))
        assertTrue(profile9D.containsKey(SkillCategory.INTERAKTION))
        assertTrue(profile9D.containsKey(SkillCategory.LESEVERSTEHEN))
        assertTrue(profile9D.containsKey(SkillCategory.SCHREIBEN))
        assertTrue(profile9D.containsKey(SkillCategory.GRAMMATIK))
        assertTrue(profile9D.containsKey(SkillCategory.WORTSCHATZ))
        assertTrue(profile9D.containsKey(SkillCategory.MEDIATION))
        assertTrue(profile9D.containsKey(SkillCategory.AUSSPRACHE_PROSODIE))

        val recognitionSkills = setOf(
            SkillCategory.HOERVERSTEHEN, SkillCategory.LESEVERSTEHEN,
            SkillCategory.GRAMMATIK, SkillCategory.WORTSCHATZ
        )
        assertEquals(recognitionSkills, completedState.assessedSkills)
        profile9D.forEach { (category, score) ->
            assertEquals(if (category in recognitionSkills) 100 else 0, score.score)
            assertTrue("MCQ answers must not imply mastery", score.masteredAreas.isEmpty())
        }
    }

    @Test
    fun `test incomplete screening reports answered recognition items without validated confidence`() = runTest {
        val learningRepo = C1LearningRepository()
        val viewModel = DiagnosticEngine(application, inMemoryDb, learningRepo)

        // Only answer 2 out of 8 tasks
        val answers = mapOf(
            0 to DiagnosticUserAnswer(
                selectedOptionIndex = viewModel.tasks[0].correctOptionIndex,
                recordedAudioLatencySeconds = 2,
                isEvaluatedCorrect = true,
                earnedScore = 90
            ),
            1 to DiagnosticUserAnswer(
                selectedOptionIndex = 0, // incorrect
                recordedAudioLatencySeconds = 4,
                isEvaluatedCorrect = false,
                earnedScore = 50
            )
        )

        viewModel.completeAssessment(answers).join()

        val state = viewModel.uiState.value
        assertTrue("UI state should be Completed, was: $state", state is DiagnosticUiState.Completed)
        val completedState = state as DiagnosticUiState.Completed

        assertTrue("Incomplete assessment must be marked provisional", completedState.isProvisional)
        assertEquals(0f, completedState.confidenceScore, 0f)
        assertEquals(2, completedState.answeredTaskCount)
        assertEquals(8, completedState.totalTaskCount)
        assertEquals(setOf(SkillCategory.HOERVERSTEHEN), completedState.assessedSkills)
        completedState.nineDimensionalProfile.forEach { (category, score) ->
            assertEquals(if (category == SkillCategory.HOERVERSTEHEN) 100 else 0, score.score)
        }
        assertTrue(completedState.summaryDe.contains("Vorläufiges Profil"))
        assertNull(inMemoryDb.baselineProfileDao().getBaselineProfile())
    }

    @Test
    fun `test completing diagnostic preserves historical error-recycling and vocabulary progress`() = runTest {
        val learningRepo = C1LearningRepository()

        // Verify initial historical learning state in repository
        val initialErrors = learningRepo.errorDatabase.value.toList()
        val initialVocabulary = learningRepo.vocabularyList.value.toList()
        val initialSkills = learningRepo.skillScores.value.toMap()
        assertTrue("Repository must have initial error records", initialErrors.isNotEmpty())
        assertTrue("Repository must have initial vocabulary items", initialVocabulary.isNotEmpty())

        val viewModel = DiagnosticEngine(application, inMemoryDb, learningRepo)

        val answers = mutableMapOf<Int, DiagnosticUserAnswer>()
        viewModel.tasks.forEachIndexed { index, task ->
            answers[index] = DiagnosticUserAnswer(
                selectedOptionIndex = task.correctOptionIndex,
                recordedAudioLatencySeconds = 2,
                isEvaluatedCorrect = true,
                earnedScore = 85
            )
        }

        viewModel.completeAssessment(answers).join()

        // Ensure error database and vocabulary progress were strictly preserved
        assertEquals(
            "Historical error database must not be wiped by new diagnostic",
            initialErrors,
            learningRepo.errorDatabase.value
        )
        assertEquals(
            "Vocabulary items must not be wiped by new diagnostic",
            initialVocabulary,
            learningRepo.vocabularyList.value
        )

        assertTrue(viewModel.uiState.value is DiagnosticUiState.Completed)
        assertEquals("Session-only screening must preserve live skills", initialSkills, learningRepo.skillScores.value)
        assertNull(inMemoryDb.baselineProfileDao().getBaselineProfile())
    }

    @Test
    fun `test selection latency cannot manufacture pronunciation or speaking evidence`() = runTest {
        val learningRepo = C1LearningRepository()
        val viewModel = DiagnosticEngine(application, inMemoryDb, learningRepo)

        // A model-utterance selection is not an observed speaking sample.
        val fastSpeakingAnswers = mapOf(
            1 to DiagnosticUserAnswer(
                selectedOptionIndex = 1,
                recordedAudioLatencySeconds = 2,
                isEvaluatedCorrect = true,
                earnedScore = 90
            )
        )
        viewModel.completeAssessment(fastSpeakingAnswers).join()
        val stateA = viewModel.uiState.value as DiagnosticUiState.Completed
        assertEquals(0, stateA.nineDimensionalProfile.getValue(SkillCategory.AUSSPRACHE_PROSODIE).score)
        assertEquals(0, stateA.nineDimensionalProfile.getValue(SkillCategory.SPONTANES_SPRECHEN).score)
        assertTrue(stateA.assessedSkills.isEmpty())
        assertEquals(1, stateA.answeredTaskCount)

        // Changing the claimed latency must not change the screening profile.
        val slowSpeakingAnswers = mapOf(
            1 to DiagnosticUserAnswer(
                selectedOptionIndex = 1,
                recordedAudioLatencySeconds = 6,
                isEvaluatedCorrect = true,
                earnedScore = 90
            )
        )
        viewModel.completeAssessment(slowSpeakingAnswers).join()
        val stateB = viewModel.uiState.value as DiagnosticUiState.Completed
        assertEquals(stateA.nineDimensionalProfile, stateB.nineDimensionalProfile)
        assertTrue(stateB.assessedSkills.isEmpty())
        assertEquals(1, stateB.answeredTaskCount)
        assertEquals(0f, stateB.confidenceScore, 0f)
        assertNull(inMemoryDb.baselineProfileDao().getBaselineProfile())
    }

    @Test
    fun `test Room persistence saves and queries due vocabulary items tracking review timestamps`() = runTest {
        val vocabDao = inMemoryDb.vocabularyItemDao()
        val now = 1_000_000L

        val dueItem = com.example.data.local.entity.VocabularyItemEntity(
            id = "voc_due",
            germanCollocation = "etw. zur Sprache bringen",
            meaningDe = "Ein heikles Thema ansprechen",
            exampleSentence = "Sie brachte das Thema zur Sprache.",
            stage = 1,
            intervalDays = 1,
            repetitions = 1,
            lastReviewedTimestamp = now - 86_400_000L,
            nextReviewTimestamp = now - 1000L // Overdue
        )

        val futureItem = com.example.data.local.entity.VocabularyItemEntity(
            id = "voc_future",
            germanCollocation = "in Erwägung ziehen",
            meaningDe = "Eine Option prüfen",
            exampleSentence = "Wir ziehen das in Erwägung.",
            stage = 3,
            intervalDays = 7,
            repetitions = 3,
            lastReviewedTimestamp = now,
            nextReviewTimestamp = now + 7 * 86_400_000L // Due in 7 days
        )

        vocabDao.insertOrUpdate(dueItem)
        vocabDao.insertOrUpdate(futureItem)

        // Query due items where nextReviewTimestamp <= now
        val dueList = vocabDao.getDueVocabularyDirect(now)
        assertEquals(1, dueList.size)
        assertEquals("voc_due", dueList[0].id)
        assertEquals(now - 86_400_000L, dueList[0].lastReviewedTimestamp)
        assertEquals(now - 1000L, dueList[0].nextReviewTimestamp)

        // Query total
        val total = vocabDao.getTotalCount()
        assertEquals(2, total)
    }

    @Test
    fun `test SM2 algorithm schedules next review timestamp and calculates intervals`() = runTest {
        val vocabDao = inMemoryDb.vocabularyItemDao()
        val srsRepo = com.example.data.repository.VocabularySrsRepository(vocabDao)
        val initialTime = 10_000_000L

        val item = com.example.data.local.entity.VocabularyItemEntity(
            id = "voc_test_sm2",
            germanCollocation = "Abstriche machen",
            meaningDe = "Kompromisse hinnehmen",
            exampleSentence = "Wir machen keine Abstriche.",
            stage = 0,
            intervalDays = 0,
            repetitions = 0,
            lastReviewedTimestamp = null,
            nextReviewTimestamp = initialTime
        )
        vocabDao.insertOrUpdate(item)

        // Review 1: Good (Quality 2)
        val review1Time = initialTime + 100L
        val updated1 = srsRepo.recordReview("voc_test_sm2", qualityScore = 2, reviewTimestamp = review1Time)
        assertNotNull(updated1)
        assertEquals(1, updated1?.repetitions)
        assertEquals(1, updated1?.intervalDays)
        assertEquals(review1Time, updated1?.lastReviewedTimestamp)
        assertEquals(review1Time + 86_400_000L, updated1?.nextReviewTimestamp)

        // Review 2: Easy (Quality 3)
        val review2Time = updated1!!.nextReviewTimestamp
        val updated2 = srsRepo.recordReview("voc_test_sm2", qualityScore = 3, reviewTimestamp = review2Time)
        assertNotNull(updated2)
        assertEquals(2, updated2?.repetitions)
        assertEquals(4, updated2?.intervalDays)
        assertEquals(review2Time, updated2?.lastReviewedTimestamp)
        assertEquals(review2Time + 4 * 86_400_000L, updated2?.nextReviewTimestamp)
    }

    @Test
    fun `test failed review resets interval to 1 day and increments consecutive errors`() = runTest {
        val vocabDao = inMemoryDb.vocabularyItemDao()
        val srsRepo = com.example.data.repository.VocabularySrsRepository(vocabDao)
        val initialTime = 20_000_000L

        val advancedItem = com.example.data.local.entity.VocabularyItemEntity(
            id = "voc_advanced",
            germanCollocation = "ins Gewicht fallen",
            meaningDe = "Ausschlaggebend sein",
            exampleSentence = "Das fällt schwer ins Gewicht.",
            stage = 3,
            intervalDays = 7,
            repetitions = 3,
            lastReviewedTimestamp = initialTime - 7 * 86_400_000L,
            nextReviewTimestamp = initialTime
        )
        vocabDao.insertOrUpdate(advancedItem)

        // Quality 0 (Blackout) -> must reset interval to 1 day, repetitions to 0, stage to 1
        val failed = srsRepo.recordReview("voc_advanced", qualityScore = 0, reviewTimestamp = initialTime)
        assertNotNull(failed)
        assertEquals(0, failed?.repetitions)
        assertEquals(1, failed?.intervalDays)
        assertEquals(1, failed?.consecutiveErrors)
        assertEquals(initialTime, failed?.lastReviewedTimestamp)
        assertEquals(initialTime + 86_400_000L, failed?.nextReviewTimestamp)
    }

    @Test
    fun `course expressions seed into the shared vocabulary retrieval queue idempotently`() = runTest {
        val repository = com.example.data.repository.VocabularySrsRepository(
            inMemoryDb.vocabularyItemDao()
        )
        val seededAt = 50_000L

        val unitId = com.example.programme.CurriculumCatalogue.units
            .first { it.content != null }.id
        val expectedExpressions = com.example.programme.CurriculumCatalogue.units
            .first { it.id == unitId }.content!!.expressions.size
        val inserted = repository.seedCurriculumExpressions(unitId, seededAt)
        assertEquals(expectedExpressions, inserted)
        val cards = inMemoryDb.vocabularyItemDao().getAllVocabularyFlow().first()
        val courseCards = cards.filter { it.sourceUnitId.isNotBlank() }
        assertEquals(expectedExpressions, courseCards.size)
        assertEquals(unitId, courseCards.first().sourceUnitId)
        assertTrue(courseCards.all { it.category == "Kursausdruck & Kollokation" })
        assertTrue(courseCards.all { it.nextReviewTimestamp == seededAt })

        val reviewed = courseCards.first().copy(
            repetitions = 1,
            intervalDays = 1,
            nextReviewTimestamp = seededAt + 86_400_000L
        )
        inMemoryDb.vocabularyItemDao().insertOrUpdate(reviewed)
        assertEquals(0, repository.seedCurriculumExpressions(unitId, seededAt + 100))

        val restored = inMemoryDb.vocabularyItemDao().getVocabularyById(reviewed.id)
        assertEquals(reviewed, restored)
    }

    @Test
    fun `test VocabularySrsViewModel review grading workflow updates active card and timestamps`() = runTest {
        val vocabDao = inMemoryDb.vocabularyItemDao()
        val srsRepo = com.example.data.repository.VocabularySrsRepository(vocabDao)
        val now = System.currentTimeMillis()

        // Seed 2 due items
        val item1 = com.example.data.local.entity.VocabularyItemEntity(
            id = "item_1",
            germanCollocation = "zur Sprache bringen",
            meaningDe = "Ansprechen",
            exampleSentence = "Sie sprach es an.",
            nextReviewTimestamp = now - 5000L
        )
        val item2 = com.example.data.local.entity.VocabularyItemEntity(
            id = "item_2",
            germanCollocation = "in Erwägung ziehen",
            meaningDe = "Abwägen",
            exampleSentence = "Wir wägen ab.",
            nextReviewTimestamp = now - 1000L
        )
        vocabDao.insertOrUpdate(item1)
        vocabDao.insertOrUpdate(item2)

        val viewModel = com.example.ui.vocabulary.VocabularySrsViewModel(application, inMemoryDb, srsRepo)
        viewModel.initializeDatabase().join()

        val cardState = viewModel.cardState.value
        assertNotNull(cardState.currentCard)
        assertEquals("item_1", cardState.currentCard?.id)
        assertFalse(cardState.isRevealed)

        // Reveal card
        viewModel.revealCard()
        assertTrue(viewModel.cardState.value.isRevealed)

        // Submit review (Quality 2 - Good)
        viewModel.submitReview(qualityScore = 2).join()

        // Next due card should now be active
        val updatedCardState = viewModel.cardState.value
        assertEquals("item_2", updatedCardState.currentCard?.id)
        assertFalse(updatedCardState.isRevealed)

        // Item 1 should have updated timestamps in Room
        val reviewedItem1 = vocabDao.getVocabularyById("item_1")
        assertNotNull(reviewedItem1?.lastReviewedTimestamp)
        assertTrue(reviewedItem1!!.nextReviewTimestamp > System.currentTimeMillis())
    }

    @Test
    fun `test proficiency snapshot room persistence and chronological order`() = runTest {
        val snapshotDao = inMemoryDb.proficiencySnapshotDao()
        val now = System.currentTimeMillis()

        val snap1 = com.example.data.local.entity.ProficiencySnapshotEntity(
            timestamp = now - 20000L,
            periodLabel = "Basis-Diagnostik",
            overallScore = 62,
            functionalScore = 58,
            listeningScore = 65,
            speakingScore = 55,
            readingScore = 68,
            writingScore = 60,
            grammarScore = 64,
            vocabularyScore = 59
        )
        val snap2 = com.example.data.local.entity.ProficiencySnapshotEntity(
            timestamp = now - 10000L,
            periodLabel = "Woche 2",
            overallScore = 70,
            functionalScore = 68,
            listeningScore = 72,
            speakingScore = 65,
            readingScore = 75,
            writingScore = 66,
            grammarScore = 72,
            vocabularyScore = 70
        )
        val snap3 = com.example.data.local.entity.ProficiencySnapshotEntity(
            timestamp = now,
            periodLabel = "Aktuell",
            overallScore = 81,
            functionalScore = 79,
            listeningScore = 84,
            speakingScore = 78,
            readingScore = 82,
            writingScore = 77,
            grammarScore = 80,
            vocabularyScore = 85
        )

        snapshotDao.insertSnapshot(snap1)
        snapshotDao.insertSnapshot(snap2)
        snapshotDao.insertSnapshot(snap3)

        assertEquals(3, snapshotDao.getSnapshotCount())
        val allSnapshots = snapshotDao.getAllSnapshotsDirect()
        assertEquals(3, allSnapshots.size)
        assertEquals("Basis-Diagnostik", allSnapshots[0].periodLabel)
        assertEquals("Woche 2", allSnapshots[1].periodLabel)
        assertEquals("Aktuell", allSnapshots[2].periodLabel)

        val latest = snapshotDao.getLatestSnapshot()
        assertNotNull(latest)
        assertEquals("Aktuell", latest?.periodLabel)
        assertEquals(81, latest?.overallScore)
        assertEquals(79, latest?.functionalScore)
    }

    @Test
    fun `test proficiency progress repository does not fabricate history from baseline`() = runTest {
        val snapshotDao = inMemoryDb.proficiencySnapshotDao()
        val baselineDao = inMemoryDb.baselineProfileDao()
        val vocabDao = inMemoryDb.vocabularyItemDao()

        // Insert baseline profile
        val baseline = BaselineProfileEntity(
            listeningScore = 66,
            speakingScore = 58,
            interactionScore = 62,
            readingScore = 70,
            writingScore = 62,
            grammarScore = 65,
            vocabularyScore = 63,
            mediationScore = 60,
            overallC1Score = 80,
            functionalReadinessPct = 74,
            primaryLimitingFactor = "Spontanes Sprechen",
            recommendedStartingEngine = "ConversationSimulator",
            assessedAtTimestamp = System.currentTimeMillis() - 100000L
        )
        baselineDao.insertOrUpdateProfile(baseline)

        val repository = com.example.data.repository.ProficiencyProgressRepository(snapshotDao, baselineDao, vocabDao)
        repository.seedHistoricalProficiencyIfEmpty()

        assertTrue(snapshotDao.getAllSnapshotsDirect().isEmpty())
        assertEquals(0, snapshotDao.getSnapshotCount())
        assertNull(repository.getLatestSnapshot())
        assertEquals(baseline, baselineDao.getBaselineProfile())

        val suppliedSnapshot = com.example.data.local.entity.ProficiencySnapshotEntity(
            timestamp = baseline.assessedAtTimestamp,
            periodLabel = "Recorded assessment",
            overallScore = baseline.overallC1Score,
            functionalScore = baseline.functionalReadinessPct,
            listeningScore = baseline.listeningScore,
            speakingScore = baseline.speakingScore,
            readingScore = baseline.readingScore,
            writingScore = baseline.writingScore,
            grammarScore = baseline.grammarScore,
            vocabularyScore = baseline.vocabularyScore
        )
        repository.insertSnapshot(suppliedSnapshot)
        val recordedHistory = snapshotDao.getAllSnapshotsDirect()
        assertEquals(1, recordedHistory.size)
        assertEquals(suppliedSnapshot.overallScore, recordedHistory.single().overallScore)
        assertEquals(suppliedSnapshot.timestamp, recordedHistory.single().timestamp)
        repository.seedHistoricalProficiencyIfEmpty()
        assertEquals("Initialization must preserve supplied history", recordedHistory, snapshotDao.getAllSnapshotsDirect())
    }

    @Test
    fun `test proficiency progress view model filters metrics and timeframes`() = runTest {
        val snapshotDao = inMemoryDb.proficiencySnapshotDao()
        val baselineDao = inMemoryDb.baselineProfileDao()
        val vocabDao = inMemoryDb.vocabularyItemDao()
        val repository = com.example.data.repository.ProficiencyProgressRepository(snapshotDao, baselineDao, vocabDao)
        val now = System.currentTimeMillis()
        val fixtures = (1..6).map { week ->
            com.example.data.local.entity.ProficiencySnapshotEntity(
                timestamp = now - (7 - week) * 7 * 86_400_000L,
                periodLabel = "Recorded week $week",
                overallScore = 60 + week,
                functionalScore = 50 + week,
                listeningScore = 65,
                speakingScore = 55,
                readingScore = 68,
                writingScore = 60,
                grammarScore = 64,
                vocabularyScore = 59
            )
        }
        fixtures.forEach { repository.insertSnapshot(it) }
        val recordedHistory = snapshotDao.getAllSnapshotsDirect()

        val viewModel = com.example.ui.progress.ProficiencyProgressViewModel(
            application = application,
            appDatabase = inMemoryDb,
            repository = repository
        )
        viewModel.initializeData().join()

        val state = viewModel.uiState.first { it.isInitialized }
        assertTrue(state.isInitialized)
        assertEquals(recordedHistory, state.snapshots)
        assertEquals(recordedHistory, state.filteredSnapshots)
        assertEquals(com.example.ui.progress.ProficiencyMetric.OVERALL_C1, state.selectedMetric)
        assertEquals(com.example.ui.progress.ProgressTimeframe.ALL, state.selectedTimeframe)

        // Switch to 4 weeks filter
        viewModel.selectTimeframe(com.example.ui.progress.ProgressTimeframe.LAST_4_WEEKS)
        val state4w = viewModel.uiState.first { it.selectedTimeframe == com.example.ui.progress.ProgressTimeframe.LAST_4_WEEKS }
        assertEquals(com.example.ui.progress.ProgressTimeframe.LAST_4_WEEKS, state4w.selectedTimeframe)
        assertEquals(recordedHistory.takeLast(4), state4w.filteredSnapshots)

        // Switch to 2 weeks filter
        viewModel.selectTimeframe(com.example.ui.progress.ProgressTimeframe.LAST_2_WEEKS)
        val state2w = viewModel.uiState.first { it.selectedTimeframe == com.example.ui.progress.ProgressTimeframe.LAST_2_WEEKS }
        assertEquals(com.example.ui.progress.ProgressTimeframe.LAST_2_WEEKS, state2w.selectedTimeframe)
        assertEquals(recordedHistory.takeLast(2), state2w.filteredSnapshots)

        // Switch metric
        viewModel.selectMetric(com.example.ui.progress.ProficiencyMetric.WORTSCHATZ)
        val stateMetric = viewModel.uiState.first { it.selectedMetric == com.example.ui.progress.ProficiencyMetric.WORTSCHATZ }
        assertEquals(com.example.ui.progress.ProficiencyMetric.WORTSCHATZ, stateMetric.selectedMetric)

        // Record a new snapshot via ViewModel
        viewModel.recordNewProficiencySnapshot("Woche 7", 88, 85).join()
        val stateUpdated = viewModel.uiState.first { it.snapshots.size == 7 }
        assertEquals(7, stateUpdated.snapshots.size)
        val updatedCount = snapshotDao.getSnapshotCount()
        assertEquals(7, updatedCount)
        assertEquals(recordedHistory, stateUpdated.snapshots.dropLast(1))
        val latest = stateUpdated.snapshots.last()
        assertEquals("Woche 7", latest.periodLabel)
        assertEquals(88, latest.overallScore)
        assertEquals(85, latest.functionalScore)
        assertEquals(stateUpdated.snapshots.takeLast(2), stateUpdated.filteredSnapshots)
        assertEquals(com.example.ui.progress.ProficiencyMetric.WORTSCHATZ, stateUpdated.selectedMetric)
    }

    @Test
    fun `test MainApplication initializes Room database and repositories for ViewModel DI`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        if (app is MainApplication) {
            assertNotNull(app.database)
            assertNotNull(app.diagnosticRepository)
            assertNotNull(app.vocabularySrsRepository)
            assertNotNull(app.proficiencyProgressRepository)
            assertNotNull(app.learningRepository)
        }
    }

    @Test
    fun `test ViewModels default constructor resolves DI without throwing exceptions`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()

        val srsVm = com.example.ui.vocabulary.VocabularySrsViewModel(app)
        assertNotNull(srsVm.repository)
        srsVm.initializeDatabase().join()

        val progressVm = com.example.ui.progress.ProficiencyProgressViewModel(app)
        assertNotNull(progressVm.repository)
        progressVm.initializeData().join()

        val diagVm = com.example.ui.diagnostic.DiagnosticEngine(app)
        assertNotNull(diagVm.learningRepository)
        diagVm.loadPersistedProfile().join()

        val c1Vm = com.example.ui.C1ViewModel(app)
        assertNotNull(c1Vm.repository)
    }

    @Test
    fun `test grammar topics progress model and filter thresholds`() {
        val sampleTopics = listOf(
            com.example.ui.components.GrammarTopicProgress(
                id = "passiv_ersatz",
                title = "Passiversatzformen",
                shortName = "Passiversatz",
                category = "Syntax",
                progressPercentage = 74,
                formulaExample = "sein + zu + Inf",
                cefrTargetLevel = "C1.1",
                completedExercises = 37,
                totalExercises = 50,
                commonMistake = "Verwechslung mit Aktiv"
            ),
            com.example.ui.components.GrammarTopicProgress(
                id = "partizipial_attribute",
                title = "Erweiterte Partizipialattribute",
                shortName = "Partizipien",
                category = "Syntax",
                progressPercentage = 58,
                formulaExample = "Partizip I/II",
                cefrTargetLevel = "C1.2",
                completedExercises = 23,
                totalExercises = 40,
                commonMistake = "Deklinationsendung"
            ),
            com.example.ui.components.GrammarTopicProgress(
                id = "genitiv_praepositionen",
                title = "Präpositionen mit Genitiv",
                shortName = "Genitiv-Präp.",
                category = "Rektion",
                progressPercentage = 92,
                formulaExample = "anlässlich des Jubiläums",
                cefrTargetLevel = "C2",
                completedExercises = 46,
                totalExercises = 50,
                commonMistake = "Dativ-Ausweichung"
            )
        )

        val focusTopics = sampleTopics.filter { it.progressPercentage < 70 }
        assertEquals(1, focusTopics.size)
        assertEquals("partizipial_attribute", focusTopics.first().id)

        val masteredTopics = sampleTopics.filter { it.progressPercentage >= 80 }
        assertEquals(1, masteredTopics.size)
        assertEquals("genitiv_praepositionen", masteredTopics.first().id)

        val averageScore = sampleTopics.map { it.progressPercentage }.average().toInt()
        assertEquals(74, averageScore) // (74 + 58 + 92) / 3 = 224 / 3 = 74
    }

    @Test
    fun `test native listening audio clips and playback helper`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val speechHelper = com.example.service.AudioSpeechHelper(app)

        assertFalse(speechHelper.isPlaying.value)
        speechHelper.speak("Test Satz auf C1 Niveau", 1.25f, "test_utterance")
        speechHelper.stop()
        assertFalse(speechHelper.isPlaying.value)

        val testClip = com.example.data.model.ListeningLaboratoryItem(
            id = "test_clip_1",
            title = "Werkstatt Test",
            environment = "Technik",
            accentOrDialect = "Schwäbisch",
            nativeSpeechSpeed = 1.25f,
            durationSeconds = 14,
            audioSpeakerText = "Also pass amol uff, Kerle!",
            blindComprehensionQuestions = listOf(
                com.example.data.model.InferenceQuestion(
                    question = "Welche Absicht hat der Sprecher?",
                    options = listOf("Fehlerkorrektur", "Lob", "Urlaub"),
                    correctIndex = 0,
                    rationaleWhyOthersAreWrong = "Er ärgert sich über Schlampigkeit."
                )
            ),
            transcriptWithReductions = "Also pass [amol uff], Kerle!",
            reductionNotes = listOf(
                com.example.data.model.ReductionAnnotation("amol uff", "einmal auf", "Schwäbisch")
            ),
            contextAndSubtextAnalysis = "Dringlichkeit in der Montage."
        )

        assertTrue(testClip.nativeSpeechSpeed >= 1.0f)
        assertTrue(testClip.durationSeconds > 0)
        assertEquals(1, testClip.blindComprehensionQuestions.size)
        assertEquals(0, testClip.blindComprehensionQuestions[0].correctIndex)
    }

    @Test
    fun `test DailyStreak Room persistence and retrieval`() = runTest {
        val streakDao = inMemoryDb.dailyStreakDao()
        val todayEpoch = java.time.LocalDate.now().toEpochDay()

        val entity = com.example.data.local.entity.DailyStreakEntity(
            id = com.example.data.local.entity.DailyStreakEntity.PRIMARY_STREAK_ID,
            currentStreak = 6,
            longestStreak = 14,
            lastActiveEpochDay = todayEpoch,
            streakFreezesAvailable = 2,
            totalSessionsCompleted = 25,
            activeDaysBitmask = 15
        )

        streakDao.insertOrUpdate(entity)
        val retrieved = streakDao.getStreak()

        assertNotNull(retrieved)
        assertEquals(6, retrieved?.currentStreak)
        assertEquals(14, retrieved?.longestStreak)
        assertEquals(todayEpoch, retrieved?.lastActiveEpochDay)
        assertEquals(2, retrieved?.streakFreezesAvailable)
        assertEquals(25, retrieved?.totalSessionsCompleted)
    }

    @Test
    fun `test DailyStreakRepository recordActivity maintains streak on same day`() = runTest {
        val streakDao = inMemoryDb.dailyStreakDao()
        val todayEpoch = java.time.LocalDate.now().toEpochDay()

        val initial = com.example.data.local.entity.DailyStreakEntity(
            currentStreak = 5,
            longestStreak = 8,
            lastActiveEpochDay = todayEpoch,
            totalSessionsCompleted = 10
        )
        streakDao.insertOrUpdate(initial)

        val repo = com.example.data.repository.DailyStreakRepository(streakDao)
        val updated = repo.recordActivity("18-Minuten Session")

        assertEquals(5, updated.currentStreak) // unchanged on same day
        assertEquals(8, updated.longestStreak)
        assertEquals(11, updated.totalSessionsCompleted)
        assertTrue(updated.isPracticedToday)
    }

    @Test
    fun `test DailyStreakRepository recordActivity increments streak on consecutive day`() = runTest {
        val streakDao = inMemoryDb.dailyStreakDao()
        val todayEpoch = java.time.LocalDate.now().toEpochDay()
        val yesterdayEpoch = todayEpoch - 1L

        val yesterdayStreak = com.example.data.local.entity.DailyStreakEntity(
            currentStreak = 6,
            longestStreak = 6,
            lastActiveEpochDay = yesterdayEpoch,
            totalSessionsCompleted = 15,
            streakFreezesAvailable = 1
        )
        streakDao.insertOrUpdate(yesterdayStreak)

        val repo = com.example.data.repository.DailyStreakRepository(streakDao)
        val updated = repo.recordActivity("Hör-Labor")

        assertEquals(7, updated.currentStreak) // 6 + 1
        assertEquals(7, updated.longestStreak)
        assertEquals(16, updated.totalSessionsCompleted)
        assertEquals(2, updated.streakFreezesAvailable) // Bonus freeze awarded at 7-day milestone
        assertTrue(updated.isPracticedToday)
    }

    @Test
    fun `test DailyStreakRepository streak freeze consumes freeze and preserves streak on 2-day gap`() = runTest {
        val streakDao = inMemoryDb.dailyStreakDao()
        val todayEpoch = java.time.LocalDate.now().toEpochDay()
        val twoDaysAgoEpoch = todayEpoch - 2L

        val frozenStreak = com.example.data.local.entity.DailyStreakEntity(
            currentStreak = 10,
            longestStreak = 12,
            lastActiveEpochDay = twoDaysAgoEpoch,
            streakFreezesAvailable = 1,
            totalSessionsCompleted = 20
        )
        streakDao.insertOrUpdate(frozenStreak)

        val repo = com.example.data.repository.DailyStreakRepository(streakDao)
        val updated = repo.recordActivity("Sprech-Simulator")

        assertEquals(11, updated.currentStreak) // Saved and incremented
        assertEquals(0, updated.streakFreezesAvailable) // Freeze consumed
        assertEquals(21, updated.totalSessionsCompleted)
        assertTrue(updated.isPracticedToday)
    }

    @Test
    fun `test PronunciationAnalysisEngine scores accurate German speech with C1 high band`() {
        val engine = com.example.data.engine.PronunciationAnalysisEngine()
        val prompt = com.example.data.model.SpeakingPrompt(
            id = "test_prompt",
            title = "Test Prompt",
            targetSentence = "Wir sollten alternative Antriebstechnologien ernsthaft in Betracht ziehen.",
            category = "Nomen-Verb-Verbindungen",
            contextBrief = "Test",
            difficultyLevel = "C1.1",
            phoneticFocusWords = listOf(
                com.example.data.model.PhoneticWordTip("Betracht", "[bəˈtʁaxt]", "Ach-Laut", "Gaumensegel")
            )
        )

        val spokenExact = "Wir sollten alternative Antriebstechnologien ernsthaft in Betracht ziehen."
        val result = engine.analyzePronunciation(prompt, spokenExact, confidence = 0.95f)

        assertTrue("Accurate speech must achieve >= 90 score, was: ${result.overallScore}", result.overallScore >= 90)
        assertEquals(100, result.accuracyScore)
        assertTrue(result.cefrBandDe.contains("C1"))
        assertEquals(prompt.targetSentence.split(" ").size, result.wordResults.size)
        assertTrue(result.wordResults.all { it.status == com.example.data.model.WordPronunciationStatus.PERFECT })
    }

    @Test
    fun `test PronunciationAnalysisEngine detects mispronounced and omitted words`() {
        val engine = com.example.data.engine.PronunciationAnalysisEngine()
        val prompt = com.example.data.model.SpeakingPrompt(
            id = "test_prompt_2",
            title = "Test Prompt 2",
            targetSentence = "Dass die Umstellung erhebliche Ressourcen erfordert, steht außer Zweifel.",
            category = "Debatte",
            contextBrief = "Test",
            difficultyLevel = "C1.1",
            phoneticFocusWords = emptyList()
        )

        // User says "Dass die Umstellung große Probleme erfordert" and omits the rest
        val spokenFlawed = "Dass die Umstellung große Probleme erfordert"
        val result = engine.analyzePronunciation(prompt, spokenFlawed, confidence = 0.70f)

        assertTrue("Flawed and incomplete speech must score lower, was: ${result.overallScore}", result.overallScore < 75)
        val missingCount = result.wordResults.count { it.status == com.example.data.model.WordPronunciationStatus.MISSING }
        assertTrue("Must detect missing words at end of sentence", missingCount > 0)
        assertTrue(result.actionableTipsDe.isNotEmpty())
    }

    @Test
    fun `test SpeakingPracticeRepository cycles prompts and stores latest analysis result`() {
        val repo = com.example.data.repository.SpeakingPracticeRepository()
        val prompts = repo.prompts.value

        assertTrue("Repository must load initial C1 speaking prompts", prompts.size >= 4)
        assertEquals(0, repo.currentPromptIndex.value)

        repo.nextPrompt()
        assertEquals(1, repo.currentPromptIndex.value)

        val prompt = prompts[1]
        val result = repo.analyzeSpokenResponse(prompt, prompt.targetSentence, 0.90f)

        assertNotNull(repo.latestAnalysisResult.value)
        assertEquals(result.promptId, repo.latestAnalysisResult.value?.promptId)
        assertTrue(result.overallScore >= 85)

        repo.clearResult()
        assertNull(repo.latestAnalysisResult.value)
    }
}



