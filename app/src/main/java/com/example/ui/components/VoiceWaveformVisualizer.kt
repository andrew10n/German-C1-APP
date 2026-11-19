package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicNone
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.audio.GermanSpeechRecognizerController
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.abs
import kotlin.math.sin

/**
 * Animated Real-Time Live Microphone Waveform Visualizer.
 * Displays 18 dynamic audio frequency bars driven by microphone decibel amplitude (rmsLevel).
 */
@Composable
fun LiveMicrophoneWaveformCanvas(
    isListening: Boolean,
    rmsLevel: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val inf = rememberInfiniteTransition(label = "micWaveInf")
    val phase by inf.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 650 else 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "micPhase"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(44.dp)) {
        val barCount = 18
        val spacing = size.width / barCount
        val barWidth = (spacing * 0.50f).coerceIn(4f, 10f)

        for (i in 0 until barCount) {
            val wave = sin(phase + i * 0.45f)
            val baseAmp = if (isListening) {
                0.22f + (rmsLevel * 0.70f) * abs(wave)
            } else {
                0.12f + 0.08f * (i % 3) / 2f
            }
            val barHeight = (size.height * baseAmp.coerceIn(0.12f, 0.95f))
            val x = i * spacing + spacing / 2f
            val yStart = (size.height - barHeight) / 2f
            val yEnd = yStart + barHeight

            drawLine(
                color = if (isListening) accentColor else accentColor.copy(alpha = 0.35f),
                start = Offset(x, yStart),
                end = Offset(x, yEnd),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * State-of-the-Art German Voice Input Deck:
 * Integrates speech-to-text, live waveform feedback, real-time WPM,
 * German filler-word detection (ähm/halt/sozusagen), and prosody scoring.
 */
@Composable
fun GermanVoiceInputDeck(
    speechController: GermanSpeechRecognizerController,
    onTranscriptionAccepted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val context = LocalContext.current
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasRecordPermission = granted
        if (granted) {
            speechController.startListening()
        }
    }

    val inf = rememberInfiniteTransition(label = "pulseRing")
    val pulseScale by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "micScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("german_voice_input_deck"),
        shape = RoundedCornerShape(22.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(
            width = if (speechController.isListening) 1.8.dp else 1.dp,
            color = if (speechController.isListening) palette.primaryAccent else palette.borderStroke
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Status + Listening Mode Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (speechController.isListening) palette.warningRed else palette.tertiaryAccent)
                    )
                    Text(
                        text = if (speechController.isListening) "MIKROFON AKTIV · SPRACHE WIRD ERKANNT" else "DEUTSCH SPRACH-INPUT (C1)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (speechController.isListening) palette.warningRed else palette.imprintStageSubtext
                    )
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = palette.secondaryAccent.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "de-DE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Real-time live audio waveform canvas
            LiveMicrophoneWaveformCanvas(
                isListening = speechController.isListening,
                rmsLevel = speechController.rmsLevel,
                accentColor = palette.primaryAccent
            )

            // Transcription text preview (live updates while speaking)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.imprintStageSurface,
                border = BorderStroke(1.dp, palette.borderStroke.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val displayCaption = when {
                        speechController.isListening && speechController.liveTranscription.isBlank() -> "Ich höre zu... Jetzt auf Deutsch sprechen."
                        speechController.liveTranscription.isNotBlank() -> speechController.liveTranscription
                        speechController.finalTranscription.isNotBlank() -> speechController.finalTranscription
                        else -> "Tippe auf das Mikrofon, um deine C1-Antwort frei einzusprechen."
                    }
                    Text(
                        text = displayCaption,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (speechController.isListening) FontWeight.Medium else FontWeight.Normal
                        ),
                        color = if (speechController.isListening) palette.primaryAccent else palette.imprintStageText
                    )
                }
            }

            // Error display if speech recognition encounters an issue
            speechController.errorMessage?.let { error ->
                Text(
                    text = "Hinweis: $error",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.warningRed
                )
            }

            // Prosody & Fluency Telemetry Metrics Card (WPM, Fillers, Flow)
            speechController.prosodyMetrics?.let { metrics ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.elevatedSurface,
                    border = BorderStroke(1.2.dp, palette.secondaryAccent.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.Speed, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "SPRECHFLUSS & PROSODIE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.secondaryAccent
                                )
                            }
                            Text(
                                text = "${metrics.flowScorePercent}% C1 Flow",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (metrics.flowScorePercent >= 80) palette.tertiaryAccent else palette.goldAccent
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.imprintStageBg,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${metrics.wordsPerMinute}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = palette.imprintStageText)
                                    Text("WPM (Tempo)", style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.imprintStageBg,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    val fillerCol = if (metrics.fillerWordCount == 0) palette.tertiaryAccent else palette.warningRed
                                    Text("${metrics.fillerWordCount}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = fillerCol)
                                    Text("Füllwörter", style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.imprintStageBg,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("%.1fs".format(metrics.durationSeconds), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = palette.imprintStageText)
                                    Text("Dauer", style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
                                }
                            }
                        }

                        Text(
                            text = metrics.summaryFeedbackDe,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
            }

            // Controls: Big Microphone Button + Submit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button with animated pulse when recording
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    if (speechController.isListening) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(palette.primaryAccent.copy(alpha = 0.28f))
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (speechController.isListening) {
                                    speechController.stopListening()
                                } else {
                                    if (hasRecordPermission) {
                                        speechController.startListening()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            }
                            .testTag("mic_record_toggle_btn"),
                        shape = CircleShape,
                        color = if (speechController.isListening) palette.warningRed else palette.primaryAccent
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = if (speechController.isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                                contentDescription = if (speechController.isListening) "Stopp" else "Aufnahme starten",
                                tint = contentColorOnAccent(if (speechController.isListening) palette.warningRed else palette.primaryAccent),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Accept & Analyze Button
                val hasText = speechController.finalTranscription.isNotBlank() || speechController.liveTranscription.isNotBlank()
                Button(
                    onClick = {
                        val text = speechController.finalTranscription.ifBlank { speechController.liveTranscription }
                        if (text.isNotBlank()) {
                            onTranscriptionAccepted(text)
                        }
                    },
                    enabled = hasText && !speechController.isListening,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("apply_transcription_btn")
                ) {
                    val fg = contentColorOnAccent(palette.primaryAccent)
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (speechController.isListening) "Sprechen..." else "Antwort analysieren",
                        color = fg,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
