package com.example.programme

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExternalListeningPracticeRepositoryTest {
    private lateinit var preferences: SharedPreferences

    @Before
    fun setUp() {
        preferences = ApplicationProvider.getApplicationContext<Application>()
            .getSharedPreferences("external_listening_practice_test", Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
    }

    @Test
    fun notesSurviveRepositoryRecreationAndCanBeCleared() {
        ExternalListeningPracticeRepository(preferences)
            .save("easy-german", "Folge 42", "Hauptaussage", "Korrektur", comparisonOpened = true)

        val restored = ExternalListeningPracticeRepository(preferences)
        val draft = restored.load("easy-german")
        assertEquals("Folge 42", draft.itemReference)
        assertEquals("Hauptaussage", draft.firstPass)
        assertEquals("Korrektur", draft.secondPass)
        assertTrue(draft.comparisonOpened)
        assertTrue(draft.updatedAt > 0L)
        assertEquals(1, restored.savedDrafts().size)

        restored.clear("easy-german")
        assertFalse(restored.load("easy-german").hasNotes)
        assertTrue(restored.savedDrafts().isEmpty())

        restored.save("dw-deutsch-lernen", "", "DW-Notiz", "", comparisonOpened = false)
        restored.clearAll()
        assertTrue(restored.savedDrafts().isEmpty())
    }

    @Test
    fun localExportIncludesSavedListeningNotes() {
        val export = JSONObject(
            ProgrammeExport.create(
                attempts = emptyList(),
                experiences = emptyList(),
                assessments = emptyList(),
                externalListeningNotes = listOf(
                    ExternalListeningDraft(
                        sourceId = "easy-german",
                        itemReference = "Folge 42",
                        firstPass = "Erste Antwort",
                        secondPass = "Vergleich",
                        comparisonOpened = true,
                        updatedAt = 123L
                    )
                )
            )
        )

        val note = export.getJSONArray("externalListeningNotes").getJSONObject(0)
        assertEquals("easy-german", note.getString("sourceId"))
        assertEquals("Folge 42", note.getString("itemReference"))
        assertEquals("Erste Antwort", note.getString("firstPass"))
        assertEquals("Vergleich", note.getString("secondPass"))
    }
}
