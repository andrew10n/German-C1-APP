package com.example.service

import android.content.Context
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.SpeakerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/** German device text-to-speech, not a native-speaker recording. */
class AudioSpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isShutdown = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                // Defer setup until the constructor has assigned the engine, even for an immediate callback.
                Handler(context.mainLooper).post {
                    if (!isShutdown) initializeGerman(status)
                }
            }
        } catch (e: Exception) {
            fail("Deutsche Sprachausgabe konnte nicht gestartet werden.")
            Log.e("AudioSpeechHelper", "Failed to create TextToSpeech instance", e)
        }
    }

    private fun initializeGerman(status: Int) {
        try {
            if (status != TextToSpeech.SUCCESS) {
                fail("Deutsche Sprachausgabe konnte nicht initialisiert werden.")
                return
            }
            val result = tts?.setLanguage(Locale.GERMANY)
            if (result == null || result < TextToSpeech.LANG_AVAILABLE) {
                fail("Deutsche TTS-Stimme nicht verfügbar. Bitte deutsche Sprachdaten in den Geräteeinstellungen installieren. Es wird keine Ersatzsprache verwendet.")
                return
            }
            setupProgressListener()
            isInitialized = true
            _errorMessage.value = null
        } catch (e: Exception) {
            fail("Deutsche Sprachausgabe ist nicht verfügbar.")
            Log.e("AudioSpeechHelper", "German TTS setup failed", e)
        }
    }

    private fun fail(message: String) {
        _isPlaying.value = false
        _currentUtteranceId.value = null
        _errorMessage.value = message
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                if (_currentUtteranceId.value == utteranceId && utteranceId != null) {
                    _isPlaying.value = true
                }
            }

            override fun onDone(utteranceId: String?) {
                if (_currentUtteranceId.value == utteranceId) {
                    _isPlaying.value = false
                    _currentUtteranceId.value = null
                }
            }

            override fun onError(utteranceId: String?) {
                if (_currentUtteranceId.value == utteranceId) {
                    fail("Deutsche TTS-Wiedergabe fehlgeschlagen. Bitte erneut versuchen.")
                }
            }
        })
    }

    fun speak(text: String, speechRate: Float = 1.0f, utteranceId: String = "c1_speech") {
        if (!isInitialized) {
            if (_errorMessage.value == null) {
                fail("Deutsche Sprachausgabe ist noch nicht bereit. Bitte erneut versuchen.")
            }
            return
        }
        stop()
        _errorMessage.value = null
        try {
            tts?.setSpeechRate(speechRate.coerceIn(0.7f, 1.6f))
            tts?.setPitch(1.0f)
            _currentUtteranceId.value = utteranceId
            if (tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) != TextToSpeech.SUCCESS) {
                fail("Deutsche TTS-Wiedergabe konnte nicht gestartet werden.")
            }
        } catch (e: Exception) {
            fail("Deutsche TTS-Wiedergabe fehlgeschlagen.")
            Log.e("AudioSpeechHelper", "TTS speak failed", e)
        }
    }

    fun speakAs(text: String, profile: SpeakerProfile, utteranceId: String = "c1_speech") {
        if (!isInitialized) {
            if (_errorMessage.value == null) {
                fail("Deutsche Sprachausgabe ist noch nicht bereit. Bitte erneut versuchen.")
            }
            return
        }
        stop()
        _errorMessage.value = null
        try {
            tts?.setSpeechRate(profile.rate.coerceIn(0.7f, 1.6f))
            tts?.setPitch(profile.pitch.coerceIn(0.7f, 1.3f))
            _currentUtteranceId.value = utteranceId
            if (tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) != TextToSpeech.SUCCESS) {
                fail("Deutsche Mehrstimmen-Wiedergabe konnte nicht gestartet werden.")
            }
        } catch (e: Exception) {
            fail("Deutsche Mehrstimmen-Wiedergabe fehlgeschlagen.")
            Log.e("AudioSpeechHelper", "Speaker profile playback failed", e)
        }
    }

    fun stop() {
        _isPlaying.value = false
        _currentUtteranceId.value = null
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("AudioSpeechHelper", "TTS stop failed", e)
        }
    }

    fun shutdown() {
        isShutdown = true
        isInitialized = false
        stop()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("AudioSpeechHelper", "TTS shutdown failed", e)
        }
        tts = null
    }
}
