package com.example.ui


import androidx.activity.compose.BackHandler

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.data.model.SessionPhase
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveSessionScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {

    BackHandler {
        viewModel.navigateBack()
    }

    val session by viewModel.activeSession.collectAsState()
    val phase by viewModel.currentPhase.collectAsState()
    val isPlayingAudio by viewModel.speechHelper.isPlaying.collectAsState()
    val warmUpOption by viewModel.warmUpSelectedOption.collectAsState()
    val warmUpRevealed by viewModel.warmUpAnswerRevealed.collectAsState()
    val prodInput by viewModel.productionUserInput.collectAsState()

    val transferInput by viewModel.transferUserInput.collectAsState()
    val isCompleted by viewModel.sessionCompleted.collectAsState()

    if (session == null) {
        Box(modifier = Modifier.fillMaxSize().background(BerlinTheme.bg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BerlinTheme.accent)
        }
        return
    }

    val currentSession = session!!

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BerlinTheme.bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentSession.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink,
                            maxLines = 1
                        )
                        Text(
                            text = "Phase ${phase.index}/5 • ${phase.titleDe} (${phase.durationMin} Min.)",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("session_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = BerlinTheme.ink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BerlinTheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = BerlinTheme.surface,
                border = BorderStroke(1.dp, BerlinTheme.line),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "18 Min. Adaptiv-Session",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BerlinTheme.ink2
                    )

                    Button(
                        onClick = {
                            if (!isCompleted) {
                                viewModel.advanceSessionPhase()
                            } else {
                                viewModel.navigateTo(Screen.Home)
                            }
                        },
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("session_next_phase_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BerlinTheme.accent,
                            contentColor = BerlinTheme.onAccent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (phase == SessionPhase.TRANSFER_TEST) "Session abschließen" else "Weiter ›",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .testTag("session_content_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Phase Progress Indicator Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SessionPhase.values().forEach { p ->
                        val isDone = p.index < phase.index
                        val isCurrent = p.index == phase.index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isDone -> BerlinTheme.ok
                                        isCurrent -> BerlinTheme.accent
                                        else -> BerlinTheme.line
                                    }
                                )
                        )
                    }
                }
            }

            item {
                Text(
                    text = viewModel.legacyProgressNote,
                    fontSize = 12.sp,
                    color = BerlinTheme.ink2,
                    modifier = Modifier.testTag("legacy_progress_note")
                )
            }

            // Completed State
            if (isCompleted) {
                item {
                    SessionCompletedCard(
                        category = currentSession.targetCategory,
                        onDone = { viewModel.navigateTo(Screen.Home) }
                    )
                }
            } else {
                when (phase) {
                    SessionPhase.WARM_UP -> {
                        item {
                            Phase1WarmUpView(
                                task = currentSession.warmUpPrompt,
                                isPlayingAudio = isPlayingAudio,
                                selectedOption = warmUpOption,
                                isAnswerRevealed = warmUpRevealed,
                                onPlayAudio = {
                                    viewModel.speechHelper.speak(
                                        currentSession.warmUpPrompt.audioSpeakerText,
                                        1.15f
                                    )
                                },
                                onSelectOption = { viewModel.selectWarmUpOption(it) },
                                onRevealAnswer = { viewModel.revealWarmUpAnswer() }
                            )
                        }
                    }
                    SessionPhase.MAIN_CHALLENGE -> {
                        item {
                            Phase2MainChallengeView(
                                task = currentSession.mainChallenge,
                                onPlayPartnerAudio = {
                                    viewModel.speechHelper.speak(
                                        currentSession.mainChallenge.partnerStatement,
                                        1.15f
                                    )
                                }
                            )
                        }
                    }
                    SessionPhase.ACTIVE_PRODUCTION -> {
                        item {
                            Phase3ProductionView(
                                task = currentSession.productionTask,
                                input = prodInput,

                                onInputChange = { viewModel.updateProductionInput(it) },
                                onPlayPrompt = {
                                    viewModel.speechHelper.speak(
                                        currentSession.productionTask.simulatedAudioPrompt,
                                        1.2f
                                    )
                                }
                            )
                        }
                    }
                    SessionPhase.PRECISION_CORRECTION -> {
                        item {
                            Phase4CorrectionView(
                                task = currentSession.correctionTask,
                                onAdvanceError = { viewModel.advanceErrorStage(it) }
                            )
                        }
                    }
                    SessionPhase.TRANSFER_TEST -> {
                        item {
                            Phase5TransferTestView(
                                task = currentSession.transferTask,
                                input = transferInput,
                                onInputChange = { viewModel.updateTransferInput(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Phase 1: Diagnostic Warm-Up View (24dp radius, .opt answer cards)
// ----------------------------------------------------
@Composable
fun Phase1WarmUpView(
    task: com.example.data.model.WarmUpTask,
    isPlayingAudio: Boolean,
    selectedOption: Int?,
    isAnswerRevealed: Boolean,
    onPlayAudio: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onRevealAnswer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
        border = BorderStroke(1.dp, BerlinTheme.line)
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
                Text(
                    text = "DIAGNOSTISCHER WARM-UP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = BerlinTheme.accentSoft
                ) {
                    Text(
                        text = "2 Minuten",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.accentInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Hören Sie die native Äußerung einmal an. Die Auflösung erfolgt erst nach Ihrer eigenständigen Einordnung.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )

            // Audio Player Bar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BerlinTheme.surface2,
                border = BorderStroke(1.dp, BerlinTheme.line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier
                            .size(44.dp)
                            .background(BerlinTheme.accent, CircleShape)
                            .testTag("warmup_play_audio")
                    ) {
                        Icon(
                            imageVector = if (isPlayingAudio) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                            contentDescription = "Audio abspielen",
                            tint = BerlinTheme.onAccent
                        )
                    }

                    Column {
                        Text(
                            text = "Authentischer Sprecher (1.15x)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = task.accentInfo,
                            fontSize = 11.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                }
            }

            Text(
                text = task.questionPrompt,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BerlinTheme.ink
            )

            // Multiple Choice Options (.opt: 2dp border, 16dp radius, min-height 56dp)
            task.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = index == task.correctIndex

                val (borderColor, bgColor) = when {
                    isAnswerRevealed && isCorrect -> Pair(BerlinTheme.ok, BerlinTheme.okBg)
                    isAnswerRevealed && isSelected && !isCorrect -> Pair(BerlinTheme.coral, BerlinTheme.coralSoft)
                    !isAnswerRevealed && isSelected -> Pair(BerlinTheme.accent, BerlinTheme.accentSoft)
                    else -> Pair(BerlinTheme.line, BerlinTheme.surface)
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = bgColor,
                    border = BorderStroke(2.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                        .clickable { if (!isAnswerRevealed) onSelectOption(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { if (!isAnswerRevealed) onSelectOption(index) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (isAnswerRevealed && isCorrect) BerlinTheme.ok else BerlinTheme.accent,
                                unselectedColor = BerlinTheme.ink2
                            )
                        )
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            color = BerlinTheme.ink,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            if (!isAnswerRevealed && selectedOption != null) {
                Button(
                    onClick = onRevealAnswer,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BerlinTheme.accent,
                        contentColor = BerlinTheme.onAccent
                    )
                ) {
                    Text("Diagnose auswerten", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (isAnswerRevealed) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BerlinTheme.okBg,
                    border = BorderStroke(1.5.dp, BerlinTheme.ok),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DIAGNOSTISCHES FEEDBACK:",
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ok,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.diagnosticExplanation,
                            fontSize = 13.sp,
                            color = BerlinTheme.ink,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Phase 2: Main Challenge View with Progression (24dp radius)
// ----------------------------------------------------
@Composable
fun Phase2MainChallengeView(
    task: com.example.data.model.MainChallengeTask,
    onPlayPartnerAudio: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
        border = BorderStroke(1.dp, BerlinTheme.line)
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
                Text(
                    text = "HAUPT-HERAUSFORDERUNG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = BerlinTheme.accentSoft
                ) {
                    Text(
                        text = "7 Minuten",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.accentInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = task.scenarioContext,
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )

            // Partner Statement Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BerlinTheme.surface2,
                border = BorderStroke(1.dp, BerlinTheme.line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Konfrontation (Herr Brandt):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BerlinTheme.coral
                        )
                        IconButton(onClick = onPlayPartnerAudio, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Audio abspielen",
                                tint = BerlinTheme.accentInk
                            )
                        }
                    }
                    Text(
                        text = "„${task.partnerStatement}“",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BerlinTheme.ink
                    )
                }
            }

            // Progression Steps
            Text(
                text = "PROGRESSIONSSTUFEN DER INTERVENTION",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = BarlowCondensedFontFamily,
                color = BerlinTheme.ink
            )

            ProgressionStepCard(stepNumber = "1", title = "Souveränes Auffangen", description = task.stage1Prompt)
            ProgressionStepCard(stepNumber = "2", title = "Konter unter Druck", description = task.stage2Deepening)
            ProgressionStepCard(stepNumber = "3", title = "Inferenz & Absicherung", description = task.stage3StressInference)
        }
    }
}

@Composable
fun ProgressionStepCard(stepNumber: String, title: String, description: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BerlinTheme.surface2,
        border = BorderStroke(1.dp, BerlinTheme.line),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = BerlinTheme.accent,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = stepNumber, fontSize = 11.sp, fontWeight = FontWeight.Black, color = BerlinTheme.onAccent)
                }
            }
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BerlinTheme.ink)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, fontSize = 12.sp, color = BerlinTheme.ink2)
            }
        }
    }
}

// ----------------------------------------------------
// Phase 3: Active Production View (Speaking / Writing)
// ----------------------------------------------------
@Composable
fun Phase3ProductionView(
    task: com.example.data.model.ProductionTask,
    input: String,

    onInputChange: (String) -> Unit,
    onPlayPrompt: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
        border = BorderStroke(1.dp, BerlinTheme.line)
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
                Text(
                    text = "AKTIVE PRODUKTION (TEXTÜBUNG)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = BerlinTheme.accentSoft
                ) {
                    Text(
                        text = "5 Minuten",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.accentInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = task.situationBrief,
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )

            // Audio challenge prompt
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BerlinTheme.surface2,
                border = BorderStroke(1.dp, BerlinTheme.line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = onPlayPrompt, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = BerlinTheme.accentInk)
                    }
                    Text(
                        text = "„${task.simulatedAudioPrompt}“",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BerlinTheme.ink
                    )
                }
            }

            // Evaluation Criteria
            Text(
                text = "SELBSTVERGLEICH (KEINE AUTOMATISCHE BEWERTUNG):",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = BarlowCondensedFontFamily,
                color = BerlinTheme.ink
            )
            task.keyEvaluationCriteria.forEach { crit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BerlinTheme.ok, modifier = Modifier.size(16.dp))
                    Text(text = crit, fontSize = 12.sp, color = BerlinTheme.ink2)
                }
            }

            Text(
                text = "Sprachaufnahme und Transkription sind in dieser Übung nicht verfügbar. Antworten Sie schriftlich; es erfolgt keine automatische Bewertung.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2,
                modifier = Modifier.testTag("recording_unavailable_note")
            )

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp).testTag("production_input_field"),
                placeholder = { Text("Hier eigene Antwort eingeben...") },
                shape = RoundedCornerShape(16.dp)
            )
            AiFeedbackShareButton(
                task = task.situationBrief,
                response = input,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ----------------------------------------------------
// Phase 4: Precision Correction View (24dp radius)
// ----------------------------------------------------
@Composable
fun Phase4CorrectionView(
    task: com.example.data.model.CorrectionTask,
    onAdvanceError: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
        border = BorderStroke(1.dp, BerlinTheme.line)
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
                Text(
                    text = "PRÄZISIONS-KORREKTUR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = BerlinTheme.accentSoft
                ) {
                    Text(
                        text = "2 Minuten",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.accentInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Beispielfehler zum Selbstvergleich, kein Nachweis persönlicher Fehler oder Beherrschung. Verstehen Sie, warum die Formulierungen unnatürlich klingen:",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )

            task.errorItems.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BerlinTheme.surface2,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.category.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.ink2
                            )
                            Text(
                                text = item.masteryStage.label,
                                fontSize = 11.sp,
                                color = BerlinTheme.ok
                            )
                        }

                        // Typical error (.alert style: Coral)
                        Text(
                            text = "❌ Lernender: ${item.userMistake}",
                            fontSize = 13.sp,
                            color = BerlinTheme.coral,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Native reformulation (Ok green)
                        Text(
                            text = "✅ Muttersprachlich: ${item.nativeReformulation}",
                            fontSize = 13.sp,
                            color = BerlinTheme.ok,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Warum klingt das fremd? ${item.whyItSoundsForeign}",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2,
                            lineHeight = 16.sp
                        )

                        OutlinedButton(
                            onClick = { onAdvanceError(item.id) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, BerlinTheme.line)
                        ) {
                            Text("Struktur gelesen (unbewertet)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BerlinTheme.ink)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Phase 5: Transfer Test View (Zero Hints!)
// ----------------------------------------------------
@Composable
fun Phase5TransferTestView(
    task: com.example.data.model.TransferTask,
    input: String,
    onInputChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
        border = BorderStroke(1.dp, BerlinTheme.line)
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
                Text(
                    text = "TRANSFER-TEST (OHNE VORLAGE)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlowCondensedFontFamily,
                    color = BerlinTheme.ink
                )
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = BerlinTheme.accentSoft
                ) {
                    Text(
                        text = "2 Minuten",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.accentInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Wenden Sie die korrigierten Wendungen jetzt eigenständig in einem völlig neuen Sachgebiet an. Keine Musterlösung sichtbar!",
                fontSize = 13.sp,
                color = BerlinTheme.ink2
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BerlinTheme.surface2,
                border = BorderStroke(1.dp, BerlinTheme.line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Neuer Transfer-Kontext:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BerlinTheme.accentInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.scenarioPrompt,
                        fontSize = 13.sp,
                        color = BerlinTheme.ink
                    )
                }
            }

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp).testTag("transfer_input_field"),
                placeholder = { Text("Hier freie Formulierung eingeben...") },
                shape = RoundedCornerShape(16.dp)
            )
            AiFeedbackShareButton(
                task = task.scenarioPrompt,
                response = input,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SessionCompletedCard(
    category: com.example.data.model.SkillCategory,
    onDone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("session_completed_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BerlinTheme.surface),
        border = BorderStroke(2.dp, BerlinTheme.ok)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = BerlinTheme.okBg,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BerlinTheme.ok,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = "ÜBUNGSSITZUNG ABGESCHLOSSEN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = BarlowCondensedFontFamily,
                color = BerlinTheme.ink
            )

            Text(
                text = "${category.titleDe}: Unbewertete Übung abgeschlossen. Kompetenzwerte und Fehler-Beherrschungsstufen bleiben unverändert; der Abschluss ist kein Leistungsnachweis.",
                fontSize = 13.sp,
                color = BerlinTheme.ink2,
                lineHeight = 18.sp
            )

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.accent, contentColor = BerlinTheme.onAccent)
            ) {
                Text("Zurück zur Übersicht", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
