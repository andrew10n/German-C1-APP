package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeaknessEngineTest {
    @Test
    fun classifiesRealLifeSignalsWithoutInventingPermanentScores() {
        val experience = RealLifeExperience(
            id = "e1",
            createdAt = 1,
            context = ContextTrack.ENGINEERING,
            situation = "Messwertbesprechung",
            heard = "Da ist etwas aufgefallen.",
            intended = "Unsicherheit ausdrücken",
            said = "Ich weiß nicht.",
            issueType = RealLifeIssueType.LISTENING,
            reviewStage = 0,
            nextReviewAt = 2
        )

        val summary = WeaknessEngine.profile(emptyList(), listOf(experience), 3).top().single()

        assertEquals(WeaknessSignal.LISTENING_VOCABULARY, summary.signal)
        assertEquals(1, summary.sampleCount)
        assertEquals(WeaknessConfidence.LOW, summary.confidence)
        assertEquals(setOf(ContextTrack.ENGINEERING), summary.contexts)
    }

    @Test
    fun repeatedSignalsBecomeHigherPriorityAndConfidence() {
        val experiences = listOf(
            experience("one", 1, ContextTrack.GENERAL),
            experience("two", 2, ContextTrack.SOCIAL),
            experience("three", 3, ContextTrack.ENGINEERING)
        )

        val summary = WeaknessEngine.profile(emptyList(), experiences, 4).top().single()

        assertEquals(WeaknessSignal.ACTIVE_RETRIEVAL, summary.signal)
        assertEquals(3, summary.sampleCount)
        assertEquals(WeaknessConfidence.HIGH, summary.confidence)
        assertTrue(summary.priority > 1.5)
        assertEquals(3, summary.contexts.size)
    }

    @Test
    fun attemptErrorsAreMappedToTaskSpecificSignals() {
        val attempt = Attempt(
            id = "a",
            unitId = "s1-explain",
            taskId = "interaction",
            timestamp = 10,
            response = "Ich weiß nicht.",
            source = EvidenceSource.AUTOMATIC_QUIZ,
            context = ContextTrack.COACHING,
            errors = listOf("unexpected follow-up")
        )

        val summary = WeaknessEngine.profile(listOf(attempt), emptyList(), 11).top().single()

        assertEquals(WeaknessSignal.INTERACTION_MANAGEMENT, summary.signal)
        assertEquals(ContextTrack.COACHING, summary.contexts.single())
    }

    @Test
    fun futureEvidenceDoesNotEnterThePracticeProfile() {
        val future = experience("future", 20, ContextTrack.GENERAL)

        assertTrue(WeaknessEngine.profile(emptyList(), listOf(future), now = 10).summaries.isEmpty())
    }

    @Test
    fun oldSignalsLosePriorityAndConfidenceAsTheyAge() {
        val old = experience("old", 1, ContextTrack.GENERAL)
        val recent = experience("recent", 100, ContextTrack.SOCIAL)

        val oldProfile = WeaknessEngine.profile(emptyList(), listOf(old), now = 1 + 180 * 86_400_000L)
        val recentProfile = WeaknessEngine.profile(emptyList(), listOf(recent), now = 101)

        assertTrue(oldProfile.top().single().priority < recentProfile.top().single().priority)
        assertEquals(WeaknessConfidence.LOW, oldProfile.top().single().confidence)
    }

    private fun experience(id: String, createdAt: Long, context: ContextTrack) =
        RealLifeExperience(
            id = id,
            createdAt = createdAt,
            context = context,
            situation = "Alltag",
            heard = "",
            intended = "Antwort geben",
            said = "Das fehlt mir gerade.",
            issueType = RealLifeIssueType.RETRIEVAL,
            reviewStage = 0,
            nextReviewAt = createdAt + 1
        )
}
