package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.C1Pillar
import com.example.domain.model.ContextTrack
import com.example.domain.model.PrioritizedRepoUnit
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent

enum class NodeHorizontalSlot(val fractionX: Float) {
    LEFT(0.18f),
    CENTER(0.50f),
    RIGHT(0.82f)
}

fun roadmapSlotForIndex(index: Int): NodeHorizontalSlot = when (index % 4) {
    0 -> NodeHorizontalSlot.LEFT
    1 -> NodeHorizontalSlot.CENTER
    2 -> NodeHorizontalSlot.RIGHT
    else -> NodeHorizontalSlot.CENTER
}

fun pillarIcon(pillar: C1Pillar): ImageVector = when (pillar) {
    C1Pillar.NOMINALISIERUNG -> Icons.Rounded.AccountTree
    C1Pillar.KONJUNKTIV -> Icons.Rounded.Forum
    C1Pillar.SATZGEFUEGE -> Icons.Rounded.Hub
    C1Pillar.NOMEN_VERB -> Icons.Rounded.Extension
    C1Pillar.MODALPARTIKELN -> Icons.Rounded.AutoAwesome
    C1Pillar.RHETORIK -> Icons.Rounded.Gavel
}

/**
 * Draws a sculpted S-curve cubic Bezier connector between two roadmap stations.
 */
@Composable
fun RoadmapSCurveConnector(
    fromSlot: NodeHorizontalSlot,
    toSlot: NodeHorizontalSlot,
    fromColor: Color,
    toColor: Color,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        val startX = size.width * fromSlot.fractionX
        val endX = size.width * toSlot.fractionX
        val midY = size.height * 0.5f

        val path = Path().apply {
            moveTo(startX, 0f)
            cubicTo(
                startX, midY,
                endX, midY,
                endX, size.height
            )
        }

        if (isHighlighted) {
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    listOf(fromColor.copy(alpha = 0.24f), toColor.copy(alpha = 0.24f))
                ),
                style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                listOf(
                    fromColor.copy(alpha = if (isHighlighted) 0.9f else 0.42f),
                    toColor.copy(alpha = if (isHighlighted) 0.9f else 0.42f)
                )
            ),
            style = Stroke(
                width = if (isHighlighted) 3.5.dp.toPx() else 2.2.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = if (isHighlighted) null else PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            )
        )

        val midX = (startX + endX) / 2f
        drawCircle(
            color = if (isHighlighted) fromColor else toColor.copy(alpha = 0.5f),
            radius = 3.5.dp.toPx(),
            center = Offset(midX, midY)
        )
    }
}

/**
 * Communicative Stage (Stufe 0..6) Divider Banner along the C1 Roadmap.
 */
