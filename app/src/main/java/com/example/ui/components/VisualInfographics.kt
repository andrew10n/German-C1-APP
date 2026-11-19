package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.engine.PillarDiagnosticSnapshot
import com.example.domain.model.C1Pillar
import com.example.domain.model.VisualDiagramType
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberSoftContainer
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CobaltInk
import com.example.ui.theme.CobaltSoftContainer
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.CoralSoftContainer
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.EmeraldSoftContainer
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.PlumIndigo
import com.example.ui.theme.PlumSoftContainer
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun pillarAccentColor(pillar: C1Pillar): Color = when (pillar) {
    C1Pillar.NOMINALISIERUNG -> CobaltBright
    C1Pillar.KONJUNKTIV -> PlumIndigo
    C1Pillar.SATZGEFUEGE -> EmeraldSage
    C1Pillar.NOMEN_VERB -> AmberGold
    C1Pillar.MODALPARTIKELN -> CoralCrimson
    C1Pillar.RHETORIK -> CobaltInk
}

@Composable
fun DailyProgressRing(
    progress: Float,
    centerTitle: String,
    centerSubtitle: String,
    accentColor: Color = LocalVibePalette.current.primaryAccent,
    size: Dp = 76.dp,
    modifier: Modifier = Modifier,
    centerTextColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "ringProgress"
    )
    val trackColor = centerTextColor.copy(alpha = 0.18f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 7.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = accentColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerTitle,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = centerTextColor
            )
            Text(
                text = centerSubtitle,
                style = MaterialTheme.typography.labelSmall,
                color = centerTextColor.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
fun C1RadarChart(
    snapshots: List<PillarDiagnosticSnapshot>,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val ordered = C1Pillar.entries.map { pillar ->
        snapshots.find { it.pillar == pillar }
    }
    val primaryColor = palette.primaryAccent
    val secondaryColor = palette.secondaryAccent
    val outlineColor = palette.borderStroke
    val targetColor = palette.tertiaryAccent
    val dotHalo = palette.cardSurface

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(180.dp)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f - 8.dp.toPx()
                val count = 6

                // Concentric grid polygons (25%, 50%, 75%, 100%)
                listOf(0.25f, 0.50f, 0.75f, 1.0f).forEach { level ->
                    val path = Path()
                    for (i in 0 until count) {
                        val angle = (-PI / 2.0 + i * (2 * PI / count)).toFloat()
                        val pt = Offset(
                            center.x + radius * level * cos(angle),
                            center.y + radius * level * sin(angle)
                        )
                        if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                    }
                    path.close()
                    drawPath(
                        path = path,
                        color = if (level == 0.75f) targetColor.copy(alpha = 0.48f) else outlineColor.copy(alpha = 0.55f),
                        style = Stroke(
                            width = if (level == 0.75f) 1.5.dp.toPx() else 1.dp.toPx(),
                            pathEffect = if (level == 0.75f) PathEffect.dashPathEffect(floatArrayOf(8f, 6f)) else null
                        )
                    )
                }

                // Axes
                for (i in 0 until count) {
                    val angle = (-PI / 2.0 + i * (2 * PI / count)).toFloat()
                    drawLine(
                        color = outlineColor.copy(alpha = 0.5f),
                        start = center,
                        end = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle)),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Mastery polygon
                val dataPath = Path()
                val points = mutableListOf<Offset>()
                ordered.forEachIndexed { i, snap ->
                    val ratio = ((snap?.mastery ?: 50f) / 100f).coerceIn(0.12f, 1f)
                    val angle = (-PI / 2.0 + i * (2 * PI / count)).toFloat()
                    val pt = Offset(
                        center.x + radius * ratio * cos(angle),
                        center.y + radius * ratio * sin(angle)
                    )
                    points.add(pt)
                    if (i == 0) dataPath.moveTo(pt.x, pt.y) else dataPath.lineTo(pt.x, pt.y)
                }
                dataPath.close()

                drawPath(
                    path = dataPath,
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.36f), secondaryColor.copy(alpha = 0.14f)),
                        center = center,
                        radius = radius
                    )
                )
                drawPath(
                    path = dataPath,
                    color = primaryColor,
                    style = Stroke(width = 2.5.dp.toPx())
                )

                points.forEachIndexed { idx, pt ->
                    val color = pillarAccentColor(C1Pillar.entries[idx])
                    drawCircle(color = dotHalo, radius = 5.5.dp.toPx(), center = pt)
                    drawCircle(color = color, radius = 3.8.dp.toPx(), center = pt)
                }
            }

            // Perimeter Labels
            val topSnap = ordered[0]
            val topRightSnap = ordered[1]
            val bottomRightSnap = ordered[2]
            val bottomSnap = ordered[3]
            val bottomLeftSnap = ordered[4]
            val topLeftSnap = ordered[5]

            RadarLabelBadge(topSnap, Alignment.TopCenter, Modifier.align(Alignment.TopCenter))
            RadarLabelBadge(topRightSnap, Alignment.TopEnd, Modifier.align(Alignment.TopEnd).padding(top = 34.dp))
            RadarLabelBadge(bottomRightSnap, Alignment.BottomEnd, Modifier.align(Alignment.BottomEnd).padding(bottom = 34.dp))
            RadarLabelBadge(bottomSnap, Alignment.BottomCenter, Modifier.align(Alignment.BottomCenter))
            RadarLabelBadge(bottomLeftSnap, Alignment.BottomStart, Modifier.align(Alignment.BottomStart).padding(bottom = 34.dp))
            RadarLabelBadge(topLeftSnap, Alignment.TopStart, Modifier.align(Alignment.TopStart).padding(top = 34.dp))
        }
    }
}

