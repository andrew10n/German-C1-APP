package com.example.programme

import com.example.programme.core.ContentStatus
import com.example.programme.core.CurriculumCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumCatalogueIsolationTest {
    @Test
    fun activeCourseAndLegacyPlannerCataloguesRemainDistinct() {
        val activeIds = CurriculumCatalogue.units.map { it.id }.toSet()
        val legacyUnits = CurriculumCatalog.units

        assertEquals(28, activeIds.size)
        assertEquals(28, legacyUnits.size)
        assertTrue(activeIds.intersect(legacyUnits.map { it.id }.toSet()).isEmpty())
        assertEquals(28, CurriculumCatalogue.units.count { it.content != null })
        assertEquals(
            listOf("u01", "u02", "u03"),
            legacyUnits.filter { it.status == ContentStatus.AUTHORED }.map { it.id }
        )
    }
}
