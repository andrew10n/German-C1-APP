package com.example.programme

import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.local.entity.DailyStreakEntity
import com.example.data.local.entity.ProficiencySnapshotEntity
import com.example.data.local.entity.VocabularyItemEntity
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ProgrammeExportTest {
    @Test
    fun exportIncludesLearnerRecordsAndExplicitlyExcludesExternalMediaAndCredentials() {
        val challenge = FeedbackChallenge(
            claimId = "claim-1",
            learnerDecision = FeedbackChallengeDecision.DISAGREE.name,
            correctedFeedback = "Alternative Erklärung",
            status = FeedbackChallengeStatus.OPEN,
            id = "challenge-1",
            attemptId = "attempt-1",
            unitId = "s1-explain",
            taskId = "writing",
            context = ContextTrack.GENERAL,
            responseExcerpt = "weil ich gehe",
            claimType = "subordinate-verb-position",
            proposedFeedback = "Prüfe die Verbposition.",
            createdAt = 100,
            updatedAt = 100
        )
        val task = DeliveredAssessmentTask(
            id = "task-1",
            unitId = "s1-explain",
            taskId = "assessment:writing",
            prompt = "Fasse die Entscheidung zusammen.",
            evidence = AssessmentEvidence.TEXT_RESPONSE,
            context = ContextTrack.GENERAL,
            unfamiliar = false,
            variationId = "original",
            taskVersion = 2,
            rubricVersion = 1
        )
        val assessment = PersistedAssessment(
            session = AssessmentSession(
                id = "session-1",
                kind = AssessmentSessionKind.WEEKLY_INTEGRATED,
                createdAt = 90,
                durationMinutes = 30,
                tasks = listOf(task)
            ),
            responses = mapOf(
                task.id to "Eigene Antwort",
                ASSESSMENT_AUDIO_RESPONSE_PREFIX + task.id to "voice.m4a"
            ),
            currentIndex = 1,
            completed = true,
            updatedAt = 120
        )
        val export = JSONObject(
            ProgrammeExport.create(
                attempts = listOf(
                    Attempt(
                        id = "attempt-1",
                        unitId = "s1-explain",
                        taskId = "writing",
                        timestamp = 80,
                        response = "Ich bleibe, weil ich gehe.",
                        source = EvidenceSource.SELF_REFLECTION,
                        scores = mapOf(Dimension.ACCURACY to 2),
                        context = ContextTrack.GENERAL,
                        errors = listOf("subordinate-verb-position"),
                        taskVersion = 2,
                        rubricVersion = 1
                    )
                ),
                experiences = emptyList(),
                assessments = listOf(assessment),
                feedbackChallenges = listOf(challenge),
                humanReviews = listOf(
                    HumanReviewSubmission(
                        id = "review-1",
                        attemptId = "attempt-1",
                        reviewer = HumanReviewer("local-reviewer", "Reviewer", verified = false),
                        rubricVersion = 1,
                        scores = Dimension.values().associateWith { 3 },
                        comment = "Text review",
                        createdAt = 110
                    )
                ),
                reviewAssignments = listOf(
                    ReviewAssignment(
                        id = "assignment-1",
                        attemptId = "attempt-1",
                        reviewerId = "local-reviewer",
                        reviewerName = "Reviewer",
                        status = "ASSIGNED",
                        createdAt = 105,
                        dueAt = 200,
                        completedAt = null
                    )
                ),
                reviewAuditEvents = listOf(
                    ReviewAuditEvent(
                        id = "audit-1",
                        assignmentId = "assignment-1",
                        reviewId = "review-1",
                        actorId = "local-reviewer",
                        action = "ASSIGNED",
                        details = "Review assigned",
                        createdAt = 105
                    )
                ),
                reassessmentLinks = listOf(
                    ReassessmentLink(
                        id = "reassessment-1",
                        sourceAttemptId = "attempt-1",
                        sourceReviewId = "review-1",
                        reassessmentUnitId = "s1-explain",
                        reassessmentTaskId = "writing",
                        status = "PENDING",
                        createdAt = 115,
                        completedAt = null
                    )
                ),
                baselineProfile = BaselineProfileEntity(
                    listeningScore = 1,
                    speakingScore = 2,
                    interactionScore = 3,
                    readingScore = 4,
                    writingScore = 5,
                    grammarScore = 6,
                    vocabularyScore = 7,
                    mediationScore = 8,
                    overallC1Score = 9,
                    functionalReadinessPct = 10,
                    primaryLimitingFactor = "Legacy",
                    recommendedStartingEngine = "Reading",
                    assessedAtTimestamp = 70
                ),
                vocabularyItems = listOf(
                    VocabularyItemEntity(
                        id = "vocabulary-1",
                        germanCollocation = "eine Entscheidung treffen",
                        meaningDe = "entscheiden",
                        exampleSentence = "Wir treffen eine Entscheidung."
                    )
                ),
                proficiencySnapshots = listOf(
                    ProficiencySnapshotEntity(
                        timestamp = 60,
                        periodLabel = "Legacy",
                        overallScore = 50,
                        functionalScore = 40,
                        listeningScore = 30,
                        speakingScore = 20,
                        readingScore = 10,
                        writingScore = 40,
                        grammarScore = 30,
                        vocabularyScore = 20
                    )
                ),
                dailyStreak = DailyStreakEntity(currentStreak = 4, longestStreak = 5),
                preferences = ProgrammePreferences(language = ProgrammeLanguage.EN, learnerName = "Mira"),
                bookmarkedUnitIds = setOf("s1-explain"),
                assistedUnitIds = setOf("s1-justify"),
                lastUnitId = "s1-explain",
                lastTaskId = "writing",
                onboardingComplete = true
            )
        )

        assertEquals("c1-deutsch-local-export-v2", export.getString("format"))
        assertEquals("attempt-1", export.getJSONArray("attempts").getJSONObject(0).getString("id"))
        assertEquals(2, export.getJSONArray("attempts").getJSONObject(0)
            .getJSONObject("scores").getInt("ACCURACY"))
        assertEquals("weil ich gehe", export.getJSONArray("feedbackChallenges")
            .getJSONObject(0).getString("responseExcerpt"))
        assertEquals("review-1", export.getJSONArray("humanReviews")
            .getJSONObject(0).getString("id"))
        assertEquals("assessment:writing", export.getJSONArray("assessmentSessions")
            .getJSONObject(0).getJSONArray("tasks").getJSONObject(0).getString("taskId"))
        assertEquals("Eigene Antwort", export.getJSONArray("assessmentSessions")
            .getJSONObject(0).getJSONObject("responses").getString("task-1"))
        assertFalse(export.getJSONArray("assessmentSessions")
            .getJSONObject(0).getJSONObject("responses")
            .has(ASSESSMENT_AUDIO_RESPONSE_PREFIX + task.id))
        assertEquals(120, export.getJSONArray("assessmentSessions")
            .getJSONObject(0).getLong("updatedAt"))
        assertEquals("Mira", export.getJSONObject("preferences").getString("learnerName"))
        assertEquals(1, export.getJSONObject("legacyData").getJSONArray("vocabularyItems").length())
        assertTrue(export.getJSONObject("legacyData").getJSONObject("baselineProfileUnvalidated")
            .getString("interpretation").contains("not validated"))
        assertFalse(export.getJSONObject("media").getBoolean("audioBytesIncluded"))
        assertFalse(export.getJSONObject("media").getBoolean("bookAudioContentUriIncluded"))
        assertFalse(export.has("apiKey"))
    }
}
