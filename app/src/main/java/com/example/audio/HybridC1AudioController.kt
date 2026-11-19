@file:OptIn(com.google.firebase.ai.type.PublicPreviewAPI::class)

package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.domain.model.PersonalizedExercise
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.MultiSpeakerVoiceConfig
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.SpeakerVoiceConfig
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.security.MessageDigest

private const val TAG = "HybridC1Audio"
private const val GEMINI_TTS_MODEL = "gemini-3.1-flash-tts-preview"

@Stable
class HybridC1AudioController(
    private val context: Context,
    private val scope: CoroutineScope
) {
    var isPlaying by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isPodcastStream by mutableStateOf(false)
        private set
    var activeKey by mutableStateOf<String?>(null)
        private set
    var engineBadge by mutableStateOf("Gemini 3.1 Studio Audio")
        private set
    var playbackSpeed by mutableFloatStateOf(1.05f)
        private set
    var positionMs by mutableIntStateOf(0)
        private set
    var durationMs by mutableIntStateOf(0)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var mediaPlayer: MediaPlayer? = null
    private var activeJob: Job? = null
    private var progressJob: Job? = null

    private val cacheDir: File by lazy {
        File(context.cacheDir, "gemini_studio_wav").apply { mkdirs() }
    }

    fun playDialogueScene(
        exercise: PersonalizedExercise,
        speed: Float = playbackSpeed
    ) {
        val key = "dlg_${exercise.id}"
        if (isPlaying && activeKey == key) {
            stop()
            return
        }
        playbackSpeed = speed
        activeJob?.cancel()
        errorMessage = null
        activeJob = scope.launch {
            isLoading = true
            isPodcastStream = false
            activeKey = key
            engineBadge = "Gemini 3.1 Studio · Mehrsprecher"
            try {
                val wavFile = getOrGenerateMultiSpeakerWav(exercise)
                playLocalWavFile(wavFile, speed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Dialogue audio generation failed", e)
                isPlaying = false
                errorMessage = "Studio-Audio nicht erreichbar (${e.localizedMessage ?: "Netzwerk/AppCheck prüfen"}). Öffnen Sie unten das Transkript oder streamen Sie einen Echt-Podcast."
            } finally {
                isLoading = false
            }
        }
    }

    fun speakPhrase(
        phrase: String,
        speed: Float = 1.0f
    ) {
        val trimmed = phrase.trim()
        if (trimmed.isEmpty()) return
        val key = "phr_${sha256(trimmed).take(12)}"
        if (isPlaying && activeKey == key) {
            stop()
            return
        }
        playbackSpeed = speed
        activeJob?.cancel()
        errorMessage = null
        activeJob = scope.launch {
            isLoading = true
            isPodcastStream = false
            activeKey = key
            engineBadge = "Gemini 3.1 Studio · Einzelstimme"
            try {
                val wavFile = getOrGenerateSingleVoiceWav(trimmed)
                playLocalWavFile(wavFile, speed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Phrase audio generation failed", e)
                isPlaying = false
                errorMessage = "Studio-Audio gerade nicht verfügbar (${e.localizedMessage ?: "Verbindung prüfen"})."
            } finally {
                isLoading = false
            }
        }
    }

    fun togglePodcastStream(
        episode: PodcastEpisodeItem,
        speed: Float = 1.0f
    ) {
        val key = "pod_${episode.audioUrl.hashCode()}"
        if (isPlaying && activeKey == key) {
            stop()
            return
        }
        playbackSpeed = speed
        activeJob?.cancel()
        errorMessage = null
        activeJob = scope.launch {
            isLoading = true
            isPodcastStream = true
            activeKey = key
            engineBadge = "Echt-Rundfunk MP3 · ${episode.sourceName}"
            try {
                streamRemoteMp3(episode.audioUrl, speed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Podcast MP3 stream failed", e)
                isPlaying = false
                errorMessage = "Stream konnte nicht gestartet werden (${e.localizedMessage ?: "Verbindung prüfen"})."
            } finally {
                isLoading = false
            }
        }
    }

    fun updateSpeed(newSpeed: Float) {
        val clamped = newSpeed.coerceIn(0.75f, 1.45f)
        playbackSpeed = clamped
        runCatching {
            val mp = mediaPlayer
            if (mp != null && mp.isPlaying) {
                mp.playbackParams = PlaybackParams().setSpeed(clamped)
            }
        }
    }

    fun seekByMs(deltaMs: Int) {
        val mp = mediaPlayer ?: return
        runCatching {
            val dur = mp.duration.takeIf { it > 0 } ?: return
            val target = (mp.currentPosition + deltaMs).coerceIn(0, dur)
            mp.seekTo(target)
            positionMs = target
        }
    }

    fun stop() {
        activeJob?.cancel()
        progressJob?.cancel()
        runCatching {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.reset()
                mp.release()
            }
        }
        mediaPlayer = null
        isPlaying = false
        isLoading = false
        positionMs = 0
        durationMs = 0
    }

    fun release() {
        stop()
    }

    private suspend fun getOrGenerateMultiSpeakerWav(ex: PersonalizedExercise): File {
        val speakerNames = ex.transcriptTurns.map { it.speaker.trim() }.distinct()
        val s1Name = speakerNames.getOrElse(0) { ex.speakers.getOrNull(0)?.name ?: "Sprecher 1" }
        val s2Name = speakerNames.getOrElse(1) { ex.speakers.getOrNull(1)?.name ?: "Sprecher 2" }

        val scriptLines = if (ex.transcriptTurns.isNotEmpty()) {
            ex.transcriptTurns.joinToString("\n") { turn ->
                val tag = if (turn.speaker.trim() == s1Name) "S1" else "S2"
                "$tag: ${turn.line.trim()}"
            }
        } else {
            "S1: ${ex.transcript}"
        }

        val varietyNote = ex.audioRequirements.regionalVariety.ifBlank { "natürliches gesprochenes Deutsch" }
        val styleNote = ex.audioRequirements.deliveryStyle.ifBlank { "authentisches Tempo, spontane Intonation" }
        val cacheKey = sha256("multi_v2_${ex.id}_${scriptLines}")
        val cachedFile = File(cacheDir, "$cacheKey.wav")
        if (cachedFile.exists() && cachedFile.length() > 128L) {
            return cachedFile
        }

        val voice1 = pickVoiceForSpeaker(s1Name, isFirst = true)
        val voice2 = pickVoiceForSpeaker(s2Name, isFirst = false)

        val model = Firebase.ai.generativeModel(
            modelName = GEMINI_TTS_MODEL,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.AUDIO)
                speechConfig = SpeechConfig(
                    multiSpeakerVoiceConfig = MultiSpeakerVoiceConfig(
                        speakerVoiceConfigs = listOf(
                            SpeakerVoiceConfig(speaker = "S1", voice = Voice(voice1)),
                            SpeakerVoiceConfig(speaker = "S2", voice = Voice(voice2))
                        )
                    ),
                    languageCode = "de-DE"
                )
            }
        )

        val prompt = """
            Sprich diesen deutschen Dialog maximal natürlich und authentisch ($varietyNote, $styleNote):
            $scriptLines
        """.trimIndent()

        val response = withTimeout(60_000L) {
            model.generateContent(prompt)
        }

        val audioPart = response.candidates.firstOrNull()?.content?.parts
            ?.filterIsInstance<InlineDataPart>()
            ?.firstOrNull()
            ?: error("Gemini TTS lieferte keine Audiospur.")

        val wavBytes = if (audioPart.mimeType.contains("wav", ignoreCase = true)) {
            audioPart.inlineData
        } else {
            val sr = WavPcmEncoder.extractSampleRateFromMime(audioPart.mimeType)
            WavPcmEncoder.wrapPcm16MonoToWav(audioPart.inlineData, sampleRate = sr)
        }

        withContext(Dispatchers.IO) {
            cachedFile.writeBytes(wavBytes)
        }
        return cachedFile
    }

    private suspend fun getOrGenerateSingleVoiceWav(phrase: String): File {
        val cacheKey = sha256("single_v2_$phrase")
        val cachedFile = File(cacheDir, "$cacheKey.wav")
        if (cachedFile.exists() && cachedFile.length() > 128L) {
            return cachedFile
        }

        val model = Firebase.ai.generativeModel(
            modelName = GEMINI_TTS_MODEL,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.AUDIO)
                speechConfig = SpeechConfig(
                    voice = Voice("Charon"),
                    languageCode = "de-DE"
                )
            }
        )

        val response = withTimeout(45_000L) {
            model.generateContent("Lies diesen deutschen Satz klar, idiomatisch und mit natürlicher Betonung vor:\n$phrase")
        }

        val audioPart = response.candidates.firstOrNull()?.content?.parts
            ?.filterIsInstance<InlineDataPart>()
            ?.firstOrNull()
            ?: error("Gemini TTS lieferte keine Audiospur.")

        val wavBytes = if (audioPart.mimeType.contains("wav", ignoreCase = true)) {
            audioPart.inlineData
        } else {
            val sr = WavPcmEncoder.extractSampleRateFromMime(audioPart.mimeType)
            WavPcmEncoder.wrapPcm16MonoToWav(audioPart.inlineData, sampleRate = sr)
        }

        withContext(Dispatchers.IO) {
            cachedFile.writeBytes(wavBytes)
        }
        return cachedFile
    }

    private fun playLocalWavFile(file: File, speed: Float) {
        cleanupPlayerOnly()
        val player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(file.absolutePath)
            setOnPreparedListener { mp ->
                runCatching { mp.playbackParams = PlaybackParams().setSpeed(speed.coerceIn(0.75f, 1.45f)) }
                this@HybridC1AudioController.durationMs = mp.duration.coerceAtLeast(0)
                this@HybridC1AudioController.isLoading = false
                this@HybridC1AudioController.isPlaying = true
                mp.start()
                startProgressTicker()
            }
            setOnCompletionListener {
                this@HybridC1AudioController.isPlaying = false
                this@HybridC1AudioController.positionMs = 0
                progressJob?.cancel()
            }
            setOnErrorListener { _, what, extra ->
                Log.e(TAG, "Local WAV MediaPlayer error what=$what extra=$extra")
                this@HybridC1AudioController.isPlaying = false
                this@HybridC1AudioController.isLoading = false
                this@HybridC1AudioController.errorMessage = "Audiowiedergabe fehlgeschlagen."
                true
            }
            prepareAsync()
        }
        mediaPlayer = player
    }

    private fun streamRemoteMp3(url: String, speed: Float) {
        cleanupPlayerOnly()
        val player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener { mp ->
                runCatching { mp.playbackParams = PlaybackParams().setSpeed(speed.coerceIn(0.75f, 1.45f)) }
                this@HybridC1AudioController.durationMs = mp.duration.coerceAtLeast(0)
                this@HybridC1AudioController.isLoading = false
                this@HybridC1AudioController.isPlaying = true
                mp.start()
                startProgressTicker()
            }
            setOnCompletionListener {
                this@HybridC1AudioController.isPlaying = false
                this@HybridC1AudioController.positionMs = 0
                progressJob?.cancel()
            }
            setOnErrorListener { _, what, extra ->
                Log.e(TAG, "Stream MP3 MediaPlayer error what=$what extra=$extra")
                this@HybridC1AudioController.isPlaying = false
                this@HybridC1AudioController.isLoading = false
                this@HybridC1AudioController.errorMessage = "Live-Stream konnte nicht geladen werden (HTTP/Netzwerk)."
                true
            }
            prepareAsync()
        }
        mediaPlayer = player
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && isPlaying) {
                val mp = mediaPlayer
                if (mp != null) {
                    runCatching {
                        if (mp.isPlaying) {
                            positionMs = mp.currentPosition.coerceAtLeast(0)
                            if (durationMs <= 0 && mp.duration > 0) durationMs = mp.duration
                        }
                    }
                }
                delay(250L)
            }
        }
    }

    private fun cleanupPlayerOnly() {
        progressJob?.cancel()
        runCatching {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.reset()
                mp.release()
            }
        }
        mediaPlayer = null
    }

    private fun pickVoiceForSpeaker(name: String, isFirst: Boolean): String {
        val lower = name.lowercase()
        val likelyFemale = lower.contains("frau") ||
            lower.contains("kassiererin") ||
            lower.contains("lisa") ||
            lower.contains("sabine") ||
            lower.contains("anna") ||
            lower.contains("maria") ||
            lower.contains("mama") ||
            lower.contains("prüferin") ||
            lower.contains("nachbarin") ||
            lower.contains("mutter")
        return when {
            likelyFemale && isFirst -> "Aoede"
            likelyFemale -> "Kore"
            isFirst -> "Charon"
            else -> "Fenrir"
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

@Composable
fun rememberHybridC1AudioController(): HybridC1AudioController {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val controller = remember(context) { HybridC1AudioController(context, scope) }
    DisposableEffect(controller) {
        onDispose { controller.release() }
    }
    return controller
}