@Composable
fun RoadmapStageHeader(
    stageLabel: String,
    title: String,
    subtitle: String,
    accent: Color,
    masteryBadge: String? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.11f),
        border = BorderStroke(1.1.dp, accent.copy(alpha = 0.40f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stageLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            }
            if (masteryBadge != null) {
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = accent.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = masteryBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Interactive Roadmap Station for each of the 28 Authentic Repo Curriculum Units
 * (Aspekte neu C1 / Sicher! C1 / Imprint Visual Deck / 4-Track Situations).
 */
@Composable
fun RoadmapRepoUnitStation(
    item: PrioritizedRepoUnit,
    unitOrderNumber: Int,
    activeContextTrack: ContextTrack,
    slot: NodeHorizontalSlot,
    isExpanded: Boolean,
    unresolvedMistakesCount: Int,
    isPinnedInCommander: Boolean = false,
    onToggleExpand: () -> Unit,
    onOpenUnitStudio: () -> Unit,
    onStartUnitDrill: () -> Unit,
    onOpenSituationsLab: () -> Unit,
    onPinToEngine: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val pillarColor = pillarAccentColor(item.unit.mappedPillar)
    val accent = when {
        unresolvedMistakesCount > 0 -> palette.warningRed
        isPinnedInCommander -> palette.primaryAccent
        item.isParetoVital20 -> palette.goldAccent
        item.masteryPercent >= 85 || item.completedStepsCount >= 4 -> palette.tertiaryAccent
        else -> pillarColor
    }
    val bookBadge = item.unit.bookBridge?.shortBookBadge ?: "Stufe ${item.unit.stage}"
    val contextGoal = item.unit.contexts[activeContextTrack]
        ?: item.unit.objectives.firstOrNull().orEmpty()
    val visualTransform = item.unit.visualDeck?.contextTransforms?.get(activeContextTrack)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("repo_unit_${item.unit.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val isRightAligned = slot == NodeHorizontalSlot.RIGHT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable { onToggleExpand() }
                .padding(vertical = 3.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isRightAligned) Arrangement.End else Arrangement.Start
        ) {
            if (slot == NodeHorizontalSlot.CENTER) {
                Spacer(Modifier.width(18.dp))
            }

            if (isRightAligned) {
                RepoUnitPlinth(
                    item = item,
                    bookBadge = bookBadge,
                    contextGoal = contextGoal,
                    accent = accent,
                    isExpanded = isExpanded,
                    isPinnedInCommander = isPinnedInCommander,
                    unresolvedMistakesCount = unresolvedMistakesCount,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                SculptedMasteryMedallion(
                    rank = unitOrderNumber,
                    pillar = item.unit.mappedPillar,
                    masteryPercent = item.masteryPercent,
                    isVital = item.isParetoVital20 || isPinnedInCommander,
                    isSelected = isExpanded,
                    accent = accent
                )
            } else {
                SculptedMasteryMedallion(
                    rank = unitOrderNumber,
                    pillar = item.unit.mappedPillar,
                    masteryPercent = item.masteryPercent,
                    isVital = item.isParetoVital20 || isPinnedInCommander,
                    isSelected = isExpanded,
                    accent = accent
                )
                Spacer(Modifier.width(12.dp))
                RepoUnitPlinth(
                    item = item,
                    bookBadge = bookBadge,
                    contextGoal = contextGoal,
                    accent = accent,
                    isExpanded = isExpanded,
                    isPinnedInCommander = isPinnedInCommander,
                    unresolvedMistakesCount = unresolvedMistakesCount,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Expandable Unit Launchpad with Book Reference, Context Situation, Imprint Preview & Commander Pin
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.cardSurface,
                border = BorderStroke(1.5.dp, accent.copy(alpha = 0.72f)),
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = accent.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "${item.statusBadge} · $bookBadge · L${item.targetDifficultyTier}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "${item.completedStepsCount}/4 Studio-Phasen",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary
                        )
                    }

                    // Exact Textbook Bridge Citation (Aspekte neu C1 / Sicher! C1)
                    item.unit.bookBridge?.let { bridge ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Rounded.MenuBook,
                                contentDescription = null,
                                tint = palette.secondaryAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = bridge.bookReference,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = palette.secondaryAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Active Track Communicative Situation Goal
                    Text(
                        text = "[${activeContextTrack.shortLabel}] $contextGoal",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textPrimary
                    )

                    // Imprint Visual Before -> After Preview for Active Track
                    if (visualTransform != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = palette.imprintStageBg,
                            border = BorderStroke(1.dp, palette.borderStroke.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "IMPRINT VISUAL · ${visualTransform.sceneLabel.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.goldAccent
                                )
                                Text(
                                    text = "✗ ${visualTransform.avoidPhrase}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.warningRed,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "✓ ${visualTransform.c1Phrase}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = palette.tertiaryAccent,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Primary CTA: Open 4-Phase Unit Studio
                    val onCtaColor = contentColorOnAccent(accent)
                    Button(
                        onClick = onOpenUnitStudio,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_studio_${item.unit.id}")
                    ) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = onCtaColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (item.completedStepsCount > 0) "Unit-Studio fortsetzen (${item.completedStepsCount}/4)" else "Unit-Studio starten (Visual + Rolle)",
                            color = onCtaColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = onCtaColor)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onStartUnitDrill,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("drill_repo_unit_${item.unit.id}")
                        ) {
                            Icon(Icons.Rounded.Quiz, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("CAT Probe", color = accent, style = MaterialTheme.typography.labelMedium)
                        }
                        if (onPinToEngine != null) {
                            OutlinedButton(
                                onClick = onPinToEngine,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (isPinnedInCommander) 1.5.dp else 1.dp,
                                    color = palette.primaryAccent.copy(alpha = 0.65f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pin_engine_${item.unit.id}")
                            ) {
                                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = if (isPinnedInCommander) "Gepinnt ✓" else "Fokus setzen",
                                    color = palette.primaryAccent,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = onOpenSituationsLab,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("sit_lab_${item.unit.id}")
                            ) {
                                Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Situationen", color = palette.secondaryAccent, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoUnitPlinth(
    item: PrioritizedRepoUnit,
    bookBadge: String,
    contextGoal: String,
    accent: Color,
    isExpanded: Boolean,
    isPinnedInCommander: Boolean,
    unresolvedMistakesCount: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            width = if (isExpanded || isPinnedInCommander || item.isParetoVital20) 1.5.dp else 1.dp,
            color = when {
                isExpanded -> accent
                isPinnedInCommander -> palette.primaryAccent.copy(alpha = 0.78f)
                item.isParetoVital20 -> palette.goldAccent.copy(alpha = 0.65f)
                else -> palette.borderStroke
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = when {
                        unresolvedMistakesCount > 0 -> "FIX $unresolvedMistakesCount FEHLER"
                        isPinnedInCommander -> "COMMANDER FOKUS ⚡"
                        else -> item.statusBadge
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = palette.secondaryAccent.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = bookBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.secondaryAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                text = item.unit.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = palette.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = contextGoal,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SculptedMasteryMedallion(
    rank: Int,
    pillar: C1Pillar,
    masteryPercent: Int,
    isVital: Boolean,
    isSelected: Boolean,
    accent: Color
) {
    val palette = LocalVibePalette.current
    Box(
        modifier = Modifier.size(74.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 5.dp.toPx()
            val pad = strokeWidth / 2f + 3.dp.toPx()
            val arcSize = Size(size.width - pad * 2, size.height - pad * 2)

            if (isVital || isSelected) {
                drawCircle(
                    color = accent.copy(alpha = if (isSelected) 0.22f else 0.12f),
                    radius = size.minDimension / 2f
                )
            }

            drawArc(
                color = accent.copy(alpha = 0.18f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = (masteryPercent.coerceIn(4, 100) / 100f) * 360f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        val selectedFg = contentColorOnAccent(accent)
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = if (isSelected) accent else palette.cardSurface,
            border = BorderStroke(1.5.dp, accent)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (masteryPercent >= 85) Icons.Rounded.Check else pillarIcon(pillar),
                    contentDescription = pillar.titleDe,
                    tint = if (isSelected) selectedFg else accent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$masteryPercent%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) selectedFg else palette.textPrimary
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = palette.imprintStageBg,
            border = BorderStroke(1.dp, accent),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.imprintStageText
                )
            }
        }
    }
}

/**
 * Interactive Milestone Checkpoint Gate along the Roadmap (Situations-Labor / CAT / AI Summit).
 */
@Composable
fun RoadmapMilestoneGate(
    badge: String,
    title: String,
    subtitle: String,
    ctaLabel: String,
    accent: Color,
    isDarkHero: Boolean = false,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val cardBg = if (isDarkHero) palette.imprintStageBg else palette.cardSurface
    val titleColor = if (isDarkHero) palette.imprintStageText else palette.textPrimary
    val subColor = if (isDarkHero) palette.imprintStageSubtext else palette.textSecondary
    val badgeColor = if (isDarkHero) palette.goldAccent else accent

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = badgeColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = badge.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subColor
                )
            }
            val pillBg = badgeColor
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = pillBg
            ) {
                Text(
                    text = ctaLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColorOnAccent(pillBg),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }
    }
}

/**
 * Structured Overview Blueprint Card for any of the 28 Repo Units (filterable by Stage / Book).
 */
@Composable
fun BlueprintOverviewRepoUnitCard(
    item: PrioritizedRepoUnit,
    activeContextTrack: ContextTrack,
    onOpenUnitStudio: () -> Unit,
    onOpenUnitDrill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val accent = if (item.isParetoVital20) palette.goldAccent else pillarAccentColor(item.unit.mappedPillar)
    val bookRef = item.unit.bookBridge?.bookReference ?: "Stufe ${item.unit.stage}"
    val contextGoal = item.unit.contexts[activeContextTrack]
        ?: item.unit.objectives.firstOrNull().orEmpty()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onOpenUnitStudio() },
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            if (item.isParetoVital20) 1.5.dp else 1.dp,
            if (item.isParetoVital20) palette.goldAccent.copy(alpha = 0.65f) else palette.borderStroke
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(pillarIcon(item.unit.mappedPillar), contentDescription = null, tint = accent)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STUFE ${item.unit.stage} · ${item.statusBadge}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = accent
                        )
                        Text(
                            text = item.unit.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    text = "${item.masteryPercent}%",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = accent
                )
            }

            Text(
                text = "📚 $bookRef",
                style = MaterialTheme.typography.labelSmall,
                color = palette.secondaryAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "[${activeContextTrack.shortLabel}] $contextGoal",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            LinearProgressIndicator(
                progress = { item.masteryPercent / 100f },
                color = accent,
                trackColor = accent.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.completedStepsCount}/4 Phasen · ROI ${item.roiScore.toInt()} · Tier L${item.targetDifficultyTier}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenUnitDrill() },
                        shape = RoundedCornerShape(10.dp),
                        color = accent.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
                            Text("Probe", style = MaterialTheme.typography.labelSmall, color = accent)
                        }
                    }
                    val onAccent = contentColorOnAccent(accent)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenUnitStudio() },
                        shape = RoundedCornerShape(10.dp),
                        color = accent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = onAccent, modifier = Modifier.size(14.dp))
                            Text("Studio", style = MaterialTheme.typography.labelSmall, color = onAccent)
                        }
                    }
                }
            }
        }
    }
}
