package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WarningAmber
import com.example.audio.rememberGermanSpeechRecognizerController
import com.example.ui.components.GermanVoiceInputDeck
import com.example.ui.components.SentenceShadowingDeck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.engine.DailySessionBeat
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.PersonalizedExercise
import com.example.ui.SpontaneousEvalUiState
import com.example.ui.components.ImprintTierMeterPills
import com.example.ui.components.InteractiveRegisterSpectrumDial
import com.example.ui.components.InteractiveSpeechMorphStrip
import com.example.ui.components.VisualChunkBlueprintBoard
import com.example.ui.components.VisualComicDialogueStage
import com.example.ui.components.VisualConceptInfographic
import com.example.ui.components.VisualMemoryBatteryBar
import com.example.ui.components.VisualSituationStoryboardBanner
import com.example.ui.components.pillarAccentColor
import com.example.ui.components.visualIllustrationResFor
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent

@Composable
internal fun EmbeddedAdaptiveProbeBeat(
    beat: DailySessionBeat,
    onSubmitProbe: (C1QuizItem, Int, Long, Boolean) -> Unit,
    onSectorDone: () -> Unit
) {
    val palette = LocalVibePalette.current
    val probes = beat.quizProbes
    var probeIdx by remember(beat.id) { mutableIntStateOf(0) }
    var selectedOpt by remember(beat.id, probeIdx) { mutableStateOf<Int?>(null) }
    var submitted by remember(beat.id, probeIdx) { mutableStateOf(false) }
    val startTimeMs = remember(beat.id, probeIdx) { System.currentTimeMillis() }

    val item = probes.getOrNull(probeIdx) ?: return
    val accent = pillarAccentColor(item.pillar)
    val badges = listOf("A", "B", "C", "D")

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.cardSurface,
            border = BorderStroke(1.2.dp, palette.borderStroke)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = item.pillar.shortLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = accent,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ImprintTierMeterPills(tier = item.difficulty, accent = palette.primaryAccent)
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = palette.elevatedSurface
                        ) {
                            Text(
                                text = "${probeIdx + 1}/${probes.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = palette.textPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = item.promptDe,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = palette.textSecondary
                )

                // Dark Imprint Focal Stage for Context Snippet
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.imprintStageBg,
                    border = BorderStroke(1.dp, palette.primaryAccent.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = item.contextSnippet,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = palette.imprintStageText,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }

                item.options.forEachIndexed { idx, opt ->
                    val isSelected = selectedOpt == idx
                    val isCorrect = idx == item.correctIndex
                    val borderCol = when {
                        submitted && isCorrect -> palette.tertiaryAccent
                        submitted && isSelected && !isCorrect -> palette.warningRed
                        isSelected -> palette.primaryAccent
                        else -> palette.borderStroke
                    }
                    val bgCol = when {
                        submitted && isCorrect -> palette.tertiaryAccent.copy(alpha = 0.15f)
                        submitted && isSelected && !isCorrect -> palette.warningRed.copy(alpha = 0.15f)
                        isSelected -> palette.primaryAccent.copy(alpha = 0.14f)
                        else -> palette.elevatedSurface.copy(alpha = 0.72f)
                    }
                    val optScale by animateFloatAsState(
                        targetValue = if (submitted && isCorrect) 1.02f else if (submitted && !isSelected) 0.98f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "probeOpt_$idx"
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(optScale)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !submitted) {
                                selectedOpt = idx
                                submitted = true
                                val elapsed = System.currentTimeMillis() - startTimeMs
                                onSubmitProbe(item, idx, elapsed, probeIdx == probes.lastIndex)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = bgCol,
                        border = BorderStroke(1.3.dp, borderCol)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (submitted && isCorrect) palette.tertiaryAccent
                                else if (submitted && isSelected) palette.warningRed
                                else palette.cardSurface,
                                border = BorderStroke(1.dp, borderCol),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = badges.getOrElse(idx) { "${idx + 1}" },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (submitted && (isCorrect || isSelected)) Color.White else palette.textPrimary
                                    )
                                }
                            }
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = palette.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            if (submitted && isCorrect) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = palette.tertiaryAccent, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = submitted,
                    enter = expandVertically(tween(340)) + fadeIn(tween(340)) + scaleIn(initialScale = 0.96f)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        VisualConceptInfographic(
                            diagramType = item.visualDiagram,
                            formulaBefore = item.trapNote,
                            formulaAfter = item.options[item.correctIndex],
                            highlightRule = item.explanationDe
                        )

                        Button(
                            onClick = {
                                if (probeIdx < probes.lastIndex) {
                                    probeIdx++
                                } else {
                                    onSectorDone()
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_sector_btn")
                        ) {
                            val fg = contentColorOnAccent(palette.primaryAccent)
                            Text(
                                text = if (probeIdx < probes.lastIndex) "Nächster Impuls →" else "Weiter zum nächsten Sektor →",
                                color = fg,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun EmbeddedBlindListeningBeat(
    ex: PersonalizedExercise,
    isPlayingAudio: Boolean,
    isLoadingAudio: Boolean,
    fastNaturalRate: Boolean,
    engineBadge: String,
    audioErrorMessage: String?,
    onPlayDialogue: () -> Unit,
    onToggleSpeed: () -> Unit,
    onSpeakLine: (String) -> Unit,
    onSectorDone: () -> Unit
) {
    val palette = LocalVibePalette.current
    val compAnswers = remember(ex.id) { mutableStateMapOf<Int, Int>() }
    var manualUnlock by remember(ex.id) { mutableStateOf(false) }
    val allAnswered = compAnswers.size >= ex.comprehensionTasks.size
    val unlocked = allAnswered || manualUnlock

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        VisualSituationStoryboardBanner(
            illustrationRes = visualIllustrationResFor(sceneType = ex.visualSceneType, familyId = ex.scenarioFamilyId),
            sceneBadge = "${ex.difficulty} · BLIND-HÖREN",
            registerChip = ex.subRegister.substringBefore("(").trim(),
            goalSummary = ex.title,
            fullSceneNote = ex.learningObjective,
            isPlayingAudio = isPlayingAudio,
            isLoadingAudio = isLoadingAudio,
            fastRateLabel = if (fastNaturalRate) "1.15× Echt" else "0.96× Klar",
            ttsDisclosureShort = "$engineBadge · Mehrsprecher ohne Text hören",
            onTogglePlay = onPlayDialogue,
            onToggleSpeed = onToggleSpeed
        )

        if (!audioErrorMessage.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.warningRed.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.45f))
            ) {
                Text(
                    text = audioErrorMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.warningRed,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        ex.comprehensionTasks.forEachIndexed { qIdx, task ->
            val chosen = compAnswers[qIdx]
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.dp, palette.borderStroke)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.question,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.textPrimary
                    )
                    task.options.forEachIndexed { oIdx, opt ->
                        val isChosen = chosen == oIdx
                        val isRight = oIdx == task.correctIndex
                        val col = when {
                            chosen != null && isRight -> palette.tertiaryAccent
                            isChosen && !isRight -> palette.warningRed
                            else -> palette.borderStroke
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { compAnswers[qIdx] = oIdx },
                            shape = RoundedCornerShape(12.dp),
                            color = if (chosen != null && isRight) palette.tertiaryAccent.copy(alpha = 0.14f)
                            else if (isChosen) palette.warningRed.copy(alpha = 0.14f)
                            else palette.elevatedSurface.copy(alpha = 0.7f),
                            border = BorderStroke(1.2.dp, col)
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = palette.textPrimary,
                                modifier = Modifier.padding(11.dp)
                            )
                        }
                    }
                }
            }
        }

        if (!unlocked) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { manualUnlock = true },
                shape = RoundedCornerShape(16.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.2.dp, palette.goldAccent.copy(alpha = 0.55f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = palette.goldAccent, modifier = Modifier.size(18.dp))
                        Text("Comic-Dialog & Reduktionen gesperrt", style = MaterialTheme.typography.labelMedium, color = palette.goldAccent)
                    }
                    Surface(shape = RoundedCornerShape(99.dp), color = palette.secondaryAccent.copy(alpha = 0.16f)) {
                        Text(
                            "Freischalten",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.secondaryAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            InteractiveSpeechMorphStrip(reductions = ex.connectedSpeechReductions)
            VisualComicDialogueStage(
                turns = ex.transcriptTurns,
                onSpeakLine = onSpeakLine
            )
            SentenceShadowingDeck(
                turns = ex.transcriptTurns,
                onSpeakLine = onSpeakLine
            )
            Button(
                onClick = onSectorDone,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("next_sector_btn")
            ) {
                Text(
                    "Weiter zu Spontan-Reaktion →",
                    color = contentColorOnAccent(palette.primaryAccent),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
internal fun EmbeddedSpontaneousSpeakingBeat(
    ex: PersonalizedExercise,
    evalState: SpontaneousEvalUiState,
    onEvaluate: (String) -> Unit,
    onSpeakPhrase: (String) -> Unit,
    onSectorDone: () -> Unit
) {
    val palette = LocalVibePalette.current
    var draft by remember(ex.id) { mutableStateOf("") }
    var submitted by remember(ex.id) { mutableStateOf(false) }
    var inputModeVoice by remember(ex.id) { mutableStateOf(true) }
    val speechController = rememberGermanSpeechRecognizerController()

    androidx.compose.runtime.LaunchedEffect(speechController.liveTranscription, speechController.finalTranscription) {
        val st = speechController.finalTranscription.ifBlank { speechController.liveTranscription }
        if (st.isNotBlank()) {
            draft = st
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Imprint Dark Visual Stage for Spontaneous Mission
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.imprintStageBg,
            border = BorderStroke(1.3.dp, palette.primaryAccent.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = palette.primaryAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(
                            text = "SPONTAN-IMPULS · MUSTER VERBORGEN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.primaryAccent
                        )
                    }
                    Icon(Icons.Rounded.VisibilityOff, contentDescription = null, tint = palette.imprintStageSubtext, modifier = Modifier.size(16.dp))
                }

                Text(
                    text = ex.speakingTask,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.imprintStageText
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.warningRed.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = palette.warningRed, modifier = Modifier.size(18.dp))
                        Text(
                            text = ex.interactionTask,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = palette.imprintStageText
                        )
                    }
                }
            }
        }

        // Input Mode Selector: Microphone vs Keyboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EINGABEMODUS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = palette.textSecondary
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(palette.elevatedSurface.copy(alpha = 0.85f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .clickable { inputModeVoice = true }
                        .testTag("spontaneous_mode_voice"),
                    color = if (inputModeVoice) palette.primaryAccent else Color.Transparent,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Mic,
                            contentDescription = null,
                            tint = if (inputModeVoice) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Mikrofon",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (inputModeVoice) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .clickable { inputModeVoice = false }
                        .testTag("spontaneous_mode_keyboard"),
                    color = if (!inputModeVoice) palette.primaryAccent else Color.Transparent,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Keyboard,
                            contentDescription = null,
                            tint = if (!inputModeVoice) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Tastatur",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (!inputModeVoice) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary
                        )
                    }
                }
            }
        }

        // Voice Input Deck with Live Waveform & Telemetry
        if (inputModeVoice) {
            GermanVoiceInputDeck(
                speechController = speechController,
                onTranscriptionAccepted = { transcribedText ->
                    draft = transcribedText
                    submitted = true
                    onEvaluate(transcribedText)
                }
            )
        }

        // Text input field (synchronized with speech or manual keyboard input)
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = palette.cardSurface,
            border = BorderStroke(1.dp, palette.borderStroke)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Spontan auf Deutsch antworten (ohne Übersetzen)...") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_spontaneous_input")
                )

                Button(
                    onClick = {
                        if (draft.isNotBlank()) {
                            submitted = true
                            onEvaluate(draft)
                        }
                    },
                    enabled = draft.isNotBlank() && evalState !is SpontaneousEvalUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("daily_spontaneous_submit")
                ) {
                    val fg = contentColorOnAccent(palette.primaryAccent)
                    if (evalState is SpontaneousEvalUiState.Loading) {
                        CircularProgressIndicator(color = fg, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("KI prüft Natürlichkeit...", color = fg)
                    } else {
                        Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Reaktion prüfen & Register-Regler öffnen", color = fg, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        when (evalState) {
            is SpontaneousEvalUiState.Error -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.warningRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, palette.warningRed)
                ) {
                    Text(
                        text = evalState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.warningRed,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }
            }
            is SpontaneousEvalUiState.Success -> {
                val ev = evalState.evaluation
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = palette.tertiaryAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.2.dp, palette.tertiaryAccent)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "✓ NATÜRLICHKEIT ${ev.naturalnessPercent}% · ${ev.primaryErrorCategory.labelDe.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.tertiaryAccent
                        )
                        Text(ev.meaningFeedbackDe, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
                        Text(
                            text = "Tipp: ${ev.targetedRepetitionPromptDe}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.primaryAccent
                        )
                    }
                }
            }
            else -> Unit
        }

        AnimatedVisibility(
            visible = submitted,
            enter = expandVertically(tween(360)) + fadeIn(tween(340))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InteractiveRegisterSpectrumDial(
                    baseline = ex.understandableBaseline,
                    alternatives = ex.acceptableAlternatives,
                    onSpeakPhrase = onSpeakPhrase
                )
                VisualChunkBlueprintBoard(
                    expressions = ex.usefulExpressions,
                    collocations = ex.vocabulary,
                    grammarPatterns = ex.grammarPatterns,
                    onSpeakChunk = onSpeakPhrase
                )
                Button(
                    onClick = onSectorDone,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_sector_btn")
                ) {
                    Text(
                        "Weiter zum nächsten Sektor →",
                        color = contentColorOnAccent(palette.primaryAccent),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
internal fun EmbeddedSrsVocabBurstBeat(
    beat: DailySessionBeat,
    onSpeakExpression: (String) -> Unit,
    onRateCard: (C1VocabCard, Int) -> Unit,
    onSectorDone: () -> Unit
) {
    val palette = LocalVibePalette.current
    val cards = beat.srsCards
    var idx by remember(beat.id) { mutableIntStateOf(0) }
    var flipped by remember(beat.id, idx) { mutableStateOf(false) }

    val item = cards.getOrNull(idx) ?: return
    val card = item.card
    val accent = pillarAccentColor(card.pillar)

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "srsCard3DFlip"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KARTE ${idx + 1}/${cards.size} · ${card.register.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = accent
            )
            VisualMemoryBatteryBar(
                stabilityDays = item.state?.stabilityDays ?: 0.5f,
                accent = accent
            )
        }

        // Real 3D Y-Axis Flip Flashcard
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("srs_flashcard")
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density
                }
                .clip(RoundedCornerShape(24.dp))
                .clickable { flipped = !flipped },
            shape = RoundedCornerShape(24.dp),
            color = palette.cardSurface,
            border = BorderStroke(1.5.dp, palette.primaryAccent.copy(alpha = 0.55f))
        ) {
            if (rotation <= 90f) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = palette.primaryAccent.copy(alpha = 0.14f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .clickable { onSpeakExpression(card.expression) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "Anhören", tint = palette.primaryAccent, modifier = Modifier.size(18.dp))
                            Text(
                                text = card.expression,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = palette.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    VisualConceptInfographic(
                        diagramType = card.diagramType,
                        formulaBefore = card.grammarTag,
                        formulaAfter = card.expression,
                        highlightRule = card.visualHint
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Rounded.TouchApp, contentDescription = null, tint = palette.textSecondary, modifier = Modifier.size(16.dp))
                        Text("Tippen zum 3D-Wenden", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { rotationY = 180f }
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(card.expression, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = palette.primaryAccent)
                    Text(card.meaningEn, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.imprintStageBg,
                        modifier = Modifier.clickable { onSpeakExpression(card.exampleDe) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(18.dp))
                            Text("„${card.exampleDe}“", style = MaterialTheme.typography.bodyMedium, color = palette.imprintStageText)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.warningRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.45f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = palette.warningRed, modifier = Modifier.size(16.dp))
                            Text(card.trapWarning, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
                        }
                    }
                }
            }
        }

        if (!flipped) {
            Button(
                onClick = { flipped = true },
                colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Aufdecken", color = contentColorOnAccent(palette.primaryAccent), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("Nochmal", palette.warningRed, 1),
                    Triple("Schwer", palette.goldAccent, 2),
                    Triple("Gut", palette.secondaryAccent, 3),
                    Triple("Sicher", palette.tertiaryAccent, 4)
                ).forEach { (label, col, q) ->
                    Button(
                        onClick = {
                            onRateCard(card, q)
                            if (idx < cards.lastIndex) {
                                idx++
                            } else {
                                onSectorDone()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = col),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = contentColorOnAccent(col))
                    }
                }
            }
        }
    }
}
