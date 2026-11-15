package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveDiagnosticRouteTest {
    private val unit = CurriculumCatalogue.coreUnits.first()

    @Test
    fun checkpointRequiresAnObjectivelyCorrectQuizAndTwoDifferentUnassistedOutputs() {
        assertEquals(
            DiagnosticCheckpointState.NEEDS_OBJECTIVE_CHECK,
            AdaptiveDiagnosticRoute.evaluate(unit, emptyList(), 1_000L).state
        )
        val failedQuiz = quiz(correct = false)
        assertEquals(
            DiagnosticCheckpointState.NEEDS_REMEDIATION,
            AdaptiveDiagnosticRoute.evaluate(unit, listOf(failedQuiz), 1_000L).state
        )
        val correctQuiz = quiz(correct = true)
        assertEquals(
            DiagnosticCheckpointState.NEEDS_PRODUCTION,
            AdaptiveDiagnosticRoute.evaluate(unit, listOf(correctQuiz), 1_000L).state
        )
        val complete = listOf(
            correctQuiz,
            production("writing", 2_000L, ContextTrack.GENERAL, "Ich fasse die Entscheidung zusammen."),
            production("interaction", 3_000L, ContextTrack.SOCIAL, "Ich frage nach und sichere den nächsten Schritt.")
        )
        assertEquals(
            DiagnosticCheckpointState.COMPLETE,
            AdaptiveDiagnosticRoute.evaluate(unit, complete, 3_000L).state
        )
    }

    @Test
    fun recognizedTextIssueRequestsRemediationInsteadOfInferringC1Ability() {
        val attempts = listOf(
            quiz(correct = true),
            production("writing", 2_000L, ContextTrack.GENERAL, "Ich bleibe, weil ich gehe morgen."),
            production("interaction", 3_000L, ContextTrack.SOCIAL, "Ich frage nach dem nächsten Schritt.")
        )

        val progress = AdaptiveDiagnosticRoute.evaluate(unit, attempts, 3_000L)

        assertEquals(DiagnosticCheckpointState.NEEDS_REMEDIATION, progress.state)
        assertTrue(progress.feedback.any { it.contains("Verb") })
    }

    @Test
    fun passingPracticeCheckpointUnlocksTheNextRouteStepButNeverFormalMastery() {
        val firstUnitAttempts = listOf(
            quiz(correct = true),
            production("writing", 2_000L, ContextTrack.GENERAL, "Ich fasse die Entscheidung zusammen."),
            production("interaction", 3_000L, ContextTrack.SOCIAL, "Ich frage nach und sichere den nächsten Schritt.")
        )

        assertTrue(AdaptiveDiagnosticRoute.isUnlocked(unit, firstUnitAttempts, 3_000L))
        assertTrue(AdaptiveDiagnosticRoute.isCheckpointComplete(unit, firstUnitAttempts, 3_000L))
        assertFalse(MasteryEngine.isMastered(unit, firstUnitAttempts))
        assertEquals(1, AdaptiveDiagnosticRoute.completedCoreUnits(firstUnitAttempts, 3_000L))
    }

    @Test
    fun practiceCheckpointDoesNotAdvanceOnAssistedOrFutureAnswers() {
        val attempts = listOf(
            quiz(correct = true),
            production("writing", 2_000L, ContextTrack.GENERAL, "Ich fasse die Entscheidung zusammen."),
            production("interaction", 4_000L, ContextTrack.SOCIAL, "Ich sichere den nächsten Schritt.")
                .copy(assisted = true)
        )

        assertEquals(
            DiagnosticCheckpointState.NEEDS_PRODUCTION,
            AdaptiveDiagnosticRoute.evaluate(unit, attempts, 3_000L).state
        )
    }

    private fun quiz(correct: Boolean) = Attempt(
        id = "quiz-$correct",
        unitId = unit.id,
        taskId = "quiz",
        timestamp = 1_000L,
        response = if (correct) "Option 1" else "Option 2",
        source = EvidenceSource.AUTOMATIC_QUIZ,
        scores = mapOf(Dimension.COMPREHENSION to if (correct) 4 else 0),
        errors = if (correct) emptyList() else listOf("incorrect")
    )

    private fun production(
        taskId: String,
        timestamp: Long,
        context: ContextTrack,
        response: String
    ) = Attempt(
        id = "$taskId-$timestamp",
        unitId = unit.id,
        taskId = taskId,
        timestamp = timestamp,
        response = response,
        source = EvidenceSource.SELF_REFLECTION,
        independentProduction = true,
        context = context
    )
}
