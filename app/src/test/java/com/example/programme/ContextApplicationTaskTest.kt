package com.example.programme

import org.junit.Assert.assertTrue
import org.junit.Test

class ContextApplicationTaskTest {
    @Test
    fun everyAuthoredUnitOffersAConcreteSoloTaskForEveryContext() {
        CurriculumCatalogue.coreUnits.forEach { unit ->
            ContextTrack.values().forEach { context ->
                val scenario = requireNotNull(unit.contexts[context])
                val prompt = ContextApplicationTask.build(unit, context)

                assertTrue("${unit.id} / $context: scenario missing", prompt.contains(scenario))
                assertTrue("${unit.id} / $context: objectives missing", unit.objectives.all(prompt::contains))
                assertTrue("${unit.id} / $context: no self-check", prompt.contains("prüfe selbst"))
                assertTrue("${unit.id} / $context: facts guard missing", prompt.contains("kennzeichne Annahmen"))
            }
        }
    }
}
