package com.example.programme

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PracticeDraftRepositoryTest {
    private lateinit var preferences: SharedPreferences

    @Before
    fun setUp() {
        preferences = ApplicationProvider.getApplicationContext<Application>()
            .getSharedPreferences("practice_drafts_test", Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
    }

    @Test
    fun draftPersistsPerUnitTaskAndContextAndCanBeCleared() {
        val repository = PracticeDraftRepository(preferences)
        repository.save("s3-negotiate", "writing", ContextTrack.GENERAL, "Erster Entwurf")
        repository.save("s3-negotiate", "writing", ContextTrack.SOCIAL, "Andere Perspektive")

        val restored = PracticeDraftRepository(preferences)
        assertEquals("Erster Entwurf", restored.load("s3-negotiate", "writing", ContextTrack.GENERAL))
        assertEquals("Andere Perspektive", restored.load("s3-negotiate", "writing", ContextTrack.SOCIAL))
        assertEquals(2, restored.savedDrafts().size)

        restored.clear("s3-negotiate", "writing", ContextTrack.GENERAL)
        assertEquals("", restored.load("s3-negotiate", "writing", ContextTrack.GENERAL))
        restored.save("s3-negotiate", "writing", ContextTrack.SOCIAL, " ")
        assertTrue(restored.savedDrafts().isEmpty())

        restored.save("s2-data", "reading", ContextTrack.ENGINEERING, "Noch offen")
        restored.clearAll()
        assertTrue(restored.savedDrafts().isEmpty())
    }

    @Test
    fun localExportIncludesUnfinishedLessonDrafts() {
        val export = JSONObject(
            ProgrammeExport.create(
                attempts = emptyList(),
                experiences = emptyList(),
                assessments = emptyList(),
                practiceDrafts = listOf(
                    PracticeDraft(
                        unitId = "s3-negotiate",
                        taskId = "writing",
                        context = ContextTrack.GENERAL,
                        response = "Noch nicht abgeschickt",
                        updatedAt = 123L
                    )
                )
            )
        )

        val draft = export.getJSONArray("practiceDrafts").getJSONObject(0)
        assertEquals("s3-negotiate", draft.getString("unitId"))
        assertEquals("writing", draft.getString("taskId"))
        assertEquals("GENERAL", draft.getString("context"))
        assertEquals("Noch nicht abgeschickt", draft.getString("response"))
    }
}
