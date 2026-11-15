package com.example.programme

import org.junit.Assert.*
import org.junit.Test

class CurriculumCatalogueTest {
    @Test fun catalogueMapsSevenStagesAndAllFourContexts() {
        assertEquals(listOf("Diagnostic", "B2 consolidation", "advanced comprehension",
            "advanced production", "interaction pragmatics", "integration mediation",
            "independent transfer practice"), CurriculumCatalogue.stages)
        assertEquals(28, CurriculumCatalogue.units.size)
        assertEquals((0..6).toSet(), CurriculumCatalogue.units.map { it.stage }.toSet())
        assertEquals(CurriculumCatalogue.units.size, CurriculumCatalogue.units.map { it.id }.toSet().size)
        CurriculumCatalogue.units.forEach {
            assertTrue(it.title.isNotBlank())
            assertTrue(it.objectives.isNotEmpty() && it.objectives.all(String::isNotBlank))
            assertEquals(ContextTrack.values().toSet(), it.contexts.keys)
            assertTrue(it.contexts.values.all(String::isNotBlank))
        }
    }

    @Test fun allMappedUnitsAreFullyAuthored() {
        val authored = CurriculumCatalogue.units.filter { it.content != null }
        assertEquals(28, authored.size)
        assertEquals(CurriculumCatalogue.units.map { it.id }.toSet(), authored.map { it.id }.toSet())
        authored.forEach { unit ->
            val c = requireNotNull(unit.content)
            assertTrue(c.reading.split(Regex("\\s+")).size >= 75)
            assertTrue(c.listening.lines().size >= 5)
            assertTrue(c.expressions.size >= 4)
            assertTrue(listOf(c.grammar, c.question, c.explanation, c.controlledPrompt,
                c.controlledModel, c.speaking, c.interaction, c.writing, c.transfer, c.feedback)
                .all { it.isNotBlank() })
            assertTrue(c.options.size >= 3)
            assertEquals(c.options.size, c.options.distinct().size)
            assertTrue(c.correctOption in c.options.indices)
        }
    }

    @Test fun stableIdsAreMappedByFunctionRatherThanHistoricalPrefix() {
        val expected = mapOf(
            0 to setOf("s0-profile", "s0-diagnose"),
            1 to setOf("s0-repair", "s0-retrieve", "s1-explain", "s1-justify", "s1-uncertainty", "s1-feedback"),
            2 to setOf("s2-data", "s4-challenge", "s5-nuance"),
            3 to setOf("s4-argument", "s4-present", "s5-revise"),
            4 to setOf("s2-register", "s3-negotiate", "s3-disagree", "s3-deescalate", "s3-agree", "s5-spontaneous"),
            5 to setOf("s2-summarise", "s2-mediate", "s4-ethics", "s5-facilitate"),
            6 to setOf("s6-general", "s6-specialist", "s6-retention", "s6-checkpoint")
        )
        assertEquals(expected, CurriculumCatalogue.units.groupBy { it.stage }
            .mapValues { (_, units) -> units.map { it.id }.toSet() })
        assertEquals(listOf("s1-explain"), CurriculumCatalogue.units.first { it.id == "s2-summarise" }.prerequisites)
    }

    @Test fun everyCoachingContextIsYouthFootballRatherThanLifeCoaching() {
        val youthAgeGroup = Regex("U(?:11|12|13|14|15)\\b")
        val lifeCoaching = Regex("Klient|Beratung|Intervention|Selbstdeutung|Reflexionsübung", RegexOption.IGNORE_CASE)
        CurriculumCatalogue.units.forEach { unit ->
            val coaching = unit.contexts.getValue(ContextTrack.COACHING)
            assertTrue("Missing youth football age group in ${unit.id}: $coaching", youthAgeGroup.containsMatchIn(coaching))
            assertFalse("Life-coaching context in ${unit.id}", lifeCoaching.containsMatchIn(coaching))
        }
    }

    @Test fun everyEngineeringContextIsExplicitlyAutomotive() {
        val automotive = Regex("EV|Hybrid|ECU|TCU|Automotive|FMEA")
        CurriculumCatalogue.units.forEach { unit ->
            assertTrue("Generic engineering context in ${unit.id}",
                automotive.containsMatchIn(unit.contexts.getValue(ContextTrack.ENGINEERING)))
        }
    }

    @Test fun authoredUnitsContainContextualProductionTasks() {
        CurriculumCatalogue.units.filter { it.content != null }.forEach { unit ->
            val c = requireNotNull(unit.content)
            val tasks = listOf(c.controlledPrompt, c.speaking, c.interaction, c.writing, c.transfer).joinToString(" ")
            assertTrue("Missing controlled production task in ${unit.id}", c.controlledPrompt.isNotBlank())
            assertTrue("Missing speaking task in ${unit.id}", c.speaking.isNotBlank())
            assertTrue("Missing interaction task in ${unit.id}", c.interaction.isNotBlank())
            assertTrue("Missing writing task in ${unit.id}", c.writing.isNotBlank())
            assertTrue("Missing transfer task in ${unit.id}", tasks.length > 120)
        }
    }

    @Test fun uncertaintyTransferClarifiesPlayerAbsencesWithParentsWithoutAttributingMotives() {
        val c = requireNotNull(CurriculumCatalogue.units.first { it.id == "s1-uncertainty" }.content)
        assertTrue(c.transfer.contains("U14-Spieler"))
        assertTrue(c.transfer.contains("Eltern"))
        assertTrue(c.transfer.contains("Gründe sind unbekannt"))
        assertTrue(c.transfer.contains("Informationsweg"))
        assertTrue(c.transfer.contains("ohne Motivation, Gesundheit oder Familienlage zu behaupten"))
        assertFalse(Regex("Klient|psychologisch|Diagnose", RegexOption.IGNORE_CASE)
            .containsMatchIn(c.transfer))
    }

    @Test fun prerequisitesExistAndAreAcyclic() {
        val units = CurriculumCatalogue.units.associateBy { it.id }
        fun visit(unit: CurriculumUnit, ancestors: Set<String>) {
            assertFalse("Cycle at ${unit.id}", unit.id in ancestors)
            unit.prerequisites.forEach { id ->
                assertTrue("Missing prerequisite $id", id in units)
                val prerequisite = units.getValue(id)
                assertTrue(prerequisite.stage <= unit.stage)
                visit(prerequisite, ancestors + unit.id)
            }
        }
        units.values.forEach { visit(it, emptySet()) }
    }

    @Test fun allMappedUnitsArePlayable() {
        assertTrue(CurriculumCatalogue.units.all { it.content != null })
        assertEquals("s1-explain", MasteryEngine.nextUnit(emptyList(), 0)?.id)
    }
}
