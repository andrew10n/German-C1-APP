package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.rememberHybridC1AudioController
import com.example.domain.engine.MasterControlState
import com.example.domain.model.PersonalizedExercise
import com.example.ui.SpontaneousEvalUiState
import com.example.ui.components.AnimatedVisualSceneHero
import com.example.ui.components.InteractiveRegisterSpectrumDial
import com.example.ui.components.InteractiveSpeechMorphStrip
import com.example.ui.components.VisualChunkBlueprintBoard
import com.example.ui.components.VisualComicDialogueStage
import com.example.ui.components.VisualSituationStoryboardBanner
import com.example.ui.components.visualIllustrationResFor
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.DeepInk
import com.example.ui.theme.EmeraldSage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizedVisualLabScreen(
    controlState: MasterControlState,
    initialExerciseId: String?,
    spontaneousEvalState: SpontaneousEvalUiState,
    onBeatCompleted: (String, String) -> Unit,
    onEvaluateSpontaneous: (PersonalizedExercise, String) -> Unit,
    onResetSpontaneousEval: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val library = controlState.personalizedLibrary
    val ordered = controlState.prioritizedPersonalizedExercises

    var selectedFamilyId by remember { mutableStateOf("ALL") }
    val filteredList = remember(ordered, selectedFamilyId) {
        if (selectedFamilyId == "ALL") ordered
        else ordered.filter { it.exercise.scenarioFamilyId == selectedFamilyId }
    }.ifEmpty { ordered }

    val startIdx = remember(initialExerciseId, filteredList) {
        filteredList.indexOfFirst { it.exercise.id == initialExerciseId }.coerceAtLeast(0)
    }
    var selectedIdx by remember(selectedFamilyId) {
        mutableIntStateOf(startIdx.coerceAtMost(filteredList.lastIndex.coerceAtLeast(0)))
    }
    val currentPrioritized = filteredList.getOrElse(selectedIdx) { ordered.first() }
    val ex = currentPrioritized.exercise

    // 4 Visual Learning Beats
    var activeBeat by remember(ex.id) { mutableIntStateOf(0) }
    val compAnswers = remember(ex.id) { mutableStateMapOf<Int, Int>() }
    var manualTranscriptReveal by remember(ex.id) { mutableStateOf(false) }
    var userSpontaneousDraft by remember(ex.id) { mutableStateOf("") }
    var hasSubmittedSpeaking by remember(ex.id) { mutableStateOf(false) }
    var userTransferDraft by remember(ex.id) { mutableStateOf("") }
    var transferCompleted by remember(ex.id) { mutableStateOf(false) }
    var showAuditOverlay by remember { mutableStateOf(false) }

    val audioController = rememberHybridC1AudioController()
    var fastNaturalRate by remember { mutableStateOf(true) }

    fun speakGerman(text: String) {
        audioController.speakPhrase(text, if (fastNaturalRate) 1.16f else 0.96f)
    }

    val transcriptUnlocked = manualTranscriptReveal ||
        compAnswers.size >= ex.comprehensionTasks.size.coerceIn(1, 2)
    val sceneIllustration = visualIllustrationResFor(
        sceneType = ex.visualSceneType,
        familyId = ex.scenarioFamilyId
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("personalized_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VISUAL LAB · ${ex.difficulty} · ${ex.subRegister.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldSage,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = ex.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showAuditOverlay = !showAuditOverlay }
                        .testTag("toggle_gap_audit_button"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (showAuditOverlay) CobaltBright else CobaltBright.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Assessment,
                            contentDescription = "Audit",
                            tint = if (showAuditOverlay) Color.White else CobaltBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Stats",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (showAuditOverlay) Color.White else CobaltBright
                        )
                    }
                }
            }

            // Visual Metric Grid for Audit & Validation (Replaces text wall!)
            AnimatedVisibility(visible = showAuditOverlay) {
                val rep = library.validationReport
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DeepInk,
                    border = BorderStroke(1.5.dp, CobaltBright)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AuditMetricTile("${rep.totalExercises}", "Szenarien", EmeraldSage, Modifier.weight(1f))
                            AuditMetricTile("${rep.scenarioFamiliesCount}", "Welten", CobaltBright, Modifier.weight(1f))
                            AuditMetricTile("${rep.duplicatesDetected}", "Duplikate", AmberBright, Modifier.weight(1f))
                            AuditMetricTile("${rep.missingAudioAssets.size}", "TTS-Vorschau", CoralCrimson, Modifier.weight(1f))
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Alltag/Schwäbisch: ${rep.contextDistribution["EVERYDAY_UMGANGSSPRACHE"] ?: 8}",
                                "MB Powertrain: ${rep.contextDistribution["AUTOMOTIVE_ENGINEERING"] ?: 6}",
                                "SV 1845 & DFB: ${rep.contextDistribution["FOOTBALL_COACHING"] ?: 6}",
                                "B2→C1+ Stufen",
                                "11-Fehler-Radar aktiv"
                            ).forEach { chip ->
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = Color.White.copy(alpha = 0.09f)
                                ) {
                                    Text(
                                        text = chip,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scenario Family Visual Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FamilyPill("Alle (${ordered.size})", selectedFamilyId == "ALL") { selectedFamilyId = "ALL" }
                library.scenarioFamilies.forEach { fam ->
                    FamilyPill(
                        label = fam.title.substringBefore(":"),
                        selected = selectedFamilyId == fam.id,
                        onClick = { selectedFamilyId = fam.id }
                    )
                }
            }

            // Exercise Carousel Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filteredList.forEachIndexed { idx, item ->
                    val sel = idx == selectedIdx
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onResetSpontaneousEval()
                                audioController.stop()
                                selectedIdx = idx
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (sel) DeepInk else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (sel) AmberBright else MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = item.exercise.difficulty,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (sel) AmberBright else EmeraldSage
                            )
                            Text(
                                text = item.exercise.title.substringBefore(":"),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Animated 60fps Canvas Motion Diagram
            AnimatedVisualSceneHero(
                sceneType = ex.visualSceneType,
                isPlayingAudio = audioController.isPlaying,
                speakers = ex.speakers,
                difficulty = ex.difficulty,
                subRegister = ex.subRegister
            )

            // 4-Beat Icon-First Visual Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val beats = listOf(
                    Triple("Hören", Icons.Rounded.GraphicEq, currentPrioritized.blindListeningDone),
                    Triple("Spontan", Icons.Rounded.RecordVoiceOver, currentPrioritized.spontaneousSpeakingDone),
                    Triple("Baukasten", Icons.Rounded.Tune, false),
                    Triple("Transfer", Icons.Rounded.RocketLaunch, currentPrioritized.transferDone)
                )
                beats.forEachIndexed { bIdx, (label, icon, done) ->
                    val active = activeBeat == bIdx
                    VisualBeatTab(
                        label = label,
                        icon = icon,
                        active = active,
                        done = done,
                        onClick = { activeBeat = bIdx },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            when (activeBeat) {
                0 -> {
                    // === BEAT 1: ILLUSTRATED STORYBOARD + BLIND LISTENING + COMIC BUBBLES ===
                    val scenePlaying = audioController.isPlaying && audioController.activeKey == "dlg_${ex.id}"
                    val sceneLoading = audioController.isLoading && audioController.activeKey == "dlg_${ex.id}"
                    VisualSituationStoryboardBanner(
                        illustrationRes = sceneIllustration,
                        sceneBadge = "${ex.speakers.size} Sprecher · ${ex.audioRequirements.regionalVariety}",
                        registerChip = ex.subRegister,
                        goalSummary = ex.learningObjective,
                        fullSceneNote = ex.germanInput,
                        isPlayingAudio = scenePlaying,
                        isLoadingAudio = sceneLoading,
                        fastRateLabel = if (fastNaturalRate) "1.16× Echt" else "0.96× Klar",
                        ttsDisclosureShort = "${audioController.engineBadge} · Multi-Speaker Studio Audio",
                        onTogglePlay = {
                            audioController.playDialogueScene(ex, if (fastNaturalRate) 1.16f else 0.96f)
                        },
                        onToggleSpeed = {
                            fastNaturalRate = !fastNaturalRate
                            audioController.updateSpeed(if (fastNaturalRate) 1.16f else 0.96f)
                        }
                    )

                    // Visual Progress Dots for the 3 Comprehension / Intention Probes
                    ex.comprehensionTasks.forEachIndexed { qIdx, probe ->
                        val picked = compAnswers[qIdx]
                        val typeBadgeColor = when (probe.type) {
                            "INTENTION" -> AmberGold
                            "IMPLICIT_MEANING" -> CoralCrimson
                            else -> CobaltBright
                        }
                        val shortType = when (probe.type) {
                            "INTENTION" -> "ABSICHT"
                            "IMPLICIT_MEANING" -> "ZWISCHENTON"
                            else -> "KERNINHALT"
                        }
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, typeBadgeColor.copy(alpha = 0.45f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = typeBadgeColor.copy(alpha = 0.14f)
                                    ) {
                                        Text(
                                            text = "${qIdx + 1}/3 · $shortType",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = typeBadgeColor,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = probe.question,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                probe.options.forEachIndexed { optIdx, opt ->
                                    val isSel = picked == optIdx
                                    val isCorr = optIdx == probe.correctIndex
                                    val bg = when {
                                        picked == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                        isCorr -> EmeraldSage.copy(alpha = 0.18f)
                                        isSel -> CoralCrimson.copy(alpha = 0.16f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                compAnswers[qIdx] = optIdx
                                                if (compAnswers.size >= 2) {
                                                    onBeatCompleted(ex.id, "BLIND_LISTEN")
                                                }
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = bg,
                                        border = BorderStroke(
                                            1.dp,
                                            if (picked != null && isCorr) EmeraldSage else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        )
                                    ) {
                                        Text(
                                            text = opt,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Locked Vault -> Visual Comic Bubble Stage + Tap-to-Morph Reductions!
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (transcriptUnlocked) MaterialTheme.colorScheme.surface else DeepInk,
                        border = BorderStroke(1.dp, if (transcriptUnlocked) EmeraldSage else AmberBright.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        if (transcriptUnlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = if (transcriptUnlocked) EmeraldSage else AmberBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (transcriptUnlocked) "COMIC-DIALOG & KLANG-MORPH" else "ERST 2 CHECKS LÖSEN (${compAnswers.size}/2)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (transcriptUnlocked) EmeraldSage else AmberBright
                                    )
                                }
                                if (!transcriptUnlocked) {
                                    Text(
                                        text = "Aufdecken",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.78f),
                                        modifier = Modifier.clickable { manualTranscriptReveal = true }
                                    )
                                }
                            }

                            if (transcriptUnlocked) {
                                VisualComicDialogueStage(
                                    turns = ex.transcriptTurns,
                                    onSpeakLine = { line -> speakGerman(line) }
                                )
                                InteractiveSpeechMorphStrip(reductions = ex.connectedSpeechReductions)
                            }
                        }
                    }
                }

                1 -> {
                    // === BEAT 2: VISUAL COUNTERPART CHALLENGE + HIDDEN-MODEL SPONTANEOUS SPEAKING ===
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = DeepInk,
                        border = BorderStroke(1.dp, AmberBright.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Rounded.VisibilityOff, contentDescription = null, tint = AmberBright, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "FREI SPRECHEN · MUSTER GESPERRT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AmberBright
                                    )
                                }
                                Surface(shape = RoundedCornerShape(99.dp), color = EmeraldSage.copy(alpha = 0.22f)) {
                                    Text(
                                        text = ex.subRegister,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldSage,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = ex.speakingTask,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            // Unexpected Counterpart Twist Bubble
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CoralCrimson.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, CoralCrimson.copy(alpha = 0.65f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = AmberBright, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = ex.interactionTask,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }

                            // Visual Target Criteria Chips instead of prose
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ex.expectedResponseCharacteristics.forEach { goalChip ->
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = Color.White.copy(alpha = 0.08f)
                                    ) {
                                        Text(
                                            text = "◎ $goalChip",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.86f),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = userSpontaneousDraft,
                        onValueChange = { userSpontaneousDraft = it },
                        label = { Text("Spontane Reaktion (in eigenen Worten)") },
                        placeholder = { Text("Direkt auf Deutsch reagieren …") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("spontaneous_reply_input")
                    )

                    Button(
                        onClick = {
                            if (userSpontaneousDraft.isNotBlank()) {
                                hasSubmittedSpeaking = true
                                onEvaluateSpontaneous(ex, userSpontaneousDraft)
                            }
                        },
                        enabled = userSpontaneousDraft.isNotBlank() && spontaneousEvalState !is SpontaneousEvalUiState.Loading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSage),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_spontaneous_button")
                    ) {
                        if (spontaneousEvalState is SpontaneousEvalUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("KI prüft 11-Kategorien-Profil...")
                        } else {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Senden & Register-Regler öffnen")
                        }
                    }

                    // ONLY AFTER SUBMISSION: Interactive Register Spectrum Dial + Visual AI Gauge
                    if (hasSubmittedSpeaking) {
                        InteractiveRegisterSpectrumDial(
                            baseline = ex.understandableBaseline,
                            alternatives = ex.acceptableAlternatives,
                            onSpeakPhrase = { phrase -> speakGerman(phrase) }
                        )

                        when (spontaneousEvalState) {
                            is SpontaneousEvalUiState.Success -> {
                                val ev = spontaneousEvalState.evaluation
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.5.dp, EmeraldSage)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldSage, modifier = Modifier.size(18.dp))
                                                Text(
                                                    text = "${ev.naturalnessPercent}% Natürlich",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldSage
                                                )
                                            }
                                            Surface(shape = RoundedCornerShape(8.dp), color = CobaltBright.copy(alpha = 0.14f)) {
                                                Text(
                                                    text = ev.primaryErrorCategory.targetFixLabel,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = CobaltBright,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        LinearProgressIndicator(
                                            progress = { ev.naturalnessPercent / 100f },
                                            color = EmeraldSage,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(7.dp)
                                                .clip(RoundedCornerShape(99.dp))
                                        )
                                        Text(ev.meaningFeedbackDe, style = MaterialTheme.typography.bodySmall)

                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            ev.naturalAlternatives.forEach { na ->
                                                Surface(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable { speakGerman(na) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = EmeraldSage.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = "🔊 „$na“",
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                color = CobaltBright.copy(alpha = 0.10f)
                                            ) {
                                                Column(modifier = Modifier.padding(9.dp)) {
                                                    Text("SOFORT-DRILL", style = MaterialTheme.typography.labelSmall, color = CobaltBright)
                                                    Text(ev.targetedRepetitionPromptDe, style = MaterialTheme.typography.bodySmall)
                                                }
                                            }
                                            Surface(
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                color = AmberGold.copy(alpha = 0.12f)
                                            ) {
                                                Column(modifier = Modifier.padding(9.dp)) {
                                                    Text("LIVE-TRANSFER", style = MaterialTheme.typography.labelSmall, color = AmberGold)
                                                    Text(ev.liveTransferChallengeDe, style = MaterialTheme.typography.bodySmall)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            is SpontaneousEvalUiState.Error -> {
                                Text(spontaneousEvalState.message, style = MaterialTheme.typography.bodySmall, color = CoralCrimson)
                            }
                            else -> Unit
                        }
                    }
                }

                2 -> {
                    // === BEAT 3: VISUAL 3-SLOT SENTENCE BLUEPRINT + PARTICLE TONE EQUALIZER ===
                    VisualChunkBlueprintBoard(
                        expressions = ex.usefulExpressions,
                        collocations = ex.vocabulary,
                        grammarPatterns = ex.grammarPatterns,
                        onSpeakChunk = { chunk -> speakGerman(chunk) }
                    )

                    InteractiveRegisterSpectrumDial(
                        baseline = ex.understandableBaseline,
                        alternatives = ex.acceptableAlternatives,
                        onSpeakPhrase = { phrase -> speakGerman(phrase) }
                    )
                }

                3 -> {
                    // === BEAT 4: VISUAL TRANSFER BRIDGE & DELAYED RETRIEVAL ANCHOR ===
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = DeepInk,
                        border = BorderStroke(1.5.dp, CobaltBright)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = EmeraldSage.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, EmeraldSage)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("ANKER", style = MaterialTheme.typography.labelSmall, color = EmeraldSage)
                                        Text(
                                            text = ex.delayedRetrievalTarget,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = AmberBright)
                                Surface(
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = CobaltBright.copy(alpha = 0.22f),
                                    border = BorderStroke(1.dp, CobaltBright)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("NEUE SITUATION", style = MaterialTheme.typography.labelSmall, color = AmberBright)
                                        Text(
                                            text = ex.transferTask,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = userTransferDraft,
                                onValueChange = { userTransferDraft = it },
                                label = { Text("Transfer-Antwort im neuen Kontext") },
                                minLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    transferCompleted = true
                                    onBeatCompleted(ex.id, "TRANSFER")
                                    if (userTransferDraft.isNotBlank()) {
                                        onEvaluateSpontaneous(ex, userTransferDraft)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CobaltBright),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Rounded.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Transfer sichern (${ex.reviewSchedule})")
                            }

                            if (transferCompleted) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { speakGerman(ex.delayedRetrievalTarget) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = AmberGold.copy(alpha = 0.20f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AmberBright)
                                        Text(
                                            text = "🔊 Gespeichert: „${ex.delayedRetrievalTarget}“",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditMetricTile(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = accent)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), maxLines = 1)
        }
    }
}

@Composable
private fun VisualBeatTab(
    label: String,
    icon: ImageVector,
    active: Boolean,
    done: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (active) EmeraldSage else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            when {
                active -> EmeraldSage
                done -> EmeraldSage.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (active) Color.White else if (done) EmeraldSage else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(17.dp)
                )
                if (done && !active) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .clickable(enabled = false) {}
                    )
                }
            }
            Text(
                text = if (done && !active) "✓ $label" else label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FamilyPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        color = if (selected) EmeraldSage else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) EmeraldSage else MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}
