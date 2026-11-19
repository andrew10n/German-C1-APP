package com.example.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.SpeakerPersona
import com.example.domain.model.SpeechReductionPair
import com.example.domain.model.VisualSceneType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Dual Readiness Split Card:
 * Explicitly separates Functional Real-Life C1 (Listening, Spontaneous Speaking, Pragmatics)
 * from Goethe-Zertifikat C1 Examination Readiness.
 */
@Composable
fun DualReadinessSplitCard(
    functionalPercent: Int,
    examPercent: Int,
    onOpenPersonalizedLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animFunc by animateFloatAsState(
        targetValue = (functionalPercent.coerceIn(5, 100) / 100f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "funcArc"
    )
    val animExam by animateFloatAsState(
        targetValue = (examPercent.coerceIn(5, 100) / 100f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "examArc"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onOpenPersonalizedLab() },
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, EmeraldSage.copy(alpha = 0.55f))
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GETRENNTE C1-DIAGNOSE · ESSLINGEN / MB / SV 1845",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldSage
                    )
                    Text(
                        text = "Alltags- & Fach-C1 vs. Goethe-Prüfung",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldSage
                ) {
                    Text(
                        text = "Visual-Lab →",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniAnimatedGauge(
                    progress = animFunc,
                    percent = functionalPercent,
                    label = "Funktionales C1",
                    sublabel = "Hören · Spontan · Technik · Platz",
                    accent = EmeraldSage,
                    icon = { Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = EmeraldSage, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )
                MiniAnimatedGauge(
                    progress = animExam,
                    percent = examPercent,
                    label = "Goethe-Prüfung C1",
                    sublabel = "Nominalstil · Aufsatz · Struktur",
                    accent = CobaltBright,
                    icon = { Icon(Icons.Rounded.School, contentDescription = null, tint = CobaltBright, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniAnimatedGauge(
    progress: Float,
    percent: Int,
    label: String,
    sublabel: String,
    accent: Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sw = 4.5.dp.toPx()
                    val pad = sw / 2f + 2.dp.toPx()
                    val sz = Size(size.width - pad * 2, size.height - pad * 2)
                    drawArc(
                        color = accent.copy(alpha = 0.18f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = sz,
                        style = Stroke(width = sw, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = accent,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = sz,
                        style = Stroke(width = sw, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    icon()
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * High-Craft Animated Imprint/Headway Motion Graphic Canvas for each of the 6 Visual Scene Types.
 */
@Composable
fun AnimatedVisualSceneHero(
    sceneType: VisualSceneType,
    isPlayingAudio: Boolean,
    speakers: List<SpeakerPersona>,
    difficulty: String,
    subRegister: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val infinite = rememberInfiniteTransition(label = "sceneMotion")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlayingAudio) 1400 else 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlayingAudio) 550 else 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.dp, palette.primaryAccent.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = palette.primaryAccent.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "$difficulty · ${sceneBadgeTitle(sceneType)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.primaryAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "${speakers.size} Sprecher",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.imprintStageSubtext
                )
            }

            // Custom Animated Canvas Scene
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.04f))
            ) {
                val AmberBright = palette.primaryAccent
                val w = size.width
                val h = size.height
                when (sceneType) {
                    VisualSceneType.WAVEFORM_RADAR -> {
                        // Pulsing speaker radar + 20 live equalizer bars
                        val leftCenter = Offset(w * 0.22f, h * 0.5f)
                        drawCircle(
                            color = EmeraldSage.copy(alpha = 0.18f),
                            radius = 28.dp.toPx() * pulse,
                            center = leftCenter
                        )
                        drawCircle(
                            color = EmeraldSage,
                            radius = 14.dp.toPx(),
                            center = leftCenter
                        )
                        val barCount = 18
                        val startX = w * 0.40f
                        val usableW = w * 0.54f
                        val step = usableW / barCount
                        for (i in 0 until barCount) {
                            val wave = sin((phase * 2f * PI + i * 0.55f)).toFloat()
                            val amp = if (isPlayingAudio) (0.35f + 0.55f * ((wave + 1f) / 2f)) else (0.18f + 0.22f * ((wave + 1f) / 2f))
                            val barH = h * 0.72f * amp
                            val x = startX + i * step
                            drawLine(
                                color = if (i % 3 == 0) AmberBright else CobaltBright,
                                start = Offset(x, (h - barH) / 2f),
                                end = Offset(x, (h + barH) / 2f),
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    VisualSceneType.MULTI_SPEAKER_TABLE -> {
                        // 3-Speaker spatial triangle with animated turn-taking signal arc
                        val nodes = listOf(
                            Offset(w * 0.20f, h * 0.68f),
                            Offset(w * 0.50f, h * 0.24f),
                            Offset(w * 0.80f, h * 0.68f)
                        )
                        val colors = listOf(AmberBright, EmeraldSage, CobaltBright)
                        for (i in nodes.indices) {
                            val next = nodes[(i + 1) % nodes.size]
                            val p = Path().apply {
                                moveTo(nodes[i].x, nodes[i].y)
                                quadraticTo(w * 0.5f, h * 0.5f, next.x, next.y)
                            }
                            drawPath(
                                path = p,
                                color = Color.White.copy(alpha = 0.22f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        val activeIdx = ((phase * 3f).toInt()) % 3
                        nodes.forEachIndexed { idx, pt ->
                            if (idx == activeIdx) {
                                drawCircle(
                                    color = colors[idx].copy(alpha = 0.28f),
                                    radius = 24.dp.toPx() * pulse,
                                    center = pt
                                )
                            }
                            drawCircle(color = colors[idx], radius = 12.dp.toPx(), center = pt)
                        }
                    }

                    VisualSceneType.RECEIPT_REGISTER_MORPH -> {
                        // Left amber node ("Verständlich") morphing into 3 right register targets
                        val src = Offset(w * 0.18f, h * 0.5f)
                        val targets = listOf(
                            Offset(w * 0.82f, h * 0.22f) to EmeraldSage,
                            Offset(w * 0.82f, h * 0.50f) to CobaltBright,
                            Offset(w * 0.82f, h * 0.78f) to AmberBright
                        )
                        targets.forEachIndexed { idx, (dst, col) ->
                            val path = Path().apply {
                                moveTo(src.x, src.y)
                                cubicTo(w * 0.48f, src.y, w * 0.52f, dst.y, dst.x, dst.y)
                            }
                            drawPath(path = path, color = col.copy(alpha = 0.45f), style = Stroke(width = 3.dp.toPx()))
                            val t = (phase + idx * 0.33f) % 1f
                            val dotX = src.x + (dst.x - src.x) * t
                            val dotY = src.y + (dst.y - src.y) * (t * t * (3 - 2 * t))
                            drawCircle(color = col, radius = 5.dp.toPx(), center = Offset(dotX, dotY))
                            drawRoundRect(
                                color = col,
                                topLeft = Offset(dst.x - 18.dp.toPx(), dst.y - 9.dp.toPx()),
                                size = Size(36.dp.toPx(), 18.dp.toPx()),
                                cornerRadius = CornerRadius(6.dp.toPx())
                            )
                        }
                        drawCircle(color = AmberGold.copy(alpha = 0.26f), radius = 22.dp.toPx() * pulse, center = src)
                        drawCircle(color = AmberGold, radius = 13.dp.toPx(), center = src)
                    }

                    VisualSceneType.POWERTRAIN_SIGNAL_FLOW -> {
                        // ECU -> K0 -> EM -> TCU signal pipeline + live torque overshoot oscilloscope
                        drawLine(
                            color = EmeraldSage.copy(alpha = 0.35f),
                            start = Offset(w * 0.08f, h * 0.62f),
                            end = Offset(w * 0.92f, h * 0.62f),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        val trace = Path()
                        val samples = 60
                        for (s in 0..samples) {
                            val u = s / samples.toFloat()
                            val x = w * (0.08f + 0.84f * u)
                            val spike = if (u in 0.38f..0.56f) {
                                sin(((u - 0.38f) / 0.18f) * PI).toFloat() * h * 0.36f
                            } else {
                                sin((u * 8f + phase * 2f * PI)).toFloat() * h * 0.03f
                            }
                            val y = h * 0.62f - spike
                            if (s == 0) trace.moveTo(x, y) else trace.lineTo(x, y)
                        }
                        drawPath(
                            path = trace,
                            brush = Brush.horizontalGradient(listOf(CobaltBright, CoralCrimson, EmeraldSage)),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        val scanX = w * (0.08f + 0.84f * phase)
                        drawLine(
                            color = AmberBright.copy(alpha = 0.7f),
                            start = Offset(scanX, h * 0.14f),
                            end = Offset(scanX, h * 0.86f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    VisualSceneType.FUNINO_PITCH_SPLIT -> {
                        // Green FUNiño 4-mini-goals pitch + yellow 6m Schusszonen + moving players
                        val padX = w * 0.08f
                        val padY = h * 0.12f
                        val pw = w - padX * 2
                        val ph = h - padY * 2
                        drawRoundRect(
                            color = EmeraldSage.copy(alpha = 0.22f),
                            topLeft = Offset(padX, padY),
                            size = Size(pw, ph),
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.5f),
                            topLeft = Offset(padX, padY),
                            size = Size(pw, ph),
                            cornerRadius = CornerRadius(10.dp.toPx()),
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                        // Yellow Schusszone lines
                        val leftZoneX = padX + pw * 0.22f
                        val rightZoneX = padX + pw * 0.78f
                        drawLine(AmberBright, Offset(leftZoneX, padY), Offset(leftZoneX, padY + ph), strokeWidth = 2.dp.toPx())
                        drawLine(AmberBright, Offset(rightZoneX, padY), Offset(rightZoneX, padY + ph), strokeWidth = 2.dp.toPx())
                        // 4 Mini-goals
                        val goalOffsets = listOf(
                            Offset(padX - 4.dp.toPx(), padY + ph * 0.22f),
                            Offset(padX - 4.dp.toPx(), padY + ph * 0.68f),
                            Offset(padX + pw - 2.dp.toPx(), padY + ph * 0.22f),
                            Offset(padX + pw - 2.dp.toPx(), padY + ph * 0.68f)
                        )
                        goalOffsets.forEach { g ->
                            drawRect(Color.White, topLeft = g, size = Size(6.dp.toPx(), ph * 0.14f))
                        }
                        // Animated ball carrier scanning open goal
                        val carrier = Offset(padX + pw * 0.52f, padY + ph * 0.55f)
                        val openGoal = Offset(padX + pw, padY + ph * (if (phase < 0.5f) 0.28f else 0.74f))
                        drawLine(
                            color = AmberBright.copy(alpha = 0.75f),
                            start = carrier,
                            end = openGoal,
                            strokeWidth = 2.5.dp.toPx()
                        )
                        drawCircle(color = CobaltBright, radius = 8.dp.toPx() * pulse, center = carrier)
                    }

                    VisualSceneType.PARTICLE_TONE_DIAL -> {
                        // Semicircular pragmatic tone meter
                        val arcRectSize = Size(w * 0.56f, h * 1.1f)
                        val arcTopLeft = Offset((w - arcRectSize.width) / 2f, h * 0.22f)
                        drawArc(
                            brush = Brush.horizontalGradient(listOf(CoralCrimson, AmberBright, EmeraldSage)),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcRectSize,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        val angleRad = PI - (0.68f + 0.16f * sin(phase * 2 * PI)) * PI
                        val cx = w * 0.5f
                        val cy = h * 0.78f
                        val needleLen = arcRectSize.width * 0.42f
                        val nx = cx + (cos(angleRad) * needleLen).toFloat()
                        val ny = cy - (sin(angleRad) * needleLen).toFloat()
                        drawLine(Color.White, Offset(cx, cy), Offset(nx, ny), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                        drawCircle(AmberBright, radius = 6.dp.toPx(), center = Offset(cx, cy))
                    }
                }
            }

            // Speaker Persona Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                speakers.forEach { sp ->
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = Color.White.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSage)
                            )
                            Text(
                                text = "${sp.name} (${sp.role} · ${sp.variety})",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Text(
                text = subRegister,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.78f)
            )
        }
    }
}

private fun sceneBadgeTitle(type: VisualSceneType): String = when (type) {
    VisualSceneType.WAVEFORM_RADAR -> "AKUSTIK-RADAR"
    VisualSceneType.RECEIPT_REGISTER_MORPH -> "SITUATIONS-MORPH"
    VisualSceneType.POWERTRAIN_SIGNAL_FLOW -> "POWERTRAIN-TRACE"
    VisualSceneType.FUNINO_PITCH_SPLIT -> "FUNIÑO & PLATZ-SPLIT"
    VisualSceneType.PARTICLE_TONE_DIAL -> "PARTIKEL-TONWAAGE"
    VisualSceneType.MULTI_SPEAKER_TABLE -> "3ER-GESPRÄCHSRADAR"
}

/**
 * Interactive Tap-to-Morph Strip for Connected Speech & Swabian Reductions.
 */
@Composable
fun InteractiveSpeechMorphStrip(
    reductions: List<SpeechReductionPair>,
    modifier: Modifier = Modifier
) {
    if (reductions.isEmpty()) return
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "VERSCHLEIFUNGS-RÖNTGEN · ANTIPPEN ZUM ENTSCHLÜSSELN",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CobaltBright
        )
        reductions.forEach { pair ->
            var morphed by remember(pair.spoken) { mutableStateOf(false) }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { morphed = !morphed },
                shape = RoundedCornerShape(14.dp),
                color = if (morphed) EmeraldSage.copy(alpha = 0.12f) else AmberGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, if (morphed) EmeraldSage else AmberGold)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Rounded.SwapHoriz,
                        contentDescription = "Morph",
                        tint = if (morphed) EmeraldSage else AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    AnimatedContent(
                        targetState = morphed,
                        modifier = Modifier.weight(1f),
                        label = "speechMorph"
                    ) { isStandard ->
                        Column {
                            Text(
                                text = if (isStandard) "Klartext: „${pair.standard}“" else "Gehört im Strom: „${pair.spoken}“",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = pair.note,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
