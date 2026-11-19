package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

data class ProsodyMetrics(
    val wordsPerMinute: Int = 0,
    val fillerWordCount: Int = 0,
    val detectedFillers: List<String> = emptyList(),
    val durationSeconds: Float = 0f,
    val flowScorePercent: Int = 0,
    val summaryFeedbackDe: String = ""
)

/**
 * Controller for on-device German Speech Recognition with live microphone RMS amplitude
 * and verbal prosody / fluency analysis (WPM, German filler detection, speech flow).
 */
@Stable
class GermanSpeechRecognizerController(private val context: Context) {

    var isListening by mutableStateOf(false)
        private set

    var isAvailable by mutableStateOf(SpeechRecognizer.isRecognitionAvailable(context))
        private set

    var rmsLevel by mutableFloatStateOf(0f)
        private set

    var liveTranscription by mutableStateOf("")
        private set

    var finalTranscription by mutableStateOf("")
        private set

    var prosodyMetrics by mutableStateOf<ProsodyMetrics?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var speechRecognizer: SpeechRecognizer? = null
    private var recordingStartTimeMs: Long = 0L

    private val commonGermanFillers = setOf(
        "ähm", "äh", "öhm", "öh", "mhm", "halt", "quasi", "sozusagen",
        "irgendwie", "gewissermaßen", "also", "eigentlich"
    )

    fun initialize() {
        if (!isAvailable) {
            errorMessage = "Spracherkennung ist auf diesem Gerät nicht verfügbar."
            return
        }
        if (speechRecognizer == null) {
            runCatching {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createListener())
                }
            }.onFailure {
                isAvailable = false
                errorMessage = "Spracherkennung konnte nicht initialisiert werden: ${it.message}"
            }
        }
    }

    private fun createListener(): RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            isListening = true
            errorMessage = null
            recordingStartTimeMs = System.currentTimeMillis()
        }

        override fun onBeginningOfSpeech() {
            isListening = true
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Normalize rmsdB (-2 to ~10 dB) to 0f..1f for visualizer
            rmsLevel = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1f)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            isListening = false
            rmsLevel = 0f
        }

        override fun onError(error: Int) {
            isListening = false
            rmsLevel = 0f
            errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio-Aufnahmefehler."
                SpeechRecognizer.ERROR_CLIENT -> null // normal cancellation
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon-Berechtigung fehlt."
                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Netzwerkfehler bei Spracherkennung."
                SpeechRecognizer.ERROR_NO_MATCH -> "Keine Sprache erkannt. Bitte deutlicher sprechen."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Spracherkennung ist belegt."
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Keine Spracheingabe empfangen."
                else -> "Spracherkennungsfehler ($error)."
            }
        }

        override fun onResults(results: Bundle?) {
            isListening = false
            rmsLevel = 0f
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull().orEmpty()
            if (recognizedText.isNotBlank()) {
                finalTranscription = recognizedText
                liveTranscription = recognizedText
                analyzeProsody(recognizedText)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            partials?.firstOrNull()?.let {
                liveTranscription = it
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun startListening() {
        initialize()
        val recognizer = speechRecognizer ?: return
        errorMessage = null
        finalTranscription = ""
        liveTranscription = ""
        prosodyMetrics = null
        rmsLevel = 0.1f

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.GERMANY.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.GERMANY.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        runCatching {
            recognizer.startListening(intent)
            isListening = true
            recordingStartTimeMs = System.currentTimeMillis()
        }.onFailure {
            isListening = false
            errorMessage = "Konnte Aufnahme nicht starten: ${it.message}"
        }
    }

    fun stopListening() {
        runCatching {
            speechRecognizer?.stopListening()
        }
        isListening = false
        rmsLevel = 0f
    }

    fun cancelListening() {
        runCatching {
            speechRecognizer?.cancel()
        }
        isListening = false
        rmsLevel = 0f
    }

    fun analyzeProsody(text: String, customDurationSec: Float? = null) {
        val durationSec = customDurationSec ?: if (recordingStartTimeMs > 0L) {
            max(0.8f, (System.currentTimeMillis() - recordingStartTimeMs) / 1000f)
        } else {
            max(0.8f, text.split(Regex("\\s+")).filter { it.isNotBlank() }.size * 0.45f)
        }
        val tokens = text.lowercase(Locale.GERMANY)
            .split(Regex("[\\s,;.!?-]+"))
            .filter { it.isNotBlank() }

        val wordCount = tokens.size
        val wpm = ((wordCount / durationSec) * 60f).roundToInt().coerceIn(0, 240)

        val foundFillers = tokens.filter { it in commonGermanFillers }
        val fillerCount = foundFillers.size

        // C1 German Conversational Benchmarks: Ideal speed ~110-145 WPM, <2 fillers per 30 words
        val tempoScore = when (wpm) {
            in 105..150 -> 100
            in 85..104 -> 85
            in 151..175 -> 88
            in 60..84 -> 68
            else -> 55
        }
        val fillerPenalty = (fillerCount * 12).coerceAtMost(35)
        val flowScore = (tempoScore - fillerPenalty).coerceIn(40, 100)

        val feedback = when {
            wpm in 105..150 && fillerCount == 0 -> "Exzellenter C1-Sprechfluss: Natürlich, flüssig und ohne Zögern."
            wpm in 105..150 -> "Gutes C1-Tempo ($wpm WPM), aber achte auf $fillerCount Füllpartikel."
            wpm < 85 -> "Leicht stockend ($wpm WPM). Versuche Gedanken im Satzrahmen flüssiger zu verbinden."
            else -> "Sehr schnelles Sprechtempo ($wpm WPM). Achte auf präzise Artikulation im Nominalstil."
        }

        prosodyMetrics = ProsodyMetrics(
            wordsPerMinute = wpm,
            fillerWordCount = fillerCount,
            detectedFillers = foundFillers.distinct(),
            durationSeconds = durationSec,
            flowScorePercent = flowScore,
            summaryFeedbackDe = feedback
        )
    }

    fun release() {
        runCatching {
            speechRecognizer?.destroy()
        }
        speechRecognizer = null
    }
}

@Composable
fun rememberGermanSpeechRecognizerController(): GermanSpeechRecognizerController {
    val context = LocalContext.current
    val controller = remember(context) { GermanSpeechRecognizerController(context) }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }
    return controller
}
