package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumQualityAuditTest {
    @Test
    fun allAuthoredUnitsHaveDistinctLongFormMaterialsAndDeepProductionTasks() {
        val units = CurriculumCatalogue.units
        val readings = units.map { requireNotNull(it.content).reading.trim() }
        val listenings = units.map { requireNotNull(it.content).listening.trim() }
        assertEquals(units.size, readings.distinct().size)
        assertEquals(units.size, listenings.distinct().size)
        units.forEach { unit ->
            val c = requireNotNull(unit.content)
            assertTrue("${unit.id}: reading too short", c.reading.wordCount() >= 75)
            assertTrue("${unit.id}: listening needs five turns/lines", c.listening.lines().count { it.isNotBlank() } >= 5)
            assertTrue("${unit.id}: writing task too short", c.writing.wordCount() >= 18)
            assertTrue("${unit.id}: interaction task too short", c.interaction.wordCount() >= 18)
            assertTrue("${unit.id}: transfer task too short", c.transfer.wordCount() >= 18)
            assertTrue("${unit.id}: feedback too short", c.feedback.wordCount() >= 10)
            assertTrue("${unit.id}: options must be distinct", c.options.size == c.options.distinct().size)
            assertTrue("${unit.id}: correct option out of range", c.correctOption in c.options.indices)
        }
    }

    @Test
    fun authoredContentContainsNoDraftMarkersOrUnsupportedCertificationClaims() {
        val forbidden = Regex("TODO|TBD|lorem ipsum|placeholder|coming soon|official C1 certificate|guaranteed C1", RegexOption.IGNORE_CASE)
        CurriculumCatalogue.units.forEach { unit ->
            val c = requireNotNull(unit.content)
            val text = listOf(c.reading, c.listening, c.grammar, c.question, c.explanation,
                c.controlledPrompt, c.controlledModel, c.speaking, c.interaction, c.writing,
                c.transfer, c.feedback).joinToString(" ")
            assertFalse("${unit.id}: draft or unsupported certification marker", forbidden.containsMatchIn(text))
        }
    }

    @Test
    fun contextualPromptsRemainSpecificAndDoNotUseLifeCoachingSubstituteContexts() {
        val lifeCoaching = Regex("Klient|Intervention|Selbstdeutung|Reflexionsübung", RegexOption.IGNORE_CASE)
        CurriculumCatalogue.units.forEach { unit ->
            val contexts = unit.contexts
            assertTrue("${unit.id}: missing automotive context", Regex("EV|Hybrid|ECU|TCU|Automotive|FMEA", RegexOption.IGNORE_CASE)
                .containsMatchIn(contexts.getValue(ContextTrack.ENGINEERING)))
            assertTrue("${unit.id}: missing youth-football context", Regex("U(?:11|12|13|14|15)\\b")
                .containsMatchIn(contexts.getValue(ContextTrack.COACHING)))
            assertFalse("${unit.id}: life-coaching substitute context", lifeCoaching.containsMatchIn(contexts.values.joinToString(" ")))
        }
    }

    @Test
    fun personalTransferPracticeDoesNotRequireAnExternalHumanReviewer() {
        val externalReviewGate = Regex(
            "zählt nur mit.{0,120}(mensch|unabhängig)|" +
                "eine unabhängige Person bewertet|" +
                "die Bewertung erfolgt unabhängig durch einen Menschen|" +
                "echte, unabhängige menschliche Beurteilung",
            RegexOption.IGNORE_CASE
        )
        CurriculumCatalogue.coreUnits.forEach { unit ->
            assertFalse("${unit.id}: transfer practice requires an external reviewer",
                externalReviewGate.containsMatchIn(requireNotNull(unit.content).transfer))
        }
    }

    private fun String.wordCount(): Int = trim().split(Regex("\\s+")).count { it.isNotBlank() }
}
