package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExternalListeningLibraryTest {
    @Test
    fun publicSourcesAreHttpsLinkOnlyAndHaveActionableListeningTasks() {
        assertEquals(6, ExternalListeningLibrary.sources.size)
        assertEquals(
            ExternalListeningLibrary.sources.size,
            ExternalListeningLibrary.sources.map { it.id }.toSet().size
        )
        ExternalListeningLibrary.sources.forEach { source ->
            assertTrue(source.title, ExternalListeningLibrary.isValid(source))
            assertTrue(source.url, source.url.startsWith("https://"))
            assertTrue(source.practiceTasks.all { it.length >= 30 })
        }
    }
}
