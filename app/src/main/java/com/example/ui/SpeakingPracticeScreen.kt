package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.PhoneticWordTip
import com.example.data.model.PronunciationAnalysisResult
import com.example.data.model.PronunciationWordResult
import com.example.data.model.SpeakingPrompt
import com.example.data.model.WordPronunciationStatus
import com.example.service.SpeechCaptureState
import com.example.service.SpeechRecognitionHelper
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingPracticeScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        viewModel.stopAudio()
        viewModel.navigateBack()
    }

    // Lazy SpeechRecognitionHelper initialized with context lifecycle
    val speechRecognizerHelper = remember { SpeechRecognitionHelper(context) }
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerHelper.cancel()
            speechRecognizerHelper.destroy()
        }
    }

    val prompts by viewModel.speakingRepository.prompts.collectAsState()
    val activeIdx by viewModel.speakingRepository.currentPromptIndex.collectAsState()
    val analysisResult by viewModel.speakingRepository.latestAnalysisResult.collectAsState()
    val captureState by speechRecognizerHelper.captureState.collectAsState()
    val liveRmsLevel by speechRecognizerHelper.rmsLevel.collectAsState()
    val liveTranscription by speechRecognizerHelper.liveTranscription.collectAsState()
    val recognizerError by speechRecognizerHelper.errorMessage.collectAsState()
    val isPlayingTts by viewModel.speechHelper.isPlaying.collectAsState()
    val ttsError by viewModel.speechHelper.errorMessage.collectAsState()

    val currentPrompt = prompts.getOrNull(activeIdx) ?: return

    var selectedWordForDetail by remember(activeIdx) { mutableStateOf<PronunciationWordResult?>(null) }
    var showDemo by remember(activeIdx) { mutableStateOf(false) }

    LaunchedEffect(captureState) {
        if (captureState == SpeechCaptureState.PREPARING || captureState == SpeechCaptureState.LISTENING) {
            showDemo = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording(speechRecognizerHelper, viewModel, currentPrompt)
        } else {
            Toast.makeText(
                context,
                "Mikrofon-Berechtigung erforderlich für die Spracherkennung.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Sprechübung · Textvergleich",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "Transkript-Abgleich, keine akustische Bewertung",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            speechRecognizerHelper.cancel()
                            viewModel.stopAudio()
                            viewModel.navigateBack()
                        },
                        modifier = Modifier.testTag("speaking_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = BerlinTheme.ink
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = {
                            speechRecognizerHelper.cancel()
                            viewModel.stopAudio()
                            viewModel.navigateTo(Screen.ConversationSimulator)
                        },
                        shape = RoundedCornerShape(99.dp),
                        color = BerlinTheme.surface2,
                        border = BorderStroke(1.dp, BerlinTheme.line),
                        modifier = Modifier.padding(end = 8.dp).testTag("btn_switch_to_conversation_sim")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = BerlinTheme.ink, modifier = Modifier.size(15.dp))
                            Text("Simulator", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BerlinTheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("speaking_practice_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Unbewertete Sprechübung: Verglichen wird nur das erkannte Transkript mit dem Zieltext. Aussprache, Phoneme, Tempo und Prosodie werden nicht gemessen. Kein C1- oder Prüfungsnachweis.",
                    color = BerlinTheme.ink2,
                    fontSize = 13.sp
                )
                Text(
                    text = "Längere Antworten werden, sofern vom Geräteservice unterstützt, in Sprachsegmente geteilt und zusammengesetzt. Prüfe das Transkript vor dem Speichern.",
                    color = BerlinTheme.ink2,
                    fontSize = 12.sp
                )
            }
            // -------------------------------------------------------------
            // 1. Horizontal Prompt Selector Carousel
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "C1 SPRECH-AUFGABEN",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BerlinTheme.ink2,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${activeIdx + 1} von ${prompts.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BerlinTheme.accentInk
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(prompts) { idx, p ->
                            val isSelected = idx == activeIdx
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) BerlinTheme.hero else BerlinTheme.surface,
                                border = BorderStroke(1.dp, if (isSelected) BerlinTheme.accent else BerlinTheme.line),
                                modifier = Modifier
                                    .clickable {
                                        speechRecognizerHelper.cancel()
                                        viewModel.speakingRepository.selectPrompt(idx)
                                    }
                                    .testTag("prompt_chip_$idx")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) BerlinTheme.accent else BerlinTheme.line)
                                    )
                                    Text(
                                        text = p.title.take(24) + "...",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BerlinTheme.onHero else BerlinTheme.ink
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 2. Hero Target Sentence Card (.hero styling)
            // -------------------------------------------------------------
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = BerlinTheme.hero,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth().testTag("speaking_hero_prompt_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = BerlinTheme.surface2.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = currentPrompt.category,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.accent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = BerlinTheme.accentSoft.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = currentPrompt.difficultyLevel,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.onHero,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "„${currentPrompt.targetSentence}“",
                            fontFamily = BarlowFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BerlinTheme.onHero,
                            lineHeight = 28.sp
                        )

                        Text(
                            text = "Kontext: ${currentPrompt.contextBrief}",
                            fontSize = 12.5.sp,
                            color = BerlinTheme.onHero2
                        )

                        // Device-generated German speech, not a human recording.
                        Button(
                            onClick = {
                                viewModel.speechHelper.speak(
                                    currentPrompt.targetSentence,
                                    currentPrompt.audioPromptSpeed,
                                    "speaking_prompt_${currentPrompt.id}"
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_listen_native_prompt"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingTts) BerlinTheme.violet else BerlinTheme.surface2,
                                contentColor = if (isPlayingTts) Color.White else BerlinTheme.ink
                            )
                        ) {
                            Icon(
                                imageVector = if (isPlayingTts) Icons.Default.VolumeUp else Icons.Default.Hearing,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPlayingTts) "Deutsche TTS läuft..." else "Deutsche Sprachausgabe anhören",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Synthetische Gerätestimme (TTS), keine Aufnahme eines Muttersprachlers.",
                            fontSize = 12.sp,
                            color = BerlinTheme.onHero2
                        )
                        ttsError?.let { message ->
                            Text(text = message, fontSize = 12.sp, color = BerlinTheme.onHero)
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 3. Phonetic Difficulties Preview Chips
            // -------------------------------------------------------------
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BerlinTheme.surface,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spellcheck,
                                contentDescription = null,
                                tint = BerlinTheme.sky,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "ALLGEMEINE LAUTTIPPS · NICHT GEMESSEN:",
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.ink2
                            )
                        }

                        currentPrompt.phoneticFocusWords.forEach { focus ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = BerlinTheme.skySoft
                                    ) {
                                        Text(
                                            text = focus.ipa,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BerlinTheme.sky,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "${focus.word} (${focus.focusArea})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = BerlinTheme.ink
                                        )
                                        Text(
                                            text = focus.tipDe,
                                            fontSize = 12.sp,
                                            color = BerlinTheme.ink2,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 4. Live Microphone Recorder & Audio Waveform Visualizer
            // -------------------------------------------------------------
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = BerlinTheme.surface,
                    border = BorderStroke(
                        1.5.dp,
                        if (captureState == SpeechCaptureState.LISTENING) BerlinTheme.coral else BerlinTheme.line
                    ),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth().testTag("microphone_control_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = when (captureState) {
                                SpeechCaptureState.LISTENING -> "MIKROFON AKTIV • JETZT SPRECHEN!"
                                SpeechCaptureState.PROCESSING -> "ERSTELLE TRANSKRIPT..."
                                SpeechCaptureState.PREPARING -> "BEREITE MIKROFON VOR..."
                                else -> "SPRACHAUFNAHME STARTEN"
                            },
                            fontFamily = BarlowCondensedFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (captureState == SpeechCaptureState.LISTENING) BerlinTheme.coral else BerlinTheme.ink2,
                            letterSpacing = 1.sp
                        )

                        // Real-time Audio Waveform Visualization Bars
                        LiveAudioWaveformVisualizer(
                            rmsLevel = liveRmsLevel,
                            isListening = captureState == SpeechCaptureState.LISTENING
                        )

                        // Live Speech Transcription Bubble (if words are spoken)
                        if (liveTranscription.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BerlinTheme.surface2,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = BerlinTheme.accentInk,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "„$liveTranscription“",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BerlinTheme.ink
                                    )
                                }
                            }
                        }

                        // Big Tactile Microphone Button (72dp)
                        val isListening = captureState == SpeechCaptureState.LISTENING
                        Box(contentAlignment = Alignment.Center) {
                            Surface(
                                onClick = {
                                    if (isListening) {
                                        speechRecognizerHelper.stopListening()
                                    } else {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED

                                        if (hasPermission) {
                                            startRecording(speechRecognizerHelper, viewModel, currentPrompt)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },
                                shape = CircleShape,
                                color = if (isListening) BerlinTheme.coral else BerlinTheme.accent,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .size(76.dp)
                                    .testTag("btn_capture_microphone")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = if (isListening) "Aufnahme stoppen" else "Aufnahme starten",
                                        tint = if (isListening) Color.White else BerlinTheme.onAccent,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isListening) {
                                "Tippen zum Stoppen der Aufnahme"
                            } else {
                                "Tippen, um die deutsche Phrase laut einzusprechen"
                            },
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2,
                            textAlign = TextAlign.Center
                        )

                        // Recognition Error or Fallback Warning
                        if (recognizerError != null) {
                            Text(
                                text = recognizerError!!,
                                fontSize = 12.sp,
                                color = BerlinTheme.coral,
                                textAlign = TextAlign.Center
                            )
                        }

                        // A static demo cannot submit a recognized response or record practice.
                        OutlinedButton(
                            onClick = {
                                speechRecognizerHelper.cancel()
                                viewModel.stopAudio()
                                viewModel.speakingRepository.clearResult()
                                showDemo = !showDemo
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BerlinTheme.line),
                            modifier = Modifier.testTag("btn_simulate_speech_test")
                        ) {
                            Icon(imageVector = Icons.Default.AutoMode, contentDescription = null, modifier = Modifier.size(16.dp), tint = BerlinTheme.ink2)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Demo anzeigen · keine Aufnahme", fontSize = 12.sp, color = BerlinTheme.ink2)
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 5. Transcript agreement feedback
            // -------------------------------------------------------------
            if (showDemo) {
                item {
                    Text(
                        text = "Demo-Transkript (vorgegebener Text): „${currentPrompt.targetSentence}“\nKeine Mikrofonaufnahme, keine Bewertung, kein Aktivitätseintrag und kein Bestehensnachweis.",
                        fontSize = 13.sp,
                        color = BerlinTheme.ink2,
                        modifier = Modifier.testTag("speaking_demo_notice")
                    )
                }
            }
            if (analysisResult != null) {
                item {
                    PronunciationFeedbackCard(
                        result = analysisResult!!,
                        prompt = currentPrompt,
                        onWordClick = { selectedWordForDetail = it },
                        onRetryClick = {
                            viewModel.speakingRepository.clearResult()
                            startRecording(speechRecognizerHelper, viewModel, currentPrompt)
                        },
                        onNextPromptClick = {
                            viewModel.speakingRepository.nextPrompt()
                        },
                        onListenWord = { word ->
                            viewModel.speechHelper.speak(word, 0.9f)
                        }
                    )
                }
                item {
                    AiFeedbackShareButton(
                        task = "Zielsatz: ${currentPrompt.targetSentence}. Vergleiche ihn mit dem erkannten Text und benenne nur Textabweichungen. Prüfe keine Aussprache.",
                        response = analysisResult!!.transcribedText,
                        evidenceType = "ASR-Transkript; keine Audioaufnahme und kein Beleg für Aussprache oder Hörgenauigkeit"
                    )
                }
            }
        }
    }

    // Word Phonetic Detail Modal
    if (selectedWordForDetail != null) {
        WordPhoneticDetailDialog(
            wordResult = selectedWordForDetail!!,
            onDismiss = { selectedWordForDetail = null },
            onSpeak = { word -> viewModel.speechHelper.speak(word, 0.9f) }
        )
    }
}

