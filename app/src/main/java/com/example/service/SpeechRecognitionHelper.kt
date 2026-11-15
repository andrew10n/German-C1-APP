package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

enum class SpeechCaptureState {
    IDLE,
    PREPARING,
    LISTENING,
    PROCESSING,
    ERROR
}

class SpeechRecognitionHelper(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private var speechRecognizer: SpeechRecognizer? = null

    private val _captureState = MutableStateFlow(SpeechCaptureState.IDLE)
    val captureState: StateFlow<SpeechCaptureState> = _captureState.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _liveTranscription = MutableStateFlow("")
    val liveTranscription: StateFlow<String> = _liveTranscription.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var onSpeechResultListener: ((String, Float) -> Unit)? = null
    private val finalizedSegments = mutableListOf<String>()

    val isRecognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    init {
        initializeRecognizer()
    }

    private fun initializeRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createListener())
                }
                Log.d("SpeechRecognitionHelper", "SpeechRecognizer initialized successfully")
            } else {
                Log.w("SpeechRecognitionHelper", "SpeechRecognizer is not available on this device")
            }
        } catch (e: Exception) {
            Log.e("SpeechRecognitionHelper", "Error initializing SpeechRecognizer", e)
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _captureState.value = SpeechCaptureState.LISTENING
                _errorMessage.value = null
            }

            override fun onBeginningOfSpeech() {
                _captureState.value = SpeechCaptureState.LISTENING
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize and expose RMS level (decibels) to feed live audio wave visualizer
                val normalized = (rmsdB + 2f).coerceIn(0f, 10f)
                _rmsLevel.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _captureState.value = SpeechCaptureState.PROCESSING
                _rmsLevel.value = 0f
            }

            override fun onError(error: Int) {
                _captureState.value = SpeechCaptureState.IDLE
                _rmsLevel.value = 0f
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio-Aufnahmefehler. Bitte Mikrofon prüfen."
                    SpeechRecognizer.ERROR_CLIENT -> "Client-Verbindungsfehler im Spracherkenner."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon-Berechtigung fehlt."
                    SpeechRecognizer.ERROR_NETWORK -> "Netzwerkfehler bei Spracherkennung."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Keine Sprache erkannt. Bitte deutlicher sprechen."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Spracherkenner beschäftigt. Bitte erneut versuchen."
                    SpeechRecognizer.ERROR_SERVER -> "Serverfehler bei Spracherkennung."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Zeitüberschreitung. Bitte früher beginnen zu sprechen."
                    else -> "Spracherkennungsfehler ($error)."
                }
                Log.w("SpeechRecognitionHelper", "Speech recognition error: $message (code $error)")
                _errorMessage.value = message
            }

            override fun onResults(results: Bundle?) {
                _captureState.value = SpeechCaptureState.IDLE
                _rmsLevel.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val scores = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                val bestMatch = matches?.firstOrNull() ?: ""
                val confidence = scores?.firstOrNull() ?: 0.85f

                val completeTranscript = SpeechRecognitionText.merge(finalizedSegments, bestMatch)
                finalizedSegments.clear()
                _liveTranscription.value = completeTranscript
                onSpeechResultListener?.invoke(completeTranscript, confidence)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!partial.isNullOrBlank()) {
                    _liveTranscription.value = SpeechRecognitionText.merge(finalizedSegments, partial)
                }
            }

            override fun onSegmentResults(segmentResults: Bundle) {
                val segment = segmentResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()
                    .orEmpty()
                if (segment.isNotBlank()) {
                    finalizedSegments += segment
                    _liveTranscription.value = finalizedSegments.joinToString(" ")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    fun startListening(onResult: (transcription: String, confidence: Float) -> Unit) {
        onSpeechResultListener = onResult
        finalizedSegments.clear()
        _errorMessage.value = null
        _liveTranscription.value = ""
        _captureState.value = SpeechCaptureState.PREPARING

        if (speechRecognizer == null) {
            initializeRecognizer()
        }

        val recognizer = speechRecognizer
        if (recognizer == null || !isRecognitionAvailable) {
            // SpeechRecognizer service unavailable on this hardware or emulator:
            // Gracefully inform state and allow fallback
            _captureState.value = SpeechCaptureState.IDLE
            _errorMessage.value = "Geräte-Spracherkennungsdienst nicht initialisierbar. Manuelle Eingabe oder Test-Simulation möglich."
            return
        }

        try {
            val intent = buildGermanLongTurnRecognitionIntent(
                packageName = context.packageName,
                apiLevel = android.os.Build.VERSION.SDK_INT
            )
            recognizer.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechRecognitionHelper", "Failed to start listening", e)
            _captureState.value = SpeechCaptureState.IDLE
            _errorMessage.value = "Starten der Spracherkennung fehlgeschlagen: ${e.localizedMessage}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechRecognitionHelper", "Failed to stop listening", e)
        }
        _captureState.value = SpeechCaptureState.PROCESSING
    }

    fun cancel() {
        try {
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.e("SpeechRecognitionHelper", "Failed to cancel listening", e)
        }
        _captureState.value = SpeechCaptureState.IDLE
        _rmsLevel.value = 0f
        _liveTranscription.value = ""
        finalizedSegments.clear()
        onSpeechResultListener = null
    }

    /**
     * Diagnostic testing helper to simulate spoken speech input with realistic live waveform
     * when testing without physical voice in automated tests or when hardware recognizer is offline.
     */
    fun simulateSpeech(text: String, onResult: (String, Float) -> Unit) {
        scope.launch {
            _captureState.value = SpeechCaptureState.LISTENING
            _liveTranscription.value = ""
            // Animate realistic RMS wave pulses
            for (i in 1..4) {
                _rmsLevel.value = (3f + (i % 3) * 2.5f)
                delay(120)
            }
            _captureState.value = SpeechCaptureState.PROCESSING
            _rmsLevel.value = 0f
            _liveTranscription.value = text
            delay(150)
            _captureState.value = SpeechCaptureState.IDLE
            onResult(text, 0.92f)
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechRecognitionHelper", "Error destroying speech recognizer", e)
        }
        speechRecognizer = null
    }
}

internal fun buildGermanLongTurnRecognitionIntent(packageName: String, apiLevel: Int): Intent =
    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5_000)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5_000)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10_000)
        if (apiLevel >= android.os.Build.VERSION_CODES.TIRAMISU) {
            putExtra(
                RecognizerIntent.EXTRA_SEGMENTED_SESSION,
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS
            )
        }
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
    }

internal object SpeechRecognitionText {
    fun merge(finalizedSegments: List<String>, currentResult: String): String {
        val segments = finalizedSegments.map(String::trim).filter(String::isNotBlank)
        val current = currentResult.trim()
        if (segments.isEmpty()) return current
        if (current.isBlank()) return segments.joinToString(" ")

        val transcript = segments.joinToString(" ")
        val normalizedCurrent = normalize(current)
        val normalizedTranscript = normalize(transcript)
        return when {
            normalizedTranscript.endsWith(normalizedCurrent) -> transcript
            normalizedCurrent.startsWith(normalizedTranscript) -> current
            else -> "$transcript $current"
        }
    }

    private fun normalize(value: String) = value
        .lowercase(Locale.ROOT)
        .replace(Regex("[\\p{Punct}\\s]+"), "")
}
