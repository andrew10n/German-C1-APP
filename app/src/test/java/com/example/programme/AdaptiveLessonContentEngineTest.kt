package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveLessonContentEngineTest {
    private val unit = CurriculumCatalogue.coreUnits.first()

    @Test
    fun cleanFirstUseKeepsCoreContentAndAddsNoUnsupportedChallenge() {
        val plan = AdaptiveLessonContentEngine.create(
            unit = unit,
            attempts = emptyList(),
            experiences = emptyList(),
            context = ContextTrack.GENERAL,
            now = 100
        )

        assertEquals(AdaptiveDifficulty.CORE, plan.difficulty)
        assertEquals("retrieval", plan.focusTaskId)
        assertNull(plan.addition)
    }

    @Test
    fun recognizedLocalErrorAddsTargetedRepairPrompt() {
        val attempt = production(
            task = "writing",
            timestamp = 1,
            response = "Ich bleibe, weil ich gehe morgen."
        )

        val plan = AdaptiveLessonContentEngine.create(
            unit = unit,
            attempts = listOf(attempt),
            experiences = emptyList(),
            context = ContextTrack.SOCIAL,
            now = 2
        )

        assertEquals(AdaptiveDifficulty.SUPPORT, plan.difficulty)
        assertNotNull(plan.addition)
        assertEquals("controlled", plan.addition?.taskId)
        assertTrue(plan.addition?.prompt?.contains("Verb im Nebensatz") == true)
        assertTrue(plan.addition?.prompt?.contains(unit.objectives.first()) == true)
    }

    @Test
    fun challengeAddsNewContextOnlyAfterCorrectAndVariedPractice() {
        val attempts = listOf(
            Attempt(
                id = "quiz",
                unitId = unit.id,
                taskId = "quiz",
                timestamp = 1,
                response = "Option 1",
                source = EvidenceSource.AUTOMATIC_QUIZ,
                scores = mapOf(Dimension.COMPREHENSION to 4)
            ),
            production("writing", 2, ContextTrack.GENERAL),
            production("speaking", 3, ContextTrack.SOCIAL),
            production("interaction", 4, ContextTrack.GENERAL)
        )

        val plan = AdaptiveLessonContentEngine.create(
            unit = unit,
            attempts = attempts,
            experiences = emptyList(),
            context = ContextTrack.GENERAL,
            now = 5
        )

        assertEquals(AdaptiveDifficulty.CHALLENGE, plan.difficulty)
        assertNotNull(plan.addition)
        assertEquals("transfer", plan.addition?.taskId)
        assertTrue(plan.addition?.prompt?.contains(unit.objectives.first()) == true)
        assertTrue(attempts.filter { it.taskId in setOf("writing", "speaking", "interaction") }
            .none { it.context == plan.addition?.context })
    }

    private fun production(task: String, timestamp: Long, context: ContextTrack) =
        production(task, timestamp, "Ich erläutere den nächsten Schritt.", context)

    private fun production(task: String, timestamp: Long, response: String) =
        production(task, timestamp, response, ContextTrack.GENERAL)

    private fun production(
        task: String,
        timestamp: Long,
        response: String,
        context: ContextTrack
    ) = Attempt(
        id = "$task-$timestamp",
        unitId = unit.id,
        taskId = task,
        timestamp = timestamp,
        response = response,
        source = EvidenceSource.SELF_REFLECTION,
        context = context,
        independentProduction = true
    )
}
