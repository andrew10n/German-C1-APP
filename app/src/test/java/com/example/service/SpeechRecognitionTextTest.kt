package com.example.service

import android.speech.RecognizerIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SpeechRecognitionTextTest {
    @Test
    fun longTurnIntentUsesGermanAndSegmentedRecognitionWhereSupported() {
        val intent = buildGermanLongTurnRecognitionIntent("test.app", 33)

        assertEquals("de-DE", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
        assertEquals(1, intent.getIntExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 0))
        assertEquals(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            intent.getStringExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION)
        )
        assertTrue(intent.getBooleanExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false))
    }

    @Test
    fun olderPlatformIntentOmitsUnsupportedSegmentedMode() {
        val intent = buildGermanLongTurnRecognitionIntent("test.app", 32)

        assertFalse(intent.hasExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION))
    }

    @Test
    fun mergesFinalSegmentWithoutDuplicatingIt() {
        assertEquals(
            "Zuerst die Lage prüfen. Danach die Entscheidung begründen.",
            SpeechRecognitionText.merge(
                listOf("Zuerst die Lage prüfen.", "Danach die Entscheidung begründen."),
                "Danach die Entscheidung begründen."
            )
        )
    }

    @Test
    fun prefersFullFinalTranscriptWhenItIncludesEarlierSegments() {
        assertEquals(
            "Zuerst die Lage prüfen. Danach entscheiden.",
            SpeechRecognitionText.merge(
                listOf("Zuerst die Lage prüfen."),
                "Zuerst die Lage prüfen. Danach entscheiden."
            )
        )
    }
}
