package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import com.example.ui.components.BranchingWorkplaceSimulator
import com.example.ui.components.SampleWorkplaceScenarios
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.HybridC1AudioController
import com.example.audio.PodcastEpisodeItem
import com.example.audio.PodcastRssClient
import com.example.domain.engine.DailySessionBeat
import com.example.domain.model.C1Pillar
import com.example.domain.model.ContextTrack
import com.example.domain.model.MediaImmersionMission
import com.example.domain.model.PostSessionAdaptationSummary
import com.example.ui.SituationEvalUiState
import com.example.ui.components.ImprintLiveWaveformBars
import com.example.ui.components.ImprintTelemetryTachometer
import com.example.ui.components.InteractiveTacticalPitchCanvas
import com.example.ui.components.VisualMediationFunnelCard
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EmbeddedTextbookBlueprintBeat(
    beat: DailySessionBeat,
    activeTrack: ContextTrack,
    onSpeakPhrase: (String) -> Unit,
    onRecordStep: (String, String, Float) -> Unit,
    onSectorDone: () -> Unit
) {
    val palette = LocalVibePalette.current
    val uItem = beat.repoUnit ?: return
    val unit = uItem.unit
    val deck = unit.visualDeck
    val transform = deck?.contextTransforms?.get(activeTrack)
        ?: deck?.contextTransforms?.values?.firstOrNull()
    val puzzle = deck?.clausePuzzle

    val selectedTiles = remember(unit.id) { mutableStateListOf<String>() }
    var puzzleSolved by remember(unit.id) { mutableStateOf(puzzle == null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.imprintStageBg,
            border = BorderStroke(1.2.dp, palette.secondaryAccent.copy(alpha = 0.55f))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = activeTrack.artworkRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.34f,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(palette.imprintStageBg.copy(alpha = 0.68f), palette.imprintStageBg.copy(alpha = 0.96f))
                            )
                        )
                )
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${unit.bookBridge?.shortBookBadge ?: "C1 LEHRWERK"} · ${activeTrack.shortLabel.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.primaryAccent
                    )
                    Text(
                        text = unit.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = palette.imprintStageText
                    )

                    if (transform != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = palette.warningRed.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("✕ FLACH (B2)", style = MaterialTheme.typography.labelSmall, color = palette.warningRed)
                                    Text(transform.avoidPhrase, style = MaterialTheme.typography.bodySmall, color = palette.imprintStageText)
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSpeakPhrase(transform.c1Phrase) },
                                shape = RoundedCornerShape(14.dp),
                                color = palette.tertiaryAccent.copy(alpha = 0.18f),
                                border = BorderStroke(1.2.dp, palette.tertiaryAccent)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = palette.tertiaryAccent, modifier = Modifier.size(14.dp))
                                        Text("✓ SOUVERÄN C1", style = MaterialTheme.typography.labelSmall, color = palette.tertiaryAccent)
                                    }
                                    Text(transform.c1Phrase, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = palette.imprintStageText)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (puzzle != null) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.dp, palette.borderStroke)
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
                        Text(
                            text = "SATZKLAMMER-SNAP · BAUSTEINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.primaryAccent
                        )
                        if (selectedTiles.isNotEmpty() && !puzzleSolved) {
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = palette.warningRed,
                                modifier = Modifier.clickable { selectedTiles.clear() }
                            )
                        }
                    }
                    Text(puzzle.instruction, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.imprintStageBg,
                        border = BorderStroke(1.2.dp, if (puzzleSolved) palette.tertiaryAccent else palette.borderStroke)
                    ) {
                        Text(
                            text = if (selectedTiles.isEmpty()) "Tippen Sie die Bausteine in C1-Reihenfolge an…"
                            else selectedTiles.joinToString(" "),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (puzzleSolved) palette.tertiaryAccent else palette.imprintStageText,
                            modifier = Modifier.fillMaxWidth().padding(14.dp)
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        puzzle.tiles.forEach { tile ->
                            val used = tile in selectedTiles
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(enabled = !used && !puzzleSolved) {
                                        selectedTiles.add(tile)
                                        if (selectedTiles.size == puzzle.targetOrder.size) {
                                            if (selectedTiles.toList() == puzzle.targetOrder) {
                                                puzzleSolved = true
                                                onRecordStep(unit.id, "VISUAL", 10f)
                                                onRecordStep(unit.id, "PUZZLE", 10f)
                                            } else {
                                                selectedTiles.clear()
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (used) palette.elevatedSurface.copy(alpha = 0.45f) else palette.secondaryAccent.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, if (used) palette.borderStroke else palette.secondaryAccent)
                            ) {
                                Text(
                                    text = tile,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (used) palette.textSecondary else palette.textPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (puzzleSolved) {
            Button(
                onClick = onSectorDone,
                colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("next_sector_btn")
            ) {
                Text(
                    "Satzbau sitzt · Nächster Sektor →",
                    color = contentColorOnAccent(palette.primaryAccent),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
internal fun EmbeddedLiveTransferBeat(
    beat: DailySessionBeat,
    situationEvalState: SituationEvalUiState,
    onEvaluateMediation: (String, String, String, String, C1Pillar) -> Unit,
    onSpeakCommand: (String) -> Unit,
    onSectorDone: () -> Unit
) {
    val palette = LocalVibePalette.current
    val fb = beat.footballScenario
    val med = beat.mediationScenario

    var showWorkplaceSimulator by remember(beat.id) {
        mutableStateOf(fb == null && med == null)
    }
    var selectedScenarioIndex by remember(beat.id) { mutableIntStateOf(0) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (fb != null || med != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRANSFER-MODUS",
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
                            .clickable { showWorkplaceSimulator = false }
                            .testTag("transfer_mode_standard"),
                        color = if (!showWorkplaceSimulator) palette.primaryAccent else Color.Transparent,
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text(
                            text = if (fb != null) "Taktik-Pitch" else "Mediation",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (!showWorkplaceSimulator) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .clickable { showWorkplaceSimulator = true }
                            .testTag("transfer_mode_roleplay"),
                        color = if (showWorkplaceSimulator) palette.primaryAccent else Color.Transparent,
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text(
                            text = "Exekutiv-Rollenspiel",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (showWorkplaceSimulator) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        if (showWorkplaceSimulator || (fb == null && med == null)) {
            // Workplace scenarios tabs: Sindelfingen (Mercedes), Weissach (Porsche), Dortmund (BVB)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SampleWorkplaceScenarios.forEachIndexed { index, spec ->
                    val isSelected = selectedScenarioIndex == index
                    val tabBadge = when (index) {
                        0 -> "Sindelfingen (MB)"
                        1 -> "Weissach (911)"
                        else -> "BVB (Presse)"
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedScenarioIndex = index }
                            .testTag("roleplay_scenario_tab_$index"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) palette.primaryAccent else palette.elevatedSurface.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, if (isSelected) palette.primaryAccent else palette.borderStroke)
                    ) {
                        Text(
                            text = tabBadge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            val activeScenario = SampleWorkplaceScenarios.getOrElse(selectedScenarioIndex) { SampleWorkplaceScenarios.first() }
            BranchingWorkplaceSimulator(
                scenario = activeScenario,
                onSpeakPhrase = onSpeakCommand,
                onSimulationDone = { _ -> onSectorDone() }
            )
        } else if (fb != null) {
        var chosenIdx by remember(fb.id) { mutableStateOf<Int?>(null) }
        val solved = chosenIdx != null

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InteractiveTacticalPitchCanvas(
                matchPhase = fb.matchPhase,
                title = fb.title,
                isSolved = chosenIdx == fb.correctIndex
            )

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.dp, palette.borderStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(fb.situationDescription, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                    fb.options.forEachIndexed { idx, opt ->
                        val isSelected = chosenIdx == idx
                        val isRight = idx == fb.correctIndex
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !solved) {
                                    chosenIdx = idx
                                    if (isRight) onSpeakCommand(fb.keyCommandToShout)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                solved && isRight -> palette.tertiaryAccent.copy(alpha = 0.16f)
                                isSelected && !isRight -> palette.warningRed.copy(alpha = 0.16f)
                                else -> palette.elevatedSurface.copy(alpha = 0.72f)
                            },
                            border = BorderStroke(
                                1.2.dp,
                                when {
                                    solved && isRight -> palette.tertiaryAccent
                                    isSelected && !isRight -> palette.warningRed
                                    else -> palette.borderStroke
                                }
                            )
                        ) {
                            Text(opt, style = MaterialTheme.typography.bodyMedium, color = palette.textPrimary, modifier = Modifier.padding(12.dp))
                        }
                    }

                    if (solved) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSpeakCommand(fb.keyCommandToShout) },
                            shape = RoundedCornerShape(12.dp),
                            color = palette.primaryAccent.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, palette.primaryAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = palette.primaryAccent)
                                Text(
                                    text = "Platz-Kommando: „${fb.keyCommandToShout}“",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = palette.primaryAccent
                                )
                            }
                        }

                        Button(
                            onClick = onSectorDone,
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_sector_btn")
                        ) {
                            Text(
                                "Stint-Sektor abschließen →",
                                color = contentColorOnAccent(palette.primaryAccent),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    } else if (med != null) {
        var draft by remember(med.id) { mutableStateOf("") }
        var submitted by remember(med.id) { mutableStateOf(false) }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VisualMediationFunnelCard(
                mediationType = med.mediationType,
                title = med.title,
                sourceMaterial = med.sourceMaterial,
                recipientContext = med.recipientContext,
                pitfalls = med.criticalPitfallsToAvoid
            )

            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Kernaussage klar & adressatengerecht auf Deutsch mitteln...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        submitted = true
                        onEvaluateMediation(
                            med.title,
                            med.sourceMaterial,
                            med.recipientContext,
                            draft,
                            C1Pillar.KONJUNKTIV
                        )
                    }
                },
                enabled = draft.isNotBlank() && situationEvalState !is SituationEvalUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                val fg = contentColorOnAccent(palette.primaryAccent)
                if (situationEvalState is SituationEvalUiState.Loading) {
                    CircularProgressIndicator(color = fg, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Prüft Mediation...", color = fg)
                } else {
                    Text("Mediation prüfen", color = fg, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            if (submitted) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.tertiaryAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, palette.tertiaryAccent)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("✓ C1-MUSTERFORMULIERUNG", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.tertiaryAccent)
                        Text(med.targetReformulationModel, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
                    }
                }
                Button(
                    onClick = onSectorDone,
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("next_sector_btn")
                ) {
                    Text(
                        "Stint-Sektor abschließen →",
                        color = contentColorOnAccent(palette.primaryAccent),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EmbeddedNativeMediaImmersionBeat(
    mission: MediaImmersionMission,
    audioController: HybridC1AudioController,
    onSpeakPhrase: (String) -> Unit,
    onCompleteMission: (Boolean) -> Unit
) {
    val palette = LocalVibePalette.current
    val context = LocalContext.current
    var episodes by remember(mission.id) { mutableStateOf<List<PodcastEpisodeItem>>(emptyList()) }
    var loadingFeed by remember(mission.id) { mutableStateOf(false) }
    var feedError by remember(mission.id) { mutableStateOf<String?>(null) }
    var chosenQuizOpt by remember(mission.id) { mutableStateOf<Int?>(null) }
    var speedFast by remember(mission.id) { mutableStateOf(false) }

    LaunchedEffect(mission.podcastFeedId) {
        if (!mission.podcastFeedId.isNullOrBlank()) {
            loadingFeed = true
            feedError = null
            PodcastRssClient.fetchEpisodes(context, mission.podcastFeedId).fold(
                onSuccess = { eps -> episodes = eps },
                onFailure = { err -> feedError = err.localizedMessage ?: "RSS-Feed nicht erreichbar." }
            )
            loadingFeed = false
        }
    }

    val activeEpisode = episodes.firstOrNull()
    val podKey = activeEpisode?.let { "pod_${it.audioUrl.hashCode()}" }
    val isThisPodPlaying = audioController.isPlaying && audioController.activeKey == podKey
    val isThisPodLoading = audioController.isLoading && audioController.activeKey == podKey

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.imprintStageBg,
            border = BorderStroke(1.3.dp, palette.secondaryAccent.copy(alpha = 0.6f))
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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = palette.secondaryAccent.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${mission.sourceType} · ${mission.sourceName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.secondaryAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "${mission.difficulty} · ${mission.goetheExamPartTag}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.primaryAccent
                    )
                }

                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.imprintStageText
                )

                ImprintLiveWaveformBars(
                    isPlaying = isThisPodPlaying,
                    accent = palette.secondaryAccent,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = mission.listeningMissionDe,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.imprintStageSubtext
                )

                if (loadingFeed) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(color = palette.secondaryAccent, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Text("Lade echte deutsche RSS-Folgen...", style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
                    }
                } else if (activeEpisode != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.06f),
                        border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.45f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "🎙 Live-Folge: ${activeEpisode.title}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = palette.imprintStageText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val btnBg = if (isThisPodPlaying) palette.warningRed else palette.secondaryAccent
                                val btnFg = contentColorOnAccent(btnBg)
                                Button(
                                    onClick = {
                                        audioController.togglePodcastStream(
                                            activeEpisode,
                                            speed = if (speedFast) 1.15f else 1.0f
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = btnBg),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    if (isThisPodLoading) {
                                        CircularProgressIndicator(color = btnFg, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(
                                            if (isThisPodPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                            contentDescription = "Stream Podcast",
                                            tint = btnFg
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = when {
                                            isThisPodLoading -> "Puffert..."
                                            isThisPodPlaying -> "Stopp"
                                            else -> "Live MP3 streamen"
                                        },
                                        color = btnFg,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        speedFast = !speedFast
                                        audioController.updateSpeed(if (speedFast) 1.15f else 1.0f)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text(if (speedFast) "1.15×" else "1.0×", color = palette.primaryAccent)
                                }
                            }
                            if (isThisPodPlaying) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { audioController.seekByMs(-10_000) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("-10s", color = palette.imprintStageText)
                                    }
                                    Text(
                                        text = "${audioController.positionMs / 1000}s",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.secondaryAccent
                                    )
                                    OutlinedButton(
                                        onClick = { audioController.seekByMs(15_000) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+15s", color = palette.imprintStageText)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mission.externalUrl))
                                runCatching { context.startActivity(intent) }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            val fg = contentColorOnAccent(palette.primaryAccent)
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (mission.sourceType == "BOOK_BRIDGE") "Buch-Modul öffnen" else "Originalquelle öffnen",
                                color = fg,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        OutlinedButton(
                            onClick = { onSpeakPhrase(mission.keyCollocationsToCatch.joinToString(". ")) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, palette.secondaryAccent),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Rounded.Headphones, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Studio-Audio", color = palette.secondaryAccent)
                        }
                    }
                }

                if (!feedError.isNullOrBlank()) {
                    Text(
                        text = "Hinweis: $feedError",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.goldAccent
                    )
                }

                if (mission.spokenFeaturesNoteDe.isNotBlank()) {
                    Text(
                        text = "🎧 Merkmale: ${mission.spokenFeaturesNoteDe}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.imprintStageSubtext
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = palette.cardSurface,
            border = BorderStroke(1.dp, palette.borderStroke)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "REDEWENDUNGEN ZUM RAUSHÖREN (ANTIPPEN = STUDIO-AUDIO)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.secondaryAccent
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mission.keyCollocationsToCatch.forEach { coll ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSpeakPhrase(coll) },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.imprintStageBg,
                            border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(14.dp))
                                Text(coll, style = MaterialTheme.typography.labelSmall, color = palette.imprintStageText)
                            }
                        }
                    }
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = palette.cardSurface,
            border = BorderStroke(1.dp, palette.borderStroke)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = mission.checkQuestionDe,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary
                )
                mission.checkOptions.forEachIndexed { idx, opt ->
                    val chosen = chosenQuizOpt
                    val isSelected = chosen == idx
                    val isRight = idx == mission.correctOptionIndex
                    val borderCol = when {
                        chosen != null && isRight -> palette.tertiaryAccent
                        isSelected && !isRight -> palette.warningRed
                        else -> palette.borderStroke
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = chosen == null) { chosenQuizOpt = idx },
                        shape = RoundedCornerShape(12.dp),
                        color = if (chosen != null && isRight) palette.tertiaryAccent.copy(alpha = 0.14f)
                        else if (isSelected) palette.warningRed.copy(alpha = 0.14f)
                        else palette.elevatedSurface.copy(alpha = 0.72f),
                        border = BorderStroke(1.2.dp, borderCol)
                    ) {
                        Text(opt, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary, modifier = Modifier.padding(11.dp))
                    }
                }

                AnimatedVisibility(
                    visible = chosenQuizOpt != null,
                    enter = expandVertically(tween(300)) + fadeIn(tween(300))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${mission.checkExplanationDe} · ${mission.activeReactionPromptDe}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = palette.secondaryAccent
                        )
                        Button(
                            onClick = { onCompleteMission(chosenQuizOpt == mission.correctOptionIndex) },
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_sector_btn")
                        ) {
                            Text(
                                "Medien-Sektor abschließen →",
                                color = contentColorOnAccent(palette.primaryAccent),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StintCompletedCard(
    completedCount: Int,
    functionalPercent: Int,
    examPercent: Int,
    minutesToday: Int,
    dailyGoalMinutes: Int,
    adaptationSummary: PostSessionAdaptationSummary?,
    onNextStint: () -> Unit,
    onOpenEngine: () -> Unit
) {
    val palette = LocalVibePalette.current
    val inf = rememberInfiniteTransition(label = "trophyGlow")
    val haloScale by inf.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "halo"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = palette.imprintStageBg,
            border = BorderStroke(1.5.dp, palette.primaryAccent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(68.dp).scale(haloScale),
                    shape = CircleShape,
                    color = palette.primaryAccent.copy(alpha = 0.2f),
                    border = BorderStroke(1.5.dp, palette.primaryAccent)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = palette.primaryAccent,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Text(
                    text = "ZIELFLAGGE · STINT KOMPLETT",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.primaryAccent
                )
                Text(
                    text = "$completedCount Controller-Sektoren absolviert",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.imprintStageText,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StintMetricPill("Live-C1", "$functionalPercent%", palette.primaryAccent, Modifier.weight(1f))
                    StintMetricPill("Goethe-C1", "$examPercent%", palette.secondaryAccent, Modifier.weight(1f))
                    StintMetricPill("Heute", "${minutesToday}/${dailyGoalMinutes}m", palette.tertiaryAccent, Modifier.weight(1f))
                }
            }
        }

        ImprintTelemetryTachometer(
            functionalPercent = functionalPercent,
            examPercent = examPercent,
            precisionPercent = adaptationSummary?.sessionAccuracyPercent ?: functionalPercent,
            automaticityPercent = functionalPercent
        )

        if (adaptationSummary != null) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.2.dp, palette.secondaryAccent.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth().testTag("post_session_adaptation_card")
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
                        Text(
                            text = "NACH-STINT-ADAPTION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.secondaryAccent
                        )
                        Surface(shape = RoundedCornerShape(99.dp), color = palette.primaryAccent.copy(alpha = 0.14f)) {
                            Text(
                                text = "${adaptationSummary.sessionAccuracyPercent}% · ${adaptationSummary.medianLatencySeconds}s RT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = palette.primaryAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = "Strategie: ${adaptationSummary.strategyModeAfter.title}" + if (adaptationSummary.strategyChanged) " (Angepasst)" else " (Aktiv)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = palette.textPrimary
                    )
                    Text(
                        text = adaptationSummary.primaryAdaptationReasonDe,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                    if (adaptationSummary.adjustedPillarsSummary.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            adaptationSummary.adjustedPillarsSummary.forEach { tag ->
                                Surface(shape = RoundedCornerShape(99.dp), color = palette.tertiaryAccent.copy(alpha = 0.14f)) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = palette.tertiaryAccent,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (adaptationSummary.nextStintPreviewTitles.isNotEmpty()) {
                        Text(
                            text = "Nächster Stint: ${adaptationSummary.nextStintPreviewTitles.take(3).joinToString(" → ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.secondaryAccent,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Button(
            onClick = onNextStint,
            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_next_stint_btn")
        ) {
            val fg = contentColorOnAccent(palette.primaryAccent)
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = fg)
            Spacer(Modifier.width(8.dp))
            Text("Nächsten Controller-Stint laden", color = fg, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        OutlinedButton(
            onClick = onOpenEngine,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.2.dp, palette.secondaryAccent.copy(alpha = 0.65f)),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Rounded.Tune, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Stint-Länge, Theme & Engine anpassen", color = palette.secondaryAccent)
        }
    }
}

@Composable
private fun StintMetricPill(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = accent)
            Text(label, style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
        }
    }
}
