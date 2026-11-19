package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.engine.CommanderOverrideConfig
import com.example.domain.engine.DailyBeatType
import com.example.domain.engine.MasterControlState
import com.example.domain.engine.PillarDiagnosticSnapshot
import com.example.domain.model.C1Pillar
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Transparent Real-Time Diagnostic Decision Log Card:
 * Shows *why* the Controller ordered Sector 1..N, detected latency / translation risk,
 * FSRS forgetting-cliff reviews, and mistake/pruner ROI.
 */
@Composable
fun VisibleControllerFeedbackCard(
    controlState: MasterControlState,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val pt = controlState.precisionTelemetry
    val isCommander = controlState.commanderConfig.commanderEnabled

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("visible_controller_feedback_card"),
        shape = RoundedCornerShape(22.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            width = 1.3.dp,
            color = if (isCommander) palette.primaryAccent.copy(alpha = 0.72f) else palette.borderStroke
        )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = (if (isCommander) palette.primaryAccent else palette.secondaryAccent).copy(alpha = 0.16f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isCommander) Icons.Rounded.Bolt else Icons.Rounded.Psychology,
                                contentDescription = null,
                                tint = if (isCommander) palette.primaryAccent else palette.secondaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isCommander) "CONTROLLER LOG · COMMANDER AKTIV" else "LIVE CONTROLLER FEEDBACK",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isCommander) palette.primaryAccent else palette.secondaryAccent
                        )
                        Text(
                            text = "${controlState.dailySessionQueue.size} aktive Sektoren · ${controlState.cognitiveLoad.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = if (pt.translationRiskDetected) palette.warningRed.copy(alpha = 0.14f)
                    else palette.tertiaryAccent.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = if (pt.translationRiskDetected) "⚠ ${pt.medianResponseSeconds}s Latenz"
                        else "✓ ${pt.medianResponseSeconds}s Direkt",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (pt.translationRiskDetected) palette.warningRed else palette.tertiaryAccent,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            // Visible 4-row decision feed computed by DiagnosticController
            controlState.decisionFeed.forEachIndexed { idx, event ->
                val accent = when {
                    event.isOverride -> palette.primaryAccent
                    idx == 0 -> palette.goldAccent
                    idx == 1 && pt.translationRiskDetected -> palette.warningRed
                    idx == 2 -> palette.secondaryAccent
                    else -> palette.tertiaryAccent
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (event.isOverride) accent.copy(alpha = 0.11f) else palette.elevatedSurface,
                    border = BorderStroke(
                        1.dp,
                        if (event.isOverride) accent.copy(alpha = 0.55f) else palette.borderStroke
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = event.titleDe,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = palette.textPrimary
                            )
                            Text(
                                text = event.detailDe,
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = accent.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = event.metricBadge,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Compact EV/min Sector Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                controlState.dailySessionQueue.forEach { beat ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.imprintStageBg,
                        border = BorderStroke(1.dp, palette.primaryAccent.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "S${beat.sectorNumber} ${beat.beatType.shortSectorLabel} · ${beat.evPerMinute.roundToInt()} EV/m",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = palette.imprintStageText,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Manual Commander Control Deck:
 * Toggle Autopilot vs. Commander Mode, pin a Focus Pillar into Sector 1,
 * choose which daily sector types run, and tune speech rate & difficulty pressure.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommanderControlDeck(
    config: CommanderOverrideConfig,
    pillarSnapshots: List<PillarDiagnosticSnapshot>,
    onSetCommanderEnabled: (Boolean) -> Unit,
    onPinFocusPillar: (C1Pillar?) -> Unit,
    onToggleBeatType: (DailyBeatType) -> Unit,
    onSetAudioSpeed: (Float) -> Unit,
    onSetDifficultyOffset: (Int) -> Unit,
    onResetAutopilot: () -> Unit,
    onLaunchConfiguredStint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val active = config.commanderEnabled

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("commander_control_deck"),
        shape = RoundedCornerShape(24.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(
            width = if (active) 1.6.dp else 1.1.dp,
            color = if (active) palette.primaryAccent else palette.borderStroke.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = (if (active) palette.primaryAccent else palette.goldAccent).copy(alpha = 0.18f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (active) Icons.Rounded.Tune else Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = if (active) palette.primaryAccent else palette.goldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (active) "COMMANDER MODUS AKTIV" else "AUTOPILOT · COMMANDER STEUERUNG",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (active) palette.primaryAccent else palette.goldAccent
                        )
                        Text(
                            text = if (active) "Deine Regeln steuern den Tages-Stint" else "Controller steuert — tippe um selbst zu übernehmen",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.imprintStageSubtext
                        )
                    }
                }

                Switch(
                    checked = active,
                    onCheckedChange = onSetCommanderEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = contentColorOnAccent(palette.primaryAccent),
                        checkedTrackColor = palette.primaryAccent,
                        uncheckedThumbColor = palette.imprintStageText,
                        uncheckedTrackColor = palette.imprintStageSurface
                    ),
                    modifier = Modifier.testTag("commander_mode_switch")
                )
            }

            // Always show quick controls smoothly (activating any immediately engages Commander Mode)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 1. Focus Pillar Override
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. FOKUS-SÄULE PINNEN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.imprintStageSubtext
                        )
                        if (active) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onResetAutopilot() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("commander_reset_btn"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.RestartAlt, contentDescription = "Autopilot", tint = palette.secondaryAccent, modifier = Modifier.size(14.dp))
                                Text("Autopilot", style = MaterialTheme.typography.labelSmall, color = palette.secondaryAccent)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isAutoPillar = config.pinnedFocusPillar == null
                        CommanderChip(
                            label = "Auto 80/20",
                            selected = isAutoPillar,
                            accent = palette.secondaryAccent,
                            onClick = { onPinFocusPillar(null) },
                            testTag = "cmd_pillar_auto"
                        )
                        pillarSnapshots.forEach { snap ->
                            val isPinned = config.pinnedFocusPillar == snap.pillar
                            CommanderChip(
                                label = "${snap.pillar.shortLabel} (${snap.mastery.roundToInt()}%)",
                                selected = isPinned,
                                accent = palette.primaryAccent,
                                onClick = {
                                    onPinFocusPillar(if (isPinned) null else snap.pillar)
                                },
                                testTag = "cmd_pillar_${snap.pillar.id}"
                            )
                        }
                    }
                }

                // 2. Active Stint Sector Mix Toggle
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "2. AKTIVE SEKTOREN IM TAGES-STINT (${config.enabledBeatTypes.size}/${DailyBeatType.entries.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.imprintStageSubtext
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DailyBeatType.entries.forEach { type ->
                            val enabled = type in config.enabledBeatTypes
                            Surface(
                                modifier = Modifier
                                    .testTag("cmd_beat_${type.name}")
                                    .defaultMinSize(minHeight = 36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onToggleBeatType(type) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (enabled) palette.secondaryAccent.copy(alpha = 0.22f) else palette.imprintStageSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (enabled) palette.secondaryAccent else palette.borderStroke.copy(alpha = 0.45f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (enabled) {
                                        Icon(
                                            Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = palette.secondaryAccent,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = type.shortSectorLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (enabled) palette.imprintStageText else palette.imprintStageSubtext
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Tempo & Difficulty Pressure
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = palette.goldAccent, modifier = Modifier.size(14.dp))
                            Text(
                                text = "AUDIO-TEMPO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = palette.imprintStageSubtext
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            listOf(0.90f to "0.9×", 1.00f to "Auto", 1.15f to "1.15×").forEach { (rate, label) ->
                                val selected = abs(config.audioSpeedBias - rate) < 0.04f
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { onSetAudioSpeed(rate) },
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (selected) palette.goldAccent else palette.imprintStageSurface
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) contentColorOnAccent(palette.goldAccent) else palette.imprintStageText,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "C1-DRUCK",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.imprintStageSubtext
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            listOf(-1 to "-1 Klar", 0 to "72%", 1 to "+1 C1+").forEach { (offset, label) ->
                                val selected = config.difficultyOffset == offset
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { onSetDifficultyOffset(offset) },
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (selected) palette.primaryAccent else palette.imprintStageSurface
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) contentColorOnAccent(palette.primaryAccent) else palette.imprintStageText,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = active,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    val ctaColor = contentColorOnAccent(palette.primaryAccent)
                    Button(
                        onClick = onLaunchConfiguredStint,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primaryAccent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("commander_apply_launch_btn")
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = ctaColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Commander-Stint starten",
                            color = ctaColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = ctaColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommanderChip(
    label: String,
    selected: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    testTag: String
) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = Modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 36.dp)
            .clip(RoundedCornerShape(99.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        color = if (selected) accent else palette.imprintStageSurface,
        border = BorderStroke(1.dp, if (selected) accent else palette.borderStroke.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (selected) contentColorOnAccent(accent) else palette.imprintStageText,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
        )
    }
}
