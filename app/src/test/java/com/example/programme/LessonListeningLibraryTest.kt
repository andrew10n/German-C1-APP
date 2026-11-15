package com.example.programme

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonListeningLibraryTest {
    @Test
    fun recommendationsAndBookBridgesPointToPlayableLessons() {
        val resources = LessonListeningLibrary.episodes.map { it.unitId } +
            LessonListeningLibrary.bookBridges.map { it.unitId }

        assertTrue(resources.isNotEmpty())
        resources.distinct().forEach { unitId ->
            val unit = CurriculumCatalogue.units.firstOrNull { it.id == unitId }
            assertNotNull("Resource must link to an existing unit: $unitId", unit)
            assertNotNull("Resource must link to an authored lesson: $unitId", unit?.content)
        }
        assertTrue(LessonListeningLibrary.episodes.all {
            it.url.startsWith("https://www.easygerman.org/") && it.title.isNotBlank() && it.task.isNotBlank()
        })
        assertTrue(LessonListeningLibrary.bookBridges.all {
            it.bookReference.isNotBlank() && it.audioFilenameHint.isNotBlank() && it.originalPrompt.isNotBlank()
        })
        val aspects = LessonListeningLibrary.bookBridges.filter {
            it.bookReference.startsWith("Aspekte neu C1")
        }
        assertTrue(aspects.size >= 4)
        assertTrue(aspects.all {
            it.audioReference?.contains("Tracknummer") == true &&
                it.originalPrompt.length > 80 &&
                !it.originalPrompt.contains("Bitte ergänzen Sie", ignoreCase = true)
        })
        assertTrue(
            LessonListeningLibrary.forUnit("s2-summarise").second
                .single().bookReference.contains("Kapitel 10")
        )
    }
}