@Composable
private fun RadarLabelBadge(
    snapshot: PillarDiagnosticSnapshot?,
    alignment: Alignment,
    modifier: Modifier = Modifier
) {
    if (snapshot == null) return
    val color = pillarAccentColor(snapshot.pillar)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "${snapshot.pillar.shortLabel} ${snapshot.mastery.roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun VisualConceptInfographic(
    diagramType: VisualDiagramType,
    formulaBefore: String,
    formulaAfter: String,
    highlightRule: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = palette.cardSurface,
        border = BorderStroke(1.2.dp, palette.borderStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (diagramType) {
                VisualDiagramType.NOMINAL_TRANSFORM_FLOW -> NominalTransformDiagram(formulaBefore, formulaAfter)
                VisualDiagramType.SENTENCE_BRACKET_ARCH -> SentenceBracketArchDiagram(highlightRule)
                VisualDiagramType.KONJUNKTIV_SPECTRUM -> KonjunktivSpectrumDiagram()
                VisualDiagramType.PARTICIPLE_UNPACK_TREE -> ParticipleUnpackDiagram(formulaBefore, formulaAfter)
                VisualDiagramType.MODAL_PARTICLE_MATRIX -> ModalParticleMatrixDiagram()
                VisualDiagramType.ARGUMENT_PYRAMID -> ArgumentPyramidDiagram()
                VisualDiagramType.COLLOCATION_NETWORK -> CollocationNetworkDiagram()
                VisualDiagramType.TEKAMOLO_PIPELINE -> TekamoloPipelineDiagram()
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.primaryAccent.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, palette.primaryAccent.copy(alpha = 0.35f))
            ) {
                Text(
                    text = highlightRule,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.primaryAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@Composable
private fun NominalTransformDiagram(before: String, after: String) {
    val palette = LocalVibePalette.current
    val inf = rememberInfiniteTransition(label = "nomFlow")
    val pulseY by inf.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Restart),
        label = "nomDot"
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // B2 Verbal Block
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.warningRed.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("B2 VERBALSATZ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.warningRed)
                Text(before, style = MaterialTheme.typography.bodyMedium, color = palette.textPrimary)
            }
        }

        // Compression Arrow Graphic with travelling pulse
        Box(modifier = Modifier.fillMaxWidth().height(34.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val midX = size.width / 2f
                drawLine(
                    color = palette.secondaryAccent.copy(alpha = 0.45f),
                    start = Offset(midX, 0f),
                    end = Offset(midX, size.height - 6.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = palette.primaryAccent,
                    radius = 4.5.dp.toPx(),
                    center = Offset(midX, size.height * pulseY)
                )
            }
            Surface(
                shape = RoundedCornerShape(99.dp),
                color = palette.secondaryAccent,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    "NOMINAL-KOMPRESSION + GENITIV",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = contentColorOnAccent(palette.secondaryAccent),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // C1 Nominal Block
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.tertiaryAccent.copy(alpha = 0.12f),
            border = BorderStroke(1.2.dp, palette.tertiaryAccent.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("C1 NOMINALSTIL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.tertiaryAccent)
                Text(
                    after,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = palette.textPrimary
                )
            }
        }
    }
}

@Composable
private fun SentenceBracketArchDiagram(rule: String) {
    val palette = LocalVibePalette.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(64.dp)) {
            val leftX = size.width * 0.24f
            val rightX = size.width * 0.78f
            val baseY = size.height * 0.85f
            val archPath = Path().apply {
                moveTo(leftX, baseY)
                cubicTo(leftX, 8.dp.toPx(), rightX, 8.dp.toPx(), rightX, baseY)
            }
            drawPath(
                path = archPath,
                brush = Brush.horizontalGradient(listOf(palette.secondaryAccent, palette.tertiaryAccent)),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(palette.secondaryAccent, radius = 6.dp.toPx(), center = Offset(leftX, baseY))
            drawCircle(palette.tertiaryAccent, radius = 6.dp.toPx(), center = Offset(rightX, baseY))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BracketNode("Vorfeld", palette.goldAccent, Modifier.weight(1f))
            BracketNode("Verb II", palette.secondaryAccent, Modifier.weight(1f))
            BracketNode("Mittelfeld", palette.primaryAccent, Modifier.weight(1.3f))
            BracketNode("Ende", palette.tertiaryAccent, Modifier.weight(1f))
        }
    }
}

@Composable
private fun BracketNode(label: String, color: Color, modifier: Modifier = Modifier) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (palette.isDark) color else palette.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        )
    }
}