/**
 * Animated Live Audio Waveform Visualizer
 * Reacts dynamically to real-time RMS microphone decibel levels.
 */
@Composable
fun LiveAudioWaveformVisualizer(
    rmsLevel: Float,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 11
    val baseHeights = listOf(14.dp, 22.dp, 30.dp, 40.dp, 48.dp, 56.dp, 48.dp, 40.dp, 30.dp, 22.dp, 14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val scaleFactor = if (isListening) {
                val waveOffset = (i % 3) * 0.2f
                ((rmsLevel / 10f) * 1.5f + waveOffset).coerceIn(0.25f, 1.4f)
            } else {
                0.2f
            }

            val dynamicHeight = (baseHeights[i].value * scaleFactor).dp.coerceIn(8.dp, 58.dp)

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(99.dp))
                    .background(
                        if (isListening) {
                            Brush.verticalGradient(
                                colors = listOf(BerlinTheme.accent, BerlinTheme.accentInk)
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(BerlinTheme.line, BerlinTheme.line)
                            )
                        }
                    )
            )
        }
    }
}

/** Text alignment feedback only; the legacy result type is retained for caller compatibility. */
@Composable
fun PronunciationFeedbackCard(
    result: PronunciationAnalysisResult,
    prompt: SpeakingPrompt,
    onWordClick: (PronunciationWordResult) -> Unit,
    onRetryClick: () -> Unit,
    onNextPromptClick: () -> Unit,
    onListenWord: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = BerlinTheme.surface,
        border = BorderStroke(1.5.dp, BerlinTheme.line),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().testTag("pronunciation_feedback_card")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Agreement with the target text is not a pass/fail or CEFR classification.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Transkript-Übereinstimmung",
                        fontFamily = BarlowCondensedFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.ink
                    )
                    Text(
                        text = "Keine Aussprache- oder GER-Bewertung",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BerlinTheme.ink2
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = BerlinTheme.surface2,
                    border = BorderStroke(1.5.dp, BerlinTheme.line),
                    modifier = Modifier.size(58.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${result.accuracyScore}%",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = BerlinTheme.ink
                        )
                    }
                }
            }

            // No acoustic metrics are available from text recognition.
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BerlinTheme.surface2,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Textvergleich mit dem Zieltext; Erkennungsfehler sind möglich. Artikulation, Phoneme, Tempo und Prosodie: nicht gemessen. Kein Bestehensnachweis.",
                        fontSize = 12.sp,
                        color = BerlinTheme.ink2
                    )
                    Text(text = "Erkannt: „${result.transcribedText}“", fontSize = 13.sp, color = BerlinTheme.ink)
                }
            }

            // Interactive Word-by-Word Breakdown
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TEXTABGLEICH · TIPPEN FÜR WORTDETAILS:",
                    fontFamily = BarlowCondensedFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = BerlinTheme.ink2
                )

                // Render word chips
                OptFlowRow(modifier = Modifier.fillMaxWidth()) {
                    result.wordResults.forEach { wordRes ->
                        val (chipBg, chipBorder, chipText) = when (wordRes.status) {
                            WordPronunciationStatus.PERFECT -> Triple(BerlinTheme.okBg, BerlinTheme.ok, BerlinTheme.ok)
                            WordPronunciationStatus.ACCEPTABLE -> Triple(BerlinTheme.accentSoft, BerlinTheme.accent, BerlinTheme.accentInk)
                            WordPronunciationStatus.MISPRONOUNCED -> Triple(BerlinTheme.coralSoft, BerlinTheme.coral, BerlinTheme.coral)
                            WordPronunciationStatus.MISSING -> Triple(BerlinTheme.surface2, BerlinTheme.line, BerlinTheme.ink2)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = chipBg,
                            border = BorderStroke(1.5.dp, chipBorder),
                            modifier = Modifier
                                .padding(end = 6.dp, bottom = 6.dp)
                                .clickable { onWordClick(wordRes) }
                                .testTag("word_chip_${wordRes.expectedWord}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = wordRes.expectedWord,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = chipText
                                )
                                if (wordRes.status == WordPronunciationStatus.PERFECT) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = chipText, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Actionable Pedagogical Tips
            val learningNotes = listOf(
                "Farben zeigen nur Textähnlichkeit: gleich, ähnlich, abweichend oder im Transkript nicht zugeordnet.",
                "Die Lauttipps oben sind allgemeines Lernmaterial, keine Diagnose deiner Aussprache."
            )
            if (learningNotes.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BerlinTheme.surface,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = BerlinTheme.accentInk, modifier = Modifier.size(16.dp))
                            Text(
                                text = "HINWEISE ZUM TEXTVERGLEICH:",
                                fontFamily = BarlowCondensedFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BerlinTheme.ink2
                            )
                        }
                        learningNotes.forEach { tip ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "•", color = BerlinTheme.accentInk, fontWeight = FontWeight.Bold)
                                Text(text = tip, fontSize = 12.5.sp, color = BerlinTheme.ink, lineHeight = 17.sp)
                            }
                        }
                    }
                }
            }

            // Action Buttons (54dp height, 16dp radius)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetryClick,
                    modifier = Modifier.weight(1f).height(54.dp).testTag("btn_retry_speaking"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, BerlinTheme.line)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Erneut aufnehmen", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = onNextPromptClick,
                    modifier = Modifier.weight(1f).height(54.dp).testTag("btn_next_speaking_prompt"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BerlinTheme.accent,
                        contentColor = BerlinTheme.onAccent
                    )
                ) {
                    Text("Nächste Phrase ›", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun DimensionScoreRow(label: String, score: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = BerlinTheme.ink)
            Text(text = "$score%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
        }
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
            color = if (score >= 75) BerlinTheme.ok else BerlinTheme.coral,
            trackColor = BerlinTheme.surface
        )
    }
}

