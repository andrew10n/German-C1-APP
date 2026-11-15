package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AttemptHistoryIndexTest {
    @Test
    fun appendingAttemptUpdatesOnlyItsUnitBucket() {
        val first = attempt("first", "unit-a")
        val second = attempt("second", "unit-b")
        val initial = AttemptHistoryIndex.empty().update(listOf(first))

        val updated = initial.update(listOf(first, second))

        assertEquals(listOf(first), updated.byUnit["unit-a"])
        assertEquals(listOf(second), updated.byUnit["unit-b"])
        assertEquals(1, updated.incrementalUpdates)
        assertEquals(1, updated.fullRebuilds)
    }

    @Test
    fun replacementAndDeletionRebuildAndIdenticalSnapshotIsReused() {
        val first = attempt("first", "unit-a")
        val initial = AttemptHistoryIndex.empty().update(listOf(first))

        assertSame(initial, initial.update(listOf(first)))
        val replaced = initial.update(emptyList())

        assertEquals(2, replaced.fullRebuilds)
        assertEquals(emptyMap<String, List<Attempt>>(), replaced.byUnit)
    }

    private fun attempt(id: String, unitId: String) = Attempt(
        id = id,
        unitId = unitId,
        taskId = "writing",
        timestamp = id.length.toLong(),
        response = "Antwort",
        source = EvidenceSource.SELF_REFLECTION
    )
}