@Composable
private fun KonjunktivSpectrumDiagram() {
    val palette = LocalVibePalette.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpectrumZone("INDIKATIV", "Fakt / Realität", palette.tertiaryAccent, Modifier.weight(1f))
            SpectrumZone("KONJUNKTIV I", "Neutrale Distanz", palette.secondaryAccent, Modifier.weight(1f))
            SpectrumZone("KONJUNKTIV II", "Irrealis / Ersatz", palette.primaryAccent, Modifier.weight(1f))
        }
        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(palette.tertiaryAccent, palette.secondaryAccent, palette.primaryAccent)),
                start = Offset(12.dp.toPx(), size.height / 2f),
                end = Offset(size.width - 12.dp.toPx(), size.height / 2f),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(palette.cardSurface, radius = 6.dp.toPx(), center = Offset(size.width * 0.5f, size.height / 2f))
            drawCircle(palette.secondaryAccent, radius = 4.dp.toPx(), center = Offset(size.width * 0.5f, size.height / 2f))
        }
    }
}

@Composable
private fun SpectrumZone(title: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color, textAlign = TextAlign.Center)
            Spacer(Modifier.height(2.dp))
            Text(sub, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun ParticipleUnpackDiagram(before: String, after: String) {
    val palette = LocalVibePalette.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BracketNode("Artikel", palette.secondaryAccent, Modifier.weight(0.8f))
            BracketNode("[ Erweiterung + Partizip ]", palette.goldAccent, Modifier.weight(1.8f))
            BracketNode("Nomen", palette.tertiaryAccent, Modifier.weight(0.9f))
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    before,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Transforms to",
                    tint = palette.secondaryAccent,
                    modifier = Modifier.padding(horizontal = 6.dp).size(16.dp)
                )
                Text(
                    after,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ModalParticleMatrixDiagram() {
    val palette = LocalVibePalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.elevatedSurface)
            .border(1.dp, palette.borderStroke, RoundedCornerShape(14.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = palette.borderStroke,
                start = Offset(size.width / 2f, 8.dp.toPx()),
                end = Offset(size.width / 2f, size.height - 8.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = palette.borderStroke,
                start = Offset(8.dp.toPx(), size.height / 2f),
                end = Offset(size.width - 8.dp.toPx(), size.height / 2f),
                strokeWidth = 1.5.dp.toPx()
            )
        }
        MatrixNode("ja (Bekanntes)", palette.tertiaryAccent, Modifier.align(Alignment.TopStart).padding(12.dp))
        MatrixNode("doch (Appell)", palette.secondaryAccent, Modifier.align(Alignment.TopEnd).padding(12.dp))
        MatrixNode("halt / eben (Fakt)", palette.goldAccent, Modifier.align(Alignment.BottomStart).padding(12.dp))
        MatrixNode("etwa (Empörung)", palette.warningRed, Modifier.align(Alignment.BottomEnd).padding(12.dp))
    }
}

@Composable
private fun MatrixNode(label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(99.dp),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ArgumentPyramidDiagram() {
    val palette = LocalVibePalette.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        PyramidTier("4 · Synthese: Abwägend lässt sich festhalten...", palette.tertiaryAccent, 0.64f)
        PyramidTier("3 · Replik: Dem ist jedoch entgegenzuhalten...", palette.secondaryAccent, 0.78f)
        PyramidTier("2 · Konzession: Zwar lässt sich anführen...", palette.goldAccent, 0.90f)
        PyramidTier("1 · These & Datenbasis: Aus der Grafik geht hervor...", palette.primaryAccent, 1.0f)
    }
}

@Composable
private fun PyramidTier(label: String, color: Color, widthFraction: Float) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = Modifier.fillMaxWidth(widthFraction),
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (palette.isDark) color else palette.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun CollocationNetworkDiagram() {
    val palette = LocalVibePalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.goldAccent.copy(alpha = 0.14f),
            border = BorderStroke(1.5.dp, palette.goldAccent),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NOMEN-KERN", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.goldAccent)
                Text("in Kraft / Kritik", style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
            }
        }
        Column(
            modifier = Modifier.weight(1.25f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            BracketNode("treten (Beginn)", palette.tertiaryAccent, Modifier.fillMaxWidth())
            BracketNode("setzen (Kausativ)", palette.secondaryAccent, Modifier.fillMaxWidth())
            BracketNode("üben an (Aktiv)", palette.primaryAccent, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun TekamoloPipelineDiagram() {
    val palette = LocalVibePalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        BracketNode("TE · Wann", palette.secondaryAccent, Modifier.weight(1f))
        BracketNode("KA · Warum", palette.warningRed, Modifier.weight(1f))
        BracketNode("MO · Wie", palette.goldAccent, Modifier.weight(1f))
        BracketNode("LO · Wo", palette.tertiaryAccent, Modifier.weight(1f))
    }
}

@Composable
fun ParetoRoiVisualChart(
    snapshots: List<PillarDiagnosticSnapshot>,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val maxRoi = (snapshots.maxOfOrNull { it.roiScore } ?: 1f).coerceAtLeast(1f)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        snapshots.forEachIndexed { idx, snap ->
            val fraction = (snap.roiScore / maxRoi).coerceIn(0.12f, 1f)
            val barColor = if (snap.isParetoVital20) palette.primaryAccent else palette.secondaryAccent.copy(alpha = 0.55f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${idx + 1}. ${snap.pillar.shortLabel}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (snap.isParetoVital20) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (snap.isParetoVital20) palette.primaryAccent else palette.textPrimary,
                    modifier = Modifier.width(88.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(palette.elevatedSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(14.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (snap.isParetoVital20) {
                                    Brush.horizontalGradient(listOf(palette.primaryAccent, palette.goldAccent))
                                } else {
                                    Brush.horizontalGradient(listOf(barColor, barColor))
                                }
                            )
                    )
                }
                Text(
                    text = "L${snap.targetDifficultyTier} · ${snap.roiSharePercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (snap.isParetoVital20) palette.primaryAccent else palette.textSecondary,
                    modifier = Modifier.width(56.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

