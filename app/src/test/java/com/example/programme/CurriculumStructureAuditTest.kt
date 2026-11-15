package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Regression checks for the authored course and the boundaries of its adaptivity. */
class CurriculumStructureAuditTest {
    @Test
    fun catalogueSeparatesOptionalOrientationFromTheSequencedCore() {
        assertEquals(2, CurriculumCatalogue.orientationUnits.size)
        assertTrue(CurriculumCatalogue.orientationUnits.all { it.stage == 0 && it.content != null })
        assertEquals(26, CurriculumCatalogue.coreUnits.size)
        assertTrue(CurriculumCatalogue.coreUnits.all { it.stage in 1..6 && it.content != null })
        assertEquals(
            mapOf(1 to 6, 2 to 3, 3 to 3, 4 to 6, 5 to 4, 6 to 4),
            CurriculumCatalogue.coreUnits.groupingBy { it.stage }.eachCount()
        )
    }

    @Test
    fun everyCoreUnitProvidesTheCompleteLearningCycle() {
        CurriculumCatalogue.coreUnits.forEach { unit ->
            val content = requireNotNull(unit.content)
            assertTrue("${unit.id}: fewer than two learning objectives", unit.objectives.size >= 2)
            assertTrue("${unit.id}: no reading input", content.reading.wordCount() >= 75)
            assertTrue("${unit.id}: no listening dialogue", content.listening.lines().count { it.isNotBlank() } >= 5)
            assertTrue("${unit.id}: too few expressions", content.expressions.size >= 4)
            assertTrue("${unit.id}: grammar focus missing", content.grammar.wordCount() >= 25)
            assertTrue("${unit.id}: comprehension item incomplete", content.question.isNotBlank() &&
                content.options.size >= 3 && content.correctOption in content.options.indices)
            assertTrue("${unit.id}: controlled practice missing", content.controlledPrompt.isNotBlank() &&
                content.controlledModel.isNotBlank())
            assertTrue("${unit.id}: productive modes incomplete", listOf(
                content.speaking, content.interaction, content.writing, content.transfer, content.feedback
            ).all { it.isNotBlank() })
        }
    }

    @Test
    fun adaptiveControlReordersTasksWithoutWaivingTheCoreSequence() {
        val experience = RealLifeExperience(
            id = "repair", createdAt = 1L, context = ContextTrack.SOCIAL,
            situation = "Unklare Zusage", heard = "Schauen wir mal", intended = "Termin klären",
            said = "Okay", issueType = RealLifeIssueType.REPAIR, reviewStage = 0, nextReviewAt = 1L
        )

        val decision = AdaptiveControlEngine.decide(emptyList(), listOf(experience), now = 2L)

        assertEquals("interaction", decision.primaryTaskId)
        assertEquals(AdaptiveAction.PRIORITIZE_INTERACTION, decision.actions.first())
        assertEquals(
            setOf("retrieval", "reading", "listening", "quiz", "controlled", "speaking", "interaction", "writing"),
            decision.taskOrder.toSet()
        )
        assertEquals(8, decision.taskOrder.size)
        assertFalse(decision.reason.contains("CEFR", ignoreCase = true))
    }

    @Test
    fun missingRoadmapContentRemainsVisibleAndBlocksGraduation() {
        val outline = CurriculumUnit(
            id = "future-outline",
            title = "Noch auszuarbeiten",
            stage = 2,
            prerequisites = emptyList(),
            objectives = listOf("Ziel"),
            contexts = emptyMap(),
            content = null
        )

        val units = CurriculumCatalogue.mergeRoadmapUnits(emptyList(), listOf(outline))
        val gaps = MasteryEngine.graduationGaps(emptyList(), units)

        assertEquals(listOf(outline), units)
        assertTrue(gaps.any { it.contains("Stufe 2: 1 Kompetenzen sind nur Entwürfe") })
    }

    @Test
    fun authoredRoadmapUnitReplacesItsOutlineWithoutDuplicatingTheId() {
        val authored = CurriculumCatalogue.coreUnits.first()
        val outline = authored.copy(content = null)

        val units = CurriculumCatalogue.mergeRoadmapUnits(listOf(authored), listOf(outline))

        assertEquals(listOf(authored), units)
    }

    private fun String.wordCount(): Int = trim().split(Regex("\\s+")).count { it.isNotBlank() }
}
