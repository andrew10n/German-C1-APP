package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssessmentDeliveryTest {
    @Test
    fun sessionsUseOriginalMaterialAndNeverProtectedExamContent() {
        val session = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.GOETHE_STYLE,
            now = 100,
            seed = 2
        )

        assertEquals(4, session.tasks.size)
        assertTrue(session.tasks.all { it.taskId.startsWith("assessment:") })
        assertTrue(session.tasks.all { it.prompt.isNotBlank() })
        assertTrue(session.tasks.none { it.taskId.contains("official", true) })
        assertTrue(session.tasks.all { it.evidence == AssessmentEvidence.TEXT_RESPONSE })
        assertTrue(session.tasks.none { it.unfamiliar })
        assertEquals(30, session.durationMinutes)
    }

    @Test
    fun audioPracticeUsesOwnedListeningInputAndRecordedSpeakingOutputWithoutClaimingNovelty() {
        val listening = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.LISTENING_PRACTICE, now = 100, seed = 2
        )
        val speaking = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.SPEAKING_PRACTICE, now = 100, seed = 2
        )

        assertEquals(AssessmentEvidence.AUDIO_RESPONSE, listening.tasks.single().evidence)
        assertTrue(listening.tasks.single().prompt.contains("selbst ausgewählte"))
        assertFalse(listening.tasks.single().unfamiliar)
        assertEquals(AssessmentEvidence.AUDIO_RESPONSE, speaking.tasks.single().evidence)
        assertTrue(speaking.tasks.single().prompt.isNotBlank())
        assertFalse(speaking.tasks.single().unfamiliar)
        assertTrue(listOf(listening, speaking).flatMap { it.tasks }.all { it.taskVersion >= 3 })
    }

    @Test
    fun differentSeedsDeliverDifferentConditionsWithVersionedProvenance() {
        val first = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.DELAYED_CHECKPOINT, now = 100, seed = 0
        )
        val second = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.DELAYED_CHECKPOINT, now = 100, seed = 3
        )

        assertTrue(first.tasks.zip(second.tasks).any { (a, b) -> a.variationId != b.variationId })
        assertTrue(first.tasks.all { it.prompt.contains("Variation") })
        assertTrue(first.tasks.zip(second.tasks).all { (a, b) -> a.taskId != b.taskId })
        assertTrue(first.tasks.all { it.taskVersion >= 2 && it.rubricVersion == 1 })
    }

    @Test
    fun defaultVariationChangesByUtcDay() {
        val first = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.DELAYED_CHECKPOINT, now = 0
        )
        val nextDay = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.DELAYED_CHECKPOINT, now = 86_400_000L
        )

        assertTrue(first.tasks.zip(nextDay.tasks).any { (a, b) -> a.variationId != b.variationId })
    }

    @Test
    fun pilotComparisonCountsIndependentAndUnfamiliarEvidenceSeparately() {
        val attempts = listOf(
            attempt("baseline", 10, independent = false, unfamiliar = false),
            attempt("final", 20, independent = true, unfamiliar = true)
        )

        val comparison = AssessmentDeliveryEngine.comparePilot(attempts, 10, 20)

        assertEquals(1, comparison.baselineResponses)
        assertEquals(1, comparison.finalResponses)
        assertEquals(0, comparison.baselineIndependent)
        assertEquals(1, comparison.finalIndependent)
        assertEquals(0, comparison.baselineUnfamiliar)
        assertEquals(1, comparison.finalUnfamiliar)
    }

    @Test
    fun assessmentTimerNeverReturnsNegativeMinutes() {
        val session = AssessmentDeliveryEngine.createSession(
            AssessmentSessionKind.PILOT_WEEK,
            now = 1_000
        )

        assertEquals(30, AssessmentDeliveryEngine.remainingMinutes(session, 1_000))
        assertEquals(30, AssessmentDeliveryEngine.remainingMinutes(session, 1_001))
        assertEquals(0, AssessmentDeliveryEngine.remainingMinutes(session, 1_000 + 30 * 60_000L))
        assertEquals(0, AssessmentDeliveryEngine.remainingMinutes(session, Long.MAX_VALUE))
    }

    @Test
    fun delayedCheckpointScheduleAdvancesFromTheFirstQualifyingAttempt() {
        val day = MasteryEngine.DAY_MILLIS
        val first = delayedAttempt("first", 0)
        assertEquals(day, AssessmentDeliveryEngine.nextDelayedCheckpoint(
            listOf(first), DiagnosticDimension.SITUATIONAL_MEANING, now = 0
        ))
        assertEquals(3 * day, AssessmentDeliveryEngine.nextDelayedCheckpoint(
            listOf(first, delayedAttempt("day-1", day)),
            DiagnosticDimension.SITUATIONAL_MEANING,
            now = day
        ))
        assertEquals(7 * day, AssessmentDeliveryEngine.nextDelayedCheckpoint(
            listOf(first, delayedAttempt("day-1", day), delayedAttempt("day-3", 3 * day)),
            DiagnosticDimension.SITUATIONAL_MEANING,
            now = 3 * day
        ))
        assertEquals(21 * day, AssessmentDeliveryEngine.nextDelayedCheckpoint(
            listOf(first, delayedAttempt("day-1", day), delayedAttempt("day-3", 3 * day),
                delayedAttempt("day-7", 7 * day)),
            DiagnosticDimension.SITUATIONAL_MEANING,
            now = 7 * day
        ))
        assertEquals(null, AssessmentDeliveryEngine.nextDelayedCheckpoint(
            listOf(first, delayedAttempt("day-1", day), delayedAttempt("day-3", 3 * day),
                delayedAttempt("day-7", 7 * day), delayedAttempt("day-21", 21 * day)),
            DiagnosticDimension.SITUATIONAL_MEANING,
            now = 21 * day
        ))
    }

    @Test
    fun delayedCheckpointIgnoresFutureAttempts() {
        val day = MasteryEngine.DAY_MILLIS
        assertEquals(day, AssessmentDeliveryEngine.nextDelayedCheckpoint(
            listOf(delayedAttempt("first", 0), delayedAttempt("future", 21 * day)),
            DiagnosticDimension.SITUATIONAL_MEANING,
            now = 0
        ))
    }

    private fun attempt(id: String, timestamp: Long, independent: Boolean, unfamiliar: Boolean) =
        Attempt(
            id = id,
            unitId = "s1-explain",
            taskId = "assessment:test",
            timestamp = timestamp,
            response = "Antwort",
            source = EvidenceSource.SELF_REFLECTION,
            independentProduction = independent,
            unfamiliar = unfamiliar
        )

    private fun delayedAttempt(id: String, timestamp: Long) = Attempt(
        id = id,
        unitId = "s1-explain",
        taskId = "accuracy-delayed",
        timestamp = timestamp,
        response = "Antwort",
        source = EvidenceSource.HUMAN_REVIEW,
        scores = Dimension.values().associateWith { 3 },
        unfamiliar = true,
        independentProduction = true
    )

}
