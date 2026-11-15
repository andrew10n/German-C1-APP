package com.example

import com.example.programme.core.*
import org.junit.Assert.*
import org.junit.Test

class CurriculumContentTest {
    @Test fun `catalog maps twenty eight communication units through seven stages and all contexts`() {
        val units = CurriculumCatalog.units
        assertEquals(7, CurriculumCatalog.stages.size)
        assertTrue(units.size >= 25)
        assertEquals(units.size, units.map { it.id }.distinct().size)
        assertEquals((1..7).toList(), CurriculumCatalog.stages.map { it.number })
        CurriculumCatalog.stages.forEach { stage ->
            val members = units.filter { it.stage == stage.number }
            assertEquals(CommunicationContext.values().toSet(), members.map { it.context }.toSet())
            assertEquals(members.map { it.id }, stage.unitIds)
            assertTrue(stage.title.isNotBlank())
        }
        assertTrue(units.all { it.title.isNotBlank() && it.communicationGoal.isNotBlank() })
    }

    @Test fun `prerequisites exist precede dependent units and are nonempty after first unit`() {
        val units = CurriculumCatalog.units
        units.forEachIndexed { index, unit ->
            if (index == 0) assertTrue(unit.prerequisites.isEmpty())
            else assertTrue(unit.prerequisites.isNotEmpty())
            unit.prerequisites.forEach { prerequisite ->
                assertTrue(units.indexOfFirst { it.id == prerequisite } in 0 until index)
            }
        }
        ProgrammePolicy(units)
    }

    @Test fun `only first three units claim authored content roadmap has none`() {
        val authored = CurriculumCatalog.units.filter { it.status == ContentStatus.AUTHORED }
        assertEquals(listOf("u01", "u02", "u03"), authored.map { it.id })
        assertTrue(authored.all { it.content != null })
        assertTrue(CurriculumCatalog.units.drop(3).all { it.status == ContentStatus.ROADMAP && it.content == null })
        assertNull(CurriculumCatalog.unit("not-a-unit"))
        val first = authored.first()
        assertThrows(IllegalArgumentException::class.java) {
            CurriculumUnit("bad", 1, "Bad", "Goal", CommunicationContext.GENERAL,
                emptyList(), ContentStatus.AUTHORED)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CurriculumUnit("bad", 1, "Bad", "Goal", CommunicationContext.GENERAL,
                emptyList(), ContentStatus.ROADMAP, first.content)
        }
    }

    @Test fun `authored units contain substantial original German reading and explicitly synthetic TTS`() {
        CurriculumCatalog.units.take(3).forEach { unit ->
            val content = requireNotNull(unit.content)
            assertTrue(content.objectives.size >= 3)
            val reading = content.sources.single { it.kind == SourceKind.READING }
            val listening = content.sources.single { it.kind == SourceKind.LISTENING_TTS }
            assertTrue(reading.text.split(Regex("\\s+")).size >= 230)
            assertTrue(listening.text.split(Regex("\\s+")).size >= 130)
            assertTrue(listening.delivery.contains("TTS"))
            assertTrue(listening.delivery.contains("synthetische"))
            assertTrue(listening.delivery.contains("kein authentisches Audio"))
            assertTrue(content.sources.all { it.attribution.contains("Originaltext") })
        }
    }

    @Test fun `each authored unit has language support keyed source questions and every practice mode`() {
        val ids = mutableSetOf<String>()
        CurriculumCatalog.units.take(3).forEach { unit ->
            val content = requireNotNull(unit.content)
            assertTrue(content.collocations.size >= 6)
            assertTrue(content.collocations.all { it.expression.isNotBlank() && it.example.isNotBlank() })
            assertTrue(content.grammar.size >= 3)
            assertTrue(content.grammar.all { it.explanation.isNotBlank() && it.example.isNotBlank() })
            assertTrue(content.comprehension.size >= 6)
            content.sources.forEach { source ->
                assertTrue(ids.add(source.id))
                assertTrue(content.comprehension.count { it.sourceId == source.id } >= 3)
            }
            assertTrue(content.comprehension.all {
                it.question.isNotBlank() && it.keyedAnswer.isNotBlank() &&
                    content.sources.any { source -> source.id == it.sourceId }
            })
            assertEquals(ActivityKind.values().toSet(), content.tasks.map { it.kind }.toSet())
            content.tasks.forEach { task ->
                assertTrue(ids.add(task.id))
                assertTrue(task.prompt.length >= 80)
                assertTrue(task.successCriteria.length >= 60)
                assertTrue(task.modelAnswer.length >= 100)
                assertTrue(task.modelFeedback.length >= 100)
            }
            assertTrue(content.tasks.single { it.kind == ActivityKind.DELAYED_REVIEW }.minimumDelayDays >= 7)
            assertTrue(content.tasks.single { it.kind == ActivityKind.TRANSFER }.prompt.contains("ungesehen"))
        }
    }

    @Test fun `answer keys retain material caveats and quantitative boundaries`() {
        val public = requireNotNull(CurriculumCatalog.unit("u01")?.content)
        assertTrue(public.comprehension[0].keyedAnswer.contains("nicht auf alle"))
        assertTrue(public.comprehension[1].keyedAnswer.contains("nicht für"))
        val social = requireNotNull(CurriculumCatalog.unit("u02")?.content)
        assertTrue(social.comprehension[0].keyedAnswer.contains("Deutung"))
        assertTrue(social.comprehension[4].keyedAnswer.contains("nicht versprechen"))
        val engineering = requireNotNull(CurriculumCatalog.unit("u03")?.content)
        assertTrue(engineering.comprehension[0].keyedAnswer.contains("nicht nachgewiesen"))
        assertTrue(engineering.comprehension[1].keyedAnswer.contains("drei allein"))
        assertTrue(engineering.comprehension[1].keyedAnswer.contains("keine genehmigte"))
    }

    @Test fun `catalog content and planning results cannot be mutated through collection casts`() {
        assertThrows(UnsupportedOperationException::class.java) {
            (CurriculumCatalog.units as MutableList).clear()
        }
        assertThrows(UnsupportedOperationException::class.java) {
            (CurriculumCatalog.stages.first().unitIds as MutableList).clear()
        }
        val unit = CurriculumCatalog.units.first()
        assertThrows(UnsupportedOperationException::class.java) {
            (requireNotNull(unit.content).tasks as MutableList).clear()
        }
        val prerequisites = mutableListOf("u01")
        val snapshot = CurriculumUnit("copy", 1, "Title", "Goal", CommunicationContext.SOCIAL,
            prerequisites, ContentStatus.ROADMAP)
        prerequisites.clear()
        assertEquals(listOf("u01"), snapshot.prerequisites)
        val plan = DailyPlanner().plan(30, DailyBudget.SHORT)
        assertThrows(UnsupportedOperationException::class.java) { (plan.blocks as MutableList).clear() }
    }

    @Test fun `roadmap only catalog never fabricates a lesson plan`() {
        val unit = CurriculumUnit("future", 1, "Future", "Goal", CommunicationContext.COACHING,
            emptyList(), ContentStatus.ROADMAP)
        val plan = DailyPlanner(listOf(unit)).plan(30, DailyBudget.STANDARD)
        assertTrue(plan.blocks.isEmpty())
        assertEquals(0, plan.plannedMinutes)
    }
}
