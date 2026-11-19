package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.engine.MasterControlState
import com.example.ui.ArenaMode
import com.example.ui.ArenaUiState
import com.example.ui.components.C1RadarChart
import com.example.ui.components.VisualConceptInfographic
import com.example.ui.components.pillarAccentColor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage
import kotlin.math.roundToInt

@Composable
fun DiagnosticArenaScreen(
    controlState: MasterControlState,
    arenaState: ArenaUiState,
    onStartSession: (ArenaMode) -> Unit,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onDismissIntervention: () -> Unit,
    onAdvanceQuestion: () -> Unit,
    onOpenAiForge: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    LaunchedEffect(Unit) {
        if (arenaState.currentItem == null) {
            onStartSession(ArenaMode.ADAPTIVE_CAT)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("arena_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to Roadmap")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = arenaState.mode.title.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Adaptive CAT Arena",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${controlState.cefrLevel} · ${controlState.overallC1Index.roundToInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // 4 High-ROI Arena Mode Switcher Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ArenaModePill(
                    label = "80/20 CAT",
                    selected = arenaState.mode == ArenaMode.ADAPTIVE_CAT,
                    onClick = { onStartSession(ArenaMode.ADAPTIVE_CAT) },
                    modifier = Modifier.weight(1f).testTag("arena_mode_cat")
                )
                ArenaModePill(
                    label = "Fix (${controlState.paretoSummary.activeMistakesCount})",
                    selected = arenaState.mode == ArenaMode.MISTAKE_REPAIR,
                    onClick = { onStartSession(ArenaMode.MISTAKE_REPAIR) },
                    modifier = Modifier.weight(1f).testTag("arena_mode_repair")
                )
                ArenaModePill(
                    label = "Sweep",
                    selected = arenaState.mode == ArenaMode.CALIBRATION_SWEEP,
                    onClick = { onStartSession(ArenaMode.CALIBRATION_SWEEP) },
                    modifier = Modifier.weight(1f).testTag("arena_mode_sweep")
                )
                ArenaModePill(
                    label = "AI Drills",
                    selected = arenaState.mode == ArenaMode.AI_CUSTOM_DRILLS,
                    onClick = onOpenAiForge,
                    modifier = Modifier.weight(1f).testTag("arena_mode_ai")
                )
            }

            if (arenaState.sessionFinished) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.5.dp, EmeraldSage.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Rounded.Radar,
                            contentDescription = "Calibration Complete",
                            tint = EmeraldSage,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "DiagnosticController Recalibrated",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Accuracy: ${arenaState.correctInSession}/${arenaState.totalQuestions} · C1 Index: ${controlState.overallC1Index.roundToInt()}% · Pruned Low-ROI: ${controlState.paretoSummary.prunedItemsCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        C1RadarChart(snapshots = controlState.pillarSnapshots)

                        Button(
                            onClick = { onStartSession(arenaState.mode) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("arena_restart_button")
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Launch Next 80/20 Set")
                        }
                    }
                }
            } else if (arenaState.interventionSlide != null) {
                val slide = arenaState.interventionSlide
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = AmberGold.copy(alpha = 0.08f),
                    border = BorderStroke(1.5.dp, AmberGold)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Psychology,
                                contentDescription = "Controller Scaffolding",
                                tint = AmberGold
                            )
                            Text(
                                text = "CONTROLLER VISUAL INTERVENTION",
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberGold
                            )
                        }
                        Text(
                            text = "Consecutive errors detected — DiagnosticController injected this visual rule for ${slide.pillar.titleDe}:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = slide.headline,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        VisualConceptInfographic(
                            diagramType = slide.diagramType,
                            formulaBefore = slide.formulaBefore,
                            formulaAfter = slide.formulaAfter,
                            highlightRule = slide.highlightRule
                        )
                        Text(
                            text = slide.keyInsight,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                onDismissIntervention()
                                onAdvanceQuestion()
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("intervention_continue_button")
                        ) {
                            Text("Got the Visual — Continue CAT")
                        }
                    }
                }
            } else {
                val item = arenaState.currentItem
                if (item != null) {
                    val accent = pillarAccentColor(item.pillar)
                    val snap = controlState.pillarSnapshots.find { it.pillar == item.pillar }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Probe ${arenaState.questionNumber}/${arenaState.totalQuestions}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tier L${item.difficulty}/4 · ROI ${(snap?.roiScore ?: 25f).roundToInt()} pts",
                                style = MaterialTheme.typography.labelMedium,
                                color = accent
                            )
                        }
                        LinearProgressIndicator(
                            progress = { arenaState.questionNumber.toFloat() / arenaState.totalQuestions.coerceAtLeast(1) },
                            color = accent,
                            trackColor = accent.copy(alpha = 0.16f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(99.dp))
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = accent.copy(alpha = 0.14f)
                                ) {
                                    Text(
                                        text = item.pillar.shortLabel.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                if (snap?.isParetoVital20 == true) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AmberGold.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = "80/20 VITAL",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmberGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (item.isAiGenerated) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.AutoAwesome,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text("AI PROBE", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }

                            Text(
                                text = item.promptDe,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                            ) {
                                Text(
                                    text = item.contextSnippet,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                )
                            }

                            item.options.forEachIndexed { idx, opt ->
                                val selected = arenaState.selectedOptionIndex == idx
                                val isCorrect = idx == item.correctIndex
                                val bgColor = when {
                                    !arenaState.isSubmitted && selected -> MaterialTheme.colorScheme.primaryContainer
                                    !arenaState.isSubmitted -> MaterialTheme.colorScheme.surface
                                    isCorrect -> EmeraldSage.copy(alpha = 0.16f)
                                    selected -> CoralCrimson.copy(alpha = 0.16f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                                val borderColor = when {
                                    !arenaState.isSubmitted && selected -> MaterialTheme.colorScheme.primary
                                    !arenaState.isSubmitted -> MaterialTheme.colorScheme.outline
                                    isCorrect -> EmeraldSage
                                    selected -> CoralCrimson
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("arena_option_$idx")
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable(enabled = !arenaState.isSubmitted) {
                                            onSelectOption(idx)
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    color = bgColor,
                                    border = BorderStroke(1.5.dp, borderColor)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = opt,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (selected || (arenaState.isSubmitted && isCorrect)) FontWeight.SemiBold else FontWeight.Normal
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (arenaState.isSubmitted && isCorrect) {
                                            Icon(Icons.Rounded.CheckCircle, contentDescription = "Correct", tint = EmeraldSage)
                                        } else if (arenaState.isSubmitted && selected && !isCorrect) {
                                            Icon(Icons.Rounded.Cancel, contentDescription = "Incorrect", tint = CoralCrimson)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (arenaState.isSubmitted) {
                        val wasCorrect = arenaState.selectedOptionIndex == item.correctIndex
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, if (wasCorrect) EmeraldSage else CoralCrimson)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (wasCorrect) "✓ Mastery & Difficulty Tier Calibrated" else "✕ Added to Surgical Mistake Repair Ledger (${item.archetype.shortCode})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (wasCorrect) EmeraldSage else CoralCrimson
                                )
                                Text(
                                    text = item.explanationDe,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.explanationEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                VisualConceptInfographic(
                                    diagramType = item.visualDiagram,
                                    formulaBefore = item.contextSnippet,
                                    formulaAfter = item.options[item.correctIndex],
                                    highlightRule = item.archetype.labelDe
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = item.trapNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AmberGold
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (!arenaState.isSubmitted) onSubmitAnswer() else onAdvanceQuestion()
                        },
                        enabled = arenaState.selectedOptionIndex != null || arenaState.isSubmitted,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("arena_primary_action")
                    ) {
                        Text(
                            if (!arenaState.isSubmitted) "Evaluate & Adapt Difficulty"
                            else if (arenaState.questionNumber >= arenaState.totalQuestions) "View Diagnostic Summary"
                            else "Next High-ROI Probe"
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ArenaModePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