/**
 * Word Phonetic Detail Modal showing articulatory tips and individual audio pronunciation playback.
 */
@Composable
fun WordPhoneticDetailDialog(
    wordResult: PronunciationWordResult,
    onDismiss: () -> Unit,
    onSpeak: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = wordResult.expectedWord,
                    fontFamily = BarlowCondensedFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BerlinTheme.ink
                )
                IconButton(onClick = { onSpeak(wordResult.expectedWord) }) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Wort anhören", tint = BerlinTheme.accentInk)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (wordResult.ipa != null) {
                    Surface(shape = RoundedCornerShape(8.dp), color = BerlinTheme.skySoft) {
                        Text(
                            text = "IPA: ${wordResult.ipa}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BerlinTheme.sky,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Im Transkript zugeordnet: ${wordResult.spokenWord ?: "— (nicht zugeordnet)"}\nNur Textabgleich, keine akustische Lautanalyse.",
                    fontSize = 13.sp,
                    color = BerlinTheme.ink2
                )

                if (wordResult.phoneticTip != null) {
                    Text(
                        text = "Allgemeiner Lauttipp (nicht gemessen):\n${wordResult.phoneticTip}",
                        fontSize = 13.sp,
                        color = BerlinTheme.ink,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Schließen", fontWeight = FontWeight.Bold, color = BerlinTheme.accentInk)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = BerlinTheme.surface
    )
}

/**
 * Lightweight Flexbox-like flow row for wrapping word tokens.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptFlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // FlowRow is supported natively in compose foundation layout
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        content()
    }
}

private fun startRecording(
    helper: SpeechRecognitionHelper,
    viewModel: C1ViewModel,
    prompt: SpeakingPrompt
) {
    viewModel.stopAudio()
    helper.startListening { transcription, confidence ->
        viewModel.speakingRepository.analyzeSpokenResponse(
            prompt = prompt,
            transcribedText = transcription,
            confidence = confidence
        )
        if (transcription.isNotBlank()) {
            viewModel.recordPracticeActivity("Sprechübung (unbewerteter Transkript-Abgleich)")
        }
    }
}
