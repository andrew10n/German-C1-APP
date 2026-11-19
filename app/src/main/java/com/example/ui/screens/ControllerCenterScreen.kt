package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BuildCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.engine.DailyBeatType
import com.example.domain.engine.MasterControlState
import com.example.domain.engine.PillarDiagnosticSnapshot
import com.example.domain.model.C1Pillar
import com.example.domain.model.ContextTrack
import com.example.domain.model.ControllerStrategyMode
import com.example.ui.AiOrchestrationUiState
import com.example.ui.components.GarageVibeGallerySelector
import com.example.ui.components.ImprintGoetheVisualMatrix
import com.example.ui.components.ImprintTelemetryTachometer
import com.example.ui.components.ParetoRoiVisualChart
import com.example.ui.components.pillarAccentColor
import com.example.ui.theme.AppVibeTheme
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.roundToInt

/**
 * Progress and personalization page:
 * Visual theme picker, daily session length, context track,
 * C1 readiness, diagnostics, and Goethe preparation overview.
 */
@Composable
fun ControllerCenterScreen(
    controlState: MasterControlState,
    selectedVibeTheme: AppVibeTheme = AppVibeTheme.PORSCHE_911,
    aiOrchestrationState: AiOrchestrationUiState,
    lastPruneCount: Int?,
    onSelectVibeTheme: (AppVibeTheme) -> Unit = {},
    onSelectContextTrack: (ContextTrack) -> Unit = {},
    onRunAiOptimizer: () -> Unit,
    onRunParetoPruneSweep: () -> Unit,
    onRestorePrunedItem: (String) -> Unit,
    onLaunchMistakeRepair: () -> Unit = {},
    onSelectStrategy: (ControllerStrategyMode) -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    onStartCalibration: () -> Unit = {},
    onJumpToPillarModule: (C1Pillar) -> Unit = {},
    onOpenAiStudio: () -> Unit = {},
    onResetTelemetry: () -> Unit,
    onSetCommanderEnabled: (Boolean) -> Unit = {},
    onPinFocusPillar: (C1Pillar?) -> Unit = {},
    onToggleBeatType: (DailyBeatType) -> Unit = {},
    onSetAudioSpeed: (Float) -> Unit = {},
    onSetDifficultyOffset: (Int) -> Unit = {},
    onResetCommanderAutopilot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val pareto = controlState.paretoSummary
    val pt = controlState.precisionTelemetry
    var showAdvancedPruner by remember { mutableStateOf(false) }
    var showAuditMatrix by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.canvasBg),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Signature Motorsport & BVB Garage Theme Switcher
            GarageVibeGallerySelector(
                selectedTheme = selectedVibeTheme,
                onSelectTheme = onSelectVibeTheme
            )

            // 2. Daily Session Length & Context Controller Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = palette.imprintStageBg,
                border = BorderStroke(1.3.dp, palette.primaryAccent.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Crossfade(
                        targetState = palette.heroDrawableRes,
                        animationSpec = tween(550, easing = FastOutSlowInEasing),
                        label = "engineBackdrop"
                    ) { drawableId ->
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = "Lernprofil",
                            contentScale = ContentScale.Crop,
                            alpha = 0.36f,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        palette.imprintStageBg.copy(alpha = 0.96f),
                                        palette.imprintStageBg.copy(alpha = 0.78f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Speed, contentDescription = null, tint = palette.primaryAccent)
                                Text(
                                    text = "MEIN LERNPROFIL",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = palette.primaryAccent
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = palette.secondaryAccent.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.55f))
                            ) {
                                Text(
                                    text = "${controlState.dailySessionQueue.size} Schritte heute",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.secondaryAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.Timer, contentDescription = null, tint = palette.imprintStageText, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Tägliche Stint-Länge",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = palette.imprintStageText
                                )
                            }
                            Text(
                                text = "${controlState.dailyGoalMinutes} Min.",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = palette.primaryAccent
                            )
                        }

                        Slider(
                            value = controlState.dailyGoalMinutes.toFloat(),
                            onValueChange = { onUpdateDailyGoal(it.roundToInt()) },
                            valueRange = 5f..45f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.primaryAccent,
                                activeTrackColor = palette.primaryAccent,
                                inactiveTrackColor = Color.White.copy(alpha = 0.22f)
                            ),
                            modifier = Modifier.testTag("daily_goal_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5 to "Sprint 5m", 15 to "15m", 25 to "25m", 35 to "Profi 35m").forEach { (mins, label) ->
                                val active = controlState.dailyGoalMinutes == mins
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onUpdateDailyGoal(mins) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (active) palette.primaryAccent else Color.White.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, if (active) palette.primaryAccent else Color.White.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (active) contentColorOnAccent(palette.primaryAccent) else palette.imprintStageText,
                                        maxLines = 1,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Text(
                            text = "KONTEXT-FOKUS FÜR TAGES-STINTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.imprintStageSubtext
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ContextTrack.entries.forEach { track ->
                                val selected = track == controlState.selectedContextTrack
                                val tag = when (track) {
                                    ContextTrack.ENGINEERING -> "⚡ AMG · 911"
                                    ContextTrack.COACHING -> "⚽ BVB · Taktik"
                                    ContextTrack.SOCIAL -> "🥨 Esslingen"
                                    ContextTrack.GENERAL -> "🏛 Alltag"
                                }
                                Surface(
                                    modifier = Modifier
                                        .testTag("track_chip_${track.id}")
                                        .clip(RoundedCornerShape(99.dp))
                                        .clickable { onSelectContextTrack(track) },
                                    shape = RoundedCornerShape(99.dp),
                                    color = if (selected) palette.secondaryAccent else Color.White.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, if (selected) palette.secondaryAccent else Color.White.copy(alpha = 0.22f))
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) contentColorOnAccent(palette.secondaryAccent) else palette.imprintStageText,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onRunAiOptimizer,
                            enabled = aiOrchestrationState !is AiOrchestrationUiState.Running,
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("ai_curriculum_optimizer_btn")
                        ) {
                            val btnFg = contentColorOnAccent(palette.primaryAccent)
                            if (aiOrchestrationState is AiOrchestrationUiState.Running) {
                                CircularProgressIndicator(color = btnFg, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("KI optimiert Stint-Rotation...", color = btnFg)
                            } else {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = btnFg, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("AI 80/20 Stint-Inhalte optimieren", color = btnFg)
                            }
                        }
                    }
                }
            }

            // Live Firebase AI Orchestration Feedback
            when (aiOrchestrationState) {
                is AiOrchestrationUiState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.warningRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, palette.warningRed)
                    ) {
                        Text(
                            text = aiOrchestrationState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.warningRed,
                            modifier = Modifier.fillMaxWidth().padding(14.dp)
                        )
                    }
                }
                is AiOrchestrationUiState.Success -> {
                    val rep = aiOrchestrationState.report
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = palette.tertiaryAccent.copy(alpha = 0.12f),
                        border = BorderStroke(1.2.dp, palette.tertiaryAccent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "✓ NEUE KI-INHALTE IN TAGES-STINT EINGEBETTET",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = palette.tertiaryAccent
                            )
                            Text(rep.executiveSummary, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
                        }
                    }
                }
                else -> Unit
            }

            // 3. Visible Real-Time Controller Decision Telemetry + Tachometer + Pareto Chart
            VisibleControllerFeedbackCard(controlState = controlState)

            CommanderControlDeck(
                config = controlState.commanderConfig,
                pillarSnapshots = controlState.pillarSnapshots,
                onSetCommanderEnabled = onSetCommanderEnabled,
                onPinFocusPillar = onPinFocusPillar,
                onToggleBeatType = onToggleBeatType,
                onSetAudioSpeed = onSetAudioSpeed,
                onSetDifficultyOffset = onSetDifficultyOffset,
                onResetAutopilot = onResetCommanderAutopilot,
                onLaunchConfiguredStint = onLaunchMistakeRepair
            )

            ImprintTelemetryTachometer(
                functionalPercent = controlState.functionalC1ReadinessPercent,
                examPercent = controlState.examReadinessPercent,
                precisionPercent = pt.diagnosticPrecisionPercent,
                automaticityPercent = pt.automaticityIndexPercent
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.dp, palette.borderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParetoRoiVisualChart(snapshots = controlState.pillarSnapshots)
                }
            }

            // 4. Controller Strategy Selector
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.dp, palette.borderStroke)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Controller-Strategie",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    ControllerStrategyMode.entries.forEach { mode ->
                        val selected = mode == controlState.strategyMode
                        val itemScale by animateFloatAsState(
                            targetValue = if (selected) 1f else 0.99f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "stratScale"
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(itemScale)
                                .testTag("strategy_mode_${mode.id}")
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectStrategy(mode) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected) palette.primaryAccent.copy(alpha = 0.14f) else palette.elevatedSurface,
                            border = BorderStroke(1.2.dp, if (selected) palette.primaryAccent else palette.borderStroke)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mode.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) palette.primaryAccent else palette.textPrimary
                                    )
                                    Text(
                                        text = mode.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (selected) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 5. Flat-6 Cylinder Pillar Telemetry
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flat-6 C1 Telemetrie",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    Text(
                        text = "Live ${controlState.functionalC1ReadinessPercent}% · Prüf ${controlState.examReadinessPercent}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent
                    )
                }

                controlState.pillarSnapshots.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { snap ->
                            FlatSixCylinderTile(snap = snap, modifier = Modifier.weight(1f))
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            // 6. Collapsible Low-ROI Pruner, Mistakes & Telemetry Reset
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.dp, palette.borderStroke)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showAdvancedPruner = !showAdvancedPruner }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.Tune, contentDescription = null, tint = palette.secondaryAccent)
                            Column {
                                Text("Filter, Fehlerspeicher & Reset", style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
                                Text(
                                    text = "${pareto.activeMistakesCount} Fehler · ${pareto.prunedItemsCount} ausgesiebt (${pareto.minutesSavedByPruning}m gespart)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = if (showAdvancedPruner) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            tint = palette.textSecondary
                        )
                    }

                    AnimatedVisibility(
                        visible = showAdvancedPruner,
                        enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                        exit = shrinkVertically(tween(240)) + fadeOut(tween(220))
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (controlState.unresolvedMistakes.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Rounded.BuildCircle, contentDescription = null, tint = palette.warningRed, modifier = Modifier.size(16.dp))
                                    Text("Aktive Fehler:", style = MaterialTheme.typography.labelSmall, color = palette.warningRed)
                                }
                                controlState.unresolvedMistakes.take(3).forEach { m ->
                                    Text(
                                        text = "• ✕ ${m.wrongChoice} → ✓ ${m.correctForm}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textPrimary
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onRunParetoPruneSweep,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("pareto_prune_sweep_btn")
                            ) {
                                Icon(Icons.Rounded.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("80/20 Low-ROI Filter ausführen")
                            }

                            if (lastPruneCount != null) {
                                Text(
                                    text = if (lastPruneCount > 0) "$lastPruneCount einfache Items aus Stints entfernt."
                                    else "Stint-Pool ist bereits optimal gefiltert.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.tertiaryAccent
                                )
                            }

                            controlState.prunedItems.take(3).forEach { pruned ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = pruned.titleLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { onRestorePrunedItem(pruned.contentId) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(Icons.Rounded.Restore, contentDescription = "Wiederherstellen", tint = palette.secondaryAccent)
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = onResetTelemetry,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reset_diagnostics_btn")
                            ) {
                                Icon(Icons.Rounded.RestartAlt, contentDescription = null, tint = palette.warningRed)
                                Spacer(Modifier.width(8.dp))
                                Text("Controller-Telemetrie zurücksetzen", color = palette.warningRed)
                            }
                        }
                    }
                }
            }

            // 7. 365-Tage Release-Audit & Visual Goethe C1 Matrix
            controlState.yearAuditReport?.let { audit ->
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = palette.imprintStageBg,
                    border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .clickable { showAuditMatrix = !showAuditMatrix }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "365-TAGE GOETHE C1 MATRIX",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.secondaryAccent
                                )
                                Text(
                                    text = "${audit.totalMultiSpeakerScenarios} Szenen · ${audit.totalLiveMediaMissions} Podcasts/YT · ${audit.totalContextualVocabChunks} Chunks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.imprintStageSubtext
                                )
                            }
                            Icon(
                                imageVector = if (showAuditMatrix) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = "Audit Matrix",
                                tint = palette.secondaryAccent
                            )
                        }

                        AnimatedVisibility(
                            visible = showAuditMatrix,
                            enter = expandVertically(tween(320)) + fadeIn(tween(320)),
                            exit = shrinkVertically(tween(240)) + fadeOut(tween(220))
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ImprintGoetheVisualMatrix(modules = audit.goetheExamMatrix)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun FlatSixCylinderTile(
    snap: PillarDiagnosticSnapshot,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val accent = if (snap.isParetoVital20) palette.primaryAccent else pillarAccentColor(snap.pillar)
    val animMastery by animateFloatAsState(
        targetValue = (snap.mastery / 100f).coerceIn(0.06f, 1f),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 120f),
        label = "cyl_${snap.pillar.id}"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            width = if (snap.isParetoVital20) 1.5.dp else 1.dp,
            color = if (snap.isParetoVital20) palette.primaryAccent.copy(alpha = 0.65f) else palette.borderStroke
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pad = 4.dp.toPx()
                    val arcSize = Size(size.width - pad * 2, size.height - pad * 2)
                    drawArc(
                        color = accent.copy(alpha = 0.18f),
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = accent,
                        startAngle = 140f,
                        sweepAngle = 260f * animMastery,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${snap.mastery.roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = snap.pillar.shortLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (snap.isParetoVital20) "★ Vital · L${snap.targetDifficultyTier}" else "Tier L${snap.targetDifficultyTier}",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }
    }
}
