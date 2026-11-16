package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveControlEngineTest {
    private val unit = CurriculumCatalogue.coreUnits.first()
    private val now = 100L * MasteryEngine.DAY_MILLIS

    @Test
    fun lessonProgressesFromRetrievalThroughInputListeningComprehensionAndProduction() {
        assertDecision(emptyList(), "retrieval", AdaptiveReasonCode.FIRST_RETRIEVAL)

        val retrieval = practice("retrieval", now - 5)
        assertDecision(listOf(retrieval), "reading", AdaptiveReasonCode.NEXT_INPUT)

        val reading = practice("reading", now - 4)
        assertDecision(listOf(retrieval, reading), "listening", AdaptiveReasonCode.NEXT_LISTENING)

        val listening = practice("listening", now - 3)
        assertDecision(
            listOf(retrieval, reading, listening),
            "quiz",
            AdaptiveReasonCode.NEXT_COMPREHENSION_CHECK
        )

        val quiz = quiz(correct = true, timestamp = now - 2)
        assertDecision(
            listOf(retrieval, reading, listening, quiz),
            "controlled",
            AdaptiveReasonCode.NEXT_CONTROLLED_PRACTICE
        )

        val controlled = practice("controlled", now - 1)
        val firstProduction = AdaptiveControlEngine.decideForUnit(
            unit,
            listOf(retrieval, reading, listening, quiz, controlled),
            emptyList(),
            now
        )
        assertEquals(AdaptiveReasonCode.NEXT_PRODUCTION, firstProduction.reasonCode)
        assertTrue(firstProduction.primaryTaskId in setOf("speaking", "interaction", "writing"))

        val secondProduction = practice(firstProduction.primaryTaskId!!, now)
        val nextProduction = AdaptiveControlEngine.decideForUnit(
            unit,
            listOf(retrieval, reading, listening, quiz, controlled, secondProduction),
            emptyList(),
            now + 1
        )
        assertEquals(AdaptiveReasonCode.NEXT_PRODUCTION, nextProduction.reasonCode)
        assertNotEquals(firstProduction.primaryTaskId, nextProduction.primaryTaskId)
    }

    @Test
    fun failedQuizAndRecognizedResponseIssueTakePriorityOverNormalLessonSequence() {
        val failedQuiz = quiz(correct = false, timestamp = now - 1)
        val quizDecision = AdaptiveControlEngine.decide(listOf(failedQuiz), emptyList(), now)
        assertEquals("quiz", quizDecision.primaryTaskId)
        assertEquals(AdaptiveReasonCode.QUIZ_REPAIR, quizDecision.reasonCode)

        val attempts = listOf(
            quiz(correct = true, timestamp = now - 3),
            practice("writing", now - 2, "Ich bleibe, weil ich gehe morgen."),
            practice("interaction", now - 1, "Ich frage nach dem nächsten Schritt.")
        )
        val repairDecision = AdaptiveControlEngine.decide(attempts, emptyList(), now)
        assertEquals("controlled", repairDecision.primaryTaskId)
        assertEquals(AdaptiveReasonCode.RESPONSE_REPAIR, repairDecision.reasonCode)
    }

    @Test
    fun duePersonalRepairOverridesTheLessonStepAndSelectsItsContext() {
        val due = RealLifeExperience(
            id = "due",
            createdAt = now - 10,
            context = ContextTrack.ENGINEERING,
            situation = "Besprechung",
            heard = "Können Sie das prüfen?",
            intended = "Nachfragen",
            said = "Ich weiß nicht.",
            issueType = RealLifeIssueType.REPAIR,
            reviewStage = 0,
            nextReviewAt = now
        )

        val decision = AdaptiveControlEngine.decide(emptyList(), listOf(due), now)

        assertEquals("interaction", decision.primaryTaskId)
        assertEquals(AdaptiveReasonCode.PERSONAL_REPAIR_DUE, decision.reasonCode)
        assertEquals(ContextTrack.ENGINEERING, decision.recommendedContext)
        assertEquals(unit.id, decision.recommendedUnitId)
    }

    @Test
    fun futureResponsesAndPersonalReportsCannotChangeTodaysRecommendation() {
        val futureAttempt = practice("writing", now + 10, "Ich bleibe, weil ich gehe morgen.")
        val futureExperience = RealLifeExperience(
            id = "future",
            createdAt = now + 1,
            context = ContextTrack.SOCIAL,
            situation = "Gespräch",
            heard = "",
            intended = "Reparieren",
            said = "Okay",
            issueType = RealLifeIssueType.REPAIR,
            reviewStage = 0,
            nextReviewAt = now
        )

        val decision = AdaptiveControlEngine.decide(
            listOf(futureAttempt),
            listOf(futureExperience),
            now
        )

        assertEquals("retrieval", decision.primaryTaskId)
        assertEquals(AdaptiveReasonCode.FIRST_RETRIEVAL, decision.reasonCode)
    }

    private fun assertDecision(
        attempts: List<Attempt>,
        task: String,
        reason: AdaptiveReasonCode
    ) {
        val decision = AdaptiveControlEngine.decide(attempts, emptyList(), now)
        assertEquals(task, decision.primaryTaskId)
        assertEquals(reason, decision.reasonCode)
        assertEquals(unit.id, decision.recommendedUnitId)
        assertEquals(8, decision.taskOrder.size)
        assertEquals(task, decision.taskOrder.first())
    }

    private fun practice(task: String, timestamp: Long, response: String = "Ich erläutere den nächsten Schritt.") =
        Attempt(
            id = "$task-$timestamp",
            unitId = unit.id,
            taskId = task,
            timestamp = timestamp,
            response = response,
            source = EvidenceSource.SELF_REFLECTION,
            context = ContextTrack.GENERAL,
            independentProduction = task in setOf("speaking", "interaction", "writing")
        )

    private fun quiz(correct: Boolean, timestamp: Long) = Attempt(
        id = "quiz-$timestamp",
        unitId = unit.id,
        taskId = "quiz",
        timestamp = timestamp,
        response = if (correct) "Option 1" else "Option 2",
        source = EvidenceSource.AUTOMATIC_QUIZ,
        scores = mapOf(Dimension.COMPREHENSION to if (correct) 4 else 0),
        errors = if (correct) emptyList() else listOf("incorrect")
    )
}
