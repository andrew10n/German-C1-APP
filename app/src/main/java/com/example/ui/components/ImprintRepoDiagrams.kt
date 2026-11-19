package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.ClauseSnapPuzzle
import com.example.domain.model.MentalDiagramArchetype
import com.example.domain.model.SentenceSurgeryItem
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent

/**
 * Custom Imprint-style visual diagram renderer for all 8 MentalDiagramArchetypes
 * from visualLessonDecks.ts, highlighting the active step's 3 nodes with smooth animated flow.
 */
@Composable
fun ImprintMentalModelCanvas(
    diagramType: MentalDiagramArchetype,
    modelTitle: String,
    visualHook: String,
    nodes: List<String>,
    highlightIndex: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val inf = rememberInfiniteTransition(label = "imprintDiagramAnim")
    val pulse by inf.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "nodePulse"
    )
    val flowPhase by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "flowDot"
    )

    val n0 = nodes.getOrElse(0) { "Impuls" }
    val n1 = nodes.getOrElse(1) { "Struktur" }
    val n2 = nodes.getOrElse(2) { "Wirkung" }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.4f))
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
                    color = palette.primaryAccent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "IMPRINT SCHEMA · ${diagramType.name}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.primaryAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "Step ${highlightIndex + 1}/3",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.imprintStageSubtext
                )
            }

            Text(
                text = modelTitle,
                style = MaterialTheme.typography.titleMedium,
                color = palette.imprintStageText
            )

            // Custom Animated Geometric Canvas matching the MentalDiagramArchetype
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
            ) {
                val w = size.width
                val h = size.height
                val x0 = w * 0.17f
                val x1 = w * 0.50f
                val x2 = w * 0.83f
                val cy = h * 0.52f
                val xs = listOf(x0, x1, x2)
                val trackColors = listOf(palette.secondaryAccent, palette.primaryAccent, palette.tertiaryAccent)

                when (diagramType) {
                    MentalDiagramArchetype.BRACKET, MentalDiagramArchetype.BRIDGE -> {
                        val arch = Path().apply {
                            moveTo(x0, cy + 16.dp.toPx())
                            quadraticTo(x1, 6.dp.toPx(), x2, cy + 16.dp.toPx())
                        }
                        drawPath(
                            path = arch,
                            brush = Brush.horizontalGradient(trackColors),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        val t = flowPhase
                        val qx = (1 - t) * (1 - t) * x0 + 2 * (1 - t) * t * x1 + t * t * x2
                        val qy = (1 - t) * (1 - t) * (cy + 16.dp.toPx()) + 2 * (1 - t) * t * 6.dp.toPx() + t * t * (cy + 16.dp.toPx())
                        drawCircle(palette.imprintStageText, radius = 4.5.dp.toPx(), center = Offset(qx, qy))
                    }
                    MentalDiagramArchetype.FUNNEL -> {
                        val funnel = Path().apply {
                            moveTo(w * 0.08f, 12.dp.toPx())
                            lineTo(w * 0.92f, 12.dp.toPx())
                            lineTo(w * 0.62f, h - 10.dp.toPx())
                            lineTo(w * 0.38f, h - 10.dp.toPx())
                            close()
                        }
                        drawPath(
                            path = funnel,
                            color = palette.primaryAccent.copy(alpha = 0.22f)
                        )
                        drawPath(
                            path = funnel,
                            color = palette.primaryAccent.copy(alpha = 0.72f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    MentalDiagramArchetype.SCALE -> {
                        drawLine(
                            color = palette.imprintStageSubtext.copy(alpha = 0.55f),
                            start = Offset(x0, cy + 8.dp.toPx()),
                            end = Offset(x2, cy - 8.dp.toPx()),
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        val fulcrum = Path().apply {
                            moveTo(x1, cy)
                            lineTo(x1 - 12.dp.toPx(), h - 8.dp.toPx())
                            lineTo(x1 + 12.dp.toPx(), h - 8.dp.toPx())
                            close()
                        }
                        drawPath(fulcrum, color = palette.primaryAccent)
                    }
                    MentalDiagramArchetype.LOOP -> {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(trackColors),
                            topLeft = Offset(x0 - 16.dp.toPx(), 16.dp.toPx()),
                            size = Size((x2 - x0) + 32.dp.toPx(), h - 32.dp.toPx()),
                            cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    MentalDiagramArchetype.MATRIX_2X2 -> {
                        drawLine(Color.White.copy(alpha = 0.2f), Offset(w * 0.1f, cy), Offset(w * 0.9f, cy), 1.5.dp.toPx())
                        drawLine(Color.White.copy(alpha = 0.2f), Offset(x1, 10.dp.toPx()), Offset(x1, h - 10.dp.toPx()), 1.5.dp.toPx())
                        drawLine(palette.primaryAccent, Offset(x0, cy + 12.dp.toPx()), Offset(x2, cy - 14.dp.toPx()), 3.dp.toPx(), StrokeCap.Round)
                    }
                    else -> {
                        drawLine(
                            brush = Brush.horizontalGradient(trackColors),
                            start = Offset(x0, cy),
                            end = Offset(x2, cy),
                            strokeWidth = 4.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        val px = x0 + (x2 - x0) * flowPhase
                        drawCircle(palette.imprintStageText, radius = 4.5.dp.toPx(), center = Offset(px, cy))
                    }
                }

                xs.forEachIndexed { idx, nx ->
                    val isActive = idx == highlightIndex
                    if (isActive) {
                        drawCircle(
                            color = palette.primaryAccent.copy(alpha = 0.30f),
                            radius = 20.dp.toPx() * pulse,
                            center = Offset(nx, cy)
                        )
                    }
                    drawCircle(
                        color = if (isActive) palette.primaryAccent else palette.imprintStageSurface,
                        radius = if (isActive) 13.dp.toPx() else 10.dp.toPx(),
                        center = Offset(nx, cy)
                    )
                    drawCircle(
                        color = if (isActive) contentColorOnAccent(palette.primaryAccent) else palette.imprintStageSubtext,
                        radius = 4.dp.toPx(),
                        center = Offset(nx, cy)
                    )
                }
            }

            // 3 Node Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(n0, n1, n2).forEachIndexed { idx, label ->
                    val active = idx == highlightIndex
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (active) palette.primaryAccent else Color.White.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (active) contentColorOnAccent(palette.primaryAccent) else palette.imprintStageText,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Text(
                text = visualHook,
                style = MaterialTheme.typography.bodySmall,
                color = palette.imprintStageSubtext
            )
        }
    }
}

/**
 * Interactive Clause Snap Puzzle from visualLessonDecks.ts.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveClauseSnapCard(
    puzzle: ClauseSnapPuzzle,
    onSolved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val selectedIndices = remember(puzzle.instruction) { mutableStateListOf<Int>() }
    var checked by remember(puzzle.instruction) { mutableStateOf(false) }

    val builtPhrase = selectedIndices.mapNotNull { puzzle.tiles.getOrNull(it) }
    val isSolved = checked && builtPhrase == puzzle.targetOrder

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(1.5.dp, if (isSolved) palette.tertiaryAccent else palette.borderStroke)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Extension, contentDescription = null, tint = palette.secondaryAccent)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SATZKLAMMER SNAP-PUZZLE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent
                    )
                    Text(
                        text = puzzle.instruction,
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.textPrimary
                    )
                }
            }

            // Constructed Sentence Tray
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (selectedIndices.isEmpty()) {
                    Text(
                        text = "Tippe unten auf die Satzbausteine in der korrekten C1-Reihenfolge …",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        selectedIndices.forEach { tileIdx ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        checked = false
                                        selectedIndices.remove(tileIdx)
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.primaryAccent
                            ) {
                                Text(
                                    text = puzzle.tiles[tileIdx],
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = contentColorOnAccent(palette.primaryAccent),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Available Tile Pool
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                puzzle.tiles.forEachIndexed { idx, tile ->
                    val used = idx in selectedIndices
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = !used) {
                                checked = false
                                selectedIndices.add(idx)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (used) palette.elevatedSurface.copy(alpha = 0.45f)
                        else palette.cardSurface,
                        border = BorderStroke(
                            1.dp,
                            if (used) palette.borderStroke.copy(alpha = 0.3f) else palette.primaryAccent.copy(alpha = 0.55f)
                        )
                    ) {
                        Text(
                            text = tile,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (used) palette.textSecondary.copy(alpha = 0.4f)
                            else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        checked = false
                        selectedIndices.clear()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reset")
                }
                Button(
                    onClick = {
                        checked = true
                        if (builtPhrase == puzzle.targetOrder) {
                            onSolved()
                        }
                    },
                    enabled = selectedIndices.size == puzzle.tiles.size,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primaryAccent,
                        contentColor = contentColorOnAccent(palette.primaryAccent)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.4f).height(48.dp)
                ) {
                    Text("Prüfen")
                }
            }

            if (checked) {
                val feedbackAccent = if (isSolved) palette.tertiaryAccent else palette.goldAccent
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = feedbackAccent.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = feedbackAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isSolved) "Perfekte C1-Satzklammer!" else "Ziel-Reihenfolge:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = feedbackAccent
                            )
                        }
                        if (!isSolved) {
                            Text(
                                text = puzzle.targetOrder.joinToString(" "),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = palette.textPrimary
                            )
                        }
                        Text(
                            text = "Regel: ${puzzle.syntaxRule}",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Visual Sentence Surgery Card from visualLessonDecks.ts.
 */
@Composable
fun SentenceSurgeryVisualCard(
    surgery: SentenceSurgeryItem,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(1.dp, palette.borderStroke)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, tint = palette.goldAccent)
                Column {
                    Text(
                        text = "SATZ-CHIRURGIE · ${surgery.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.goldAccent
                    )
                    Text(
                        text = "B2-Rohsatz → C1-Präzision",
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.textPrimary
                    )
                }
            }

            // Before
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.warningRed.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.28f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("VORHER (UNGESCHÄRFT)", style = MaterialTheme.typography.labelSmall, color = palette.warningRed)
                    Text("„${surgery.before}“", style = MaterialTheme.typography.bodyMedium, color = palette.textPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text("Diagnose: ${surgery.diagnosis}", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                }
            }

            // Surgical Steps
            surgery.steps.forEachIndexed { i, step ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(palette.goldAccent.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.goldAccent)
                    }
                    Text(step, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
                }
            }

            // After
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.tertiaryAccent.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, palette.tertiaryAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("C1-ZIELFORMULIERUNG", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.tertiaryAccent)
                    Text(
                        text = "„${surgery.after}“",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = palette.textPrimary
                    )
                }
            }
        }
    }
}
