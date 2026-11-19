package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.TextSnippet
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.ContextTrack
import com.example.domain.model.RegisterAlternative
import com.example.domain.model.TranscriptTurn
import com.example.domain.model.UnderstandableBaseline
import com.example.domain.model.VisualSceneType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CobaltBright
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.EmeraldSage
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.sin

fun visualIllustrationResFor(
    sceneType: VisualSceneType? = null,
    familyId: String? = null,
    track: ContextTrack? = null
): Int {
    if (familyId != null) {
        return when {
            familyId.contains("kasse") || familyId.contains("alltag") -> R.drawable.img_visual_kasse_alltag
            familyId.contains("swabian") || familyId.contains("colleagues") -> R.drawable.img_visual_swabia_roundtable
            familyId.contains("powertrain") -> R.drawable.img_amg_911_cockpit
            familyId.contains("fmea") || familyId.contains("eng") -> R.drawable.img_visual_powertrain_lab
            familyId.contains("cotrainer") -> R.drawable.img_bvb_yellow_pitch
            familyId.contains("fjugend") || familyId.contains("coach") -> R.drawable.img_visual_funino_pitch
            else -> R.drawable.img_visual_kasse_alltag
        }
    }
    if (sceneType != null) {
        return when (sceneType) {
            VisualSceneType.RECEIPT_REGISTER_MORPH -> R.drawable.img_visual_kasse_alltag
            VisualSceneType.MULTI_SPEAKER_TABLE, VisualSceneType.PARTICLE_TONE_DIAL, VisualSceneType.WAVEFORM_RADAR ->
                R.drawable.img_visual_swabia_roundtable
            VisualSceneType.POWERTRAIN_SIGNAL_FLOW -> R.drawable.img_amg_911_cockpit
            VisualSceneType.FUNINO_PITCH_SPLIT -> R.drawable.img_bvb_yellow_pitch
        }
    }
    return when (track) {
        ContextTrack.ENGINEERING -> R.drawable.img_amg_911_cockpit
        ContextTrack.COACHING -> R.drawable.img_bvb_yellow_pitch
        ContextTrack.SOCIAL -> R.drawable.img_visual_swabia_roundtable
        else -> R.drawable.img_visual_kasse_alltag
    }
}

/**
 * Replaces prose scenario introductions with an Imprint/Headway-style Illustrated Scene Card +
 * compact visual HUD chips + animated audio trigger + optional text peek.
 */
@Composable
fun VisualSituationStoryboardBanner(
    illustrationRes: Int,
    sceneBadge: String,
    registerChip: String,
    goalSummary: String,
    fullSceneNote: String,
    isPlayingAudio: Boolean,
    fastRateLabel: String,
    ttsDisclosureShort: String,
    onTogglePlay: () -> Unit,
    onToggleSpeed: () -> Unit,
    modifier: Modifier = Modifier,
    isLoadingAudio: Boolean = false
) {
    var showDetailText by remember(fullSceneNote) { mutableStateOf(false) }
    val palette = LocalVibePalette.current
    val inf = rememberInfiniteTransition(label = "audioPulse")
    val wavePhase by inf.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Restart),
        label = "wave"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(158.dp)
            ) {
                Image(
                    painter = painterResource(id = illustrationRes),
                    contentDescription = sceneBadge,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    palette.imprintStageBg.copy(alpha = 0.18f),
                                    palette.imprintStageBg.copy(alpha = 0.65f),
                                    palette.imprintStageBg.copy(alpha = 0.96f)
                                )
                            )
                        )
                )

                // Top Visual HUD Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = palette.primaryAccent
                    ) {
                        Text(
                            text = sceneBadge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = contentColorOnAccent(palette.primaryAccent),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .clickable { showDetailText = !showDetailText },
                        shape = RoundedCornerShape(99.dp),
                        color = palette.imprintStageBg.copy(alpha = 0.78f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Rounded.TextSnippet,
                                contentDescription = "Szene lesen",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (showDetailText) "Kompakt" else "Kontext",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }

                // Bottom Visual Mission Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = registerChip.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent
                    )
                    Text(
                        text = goalSummary,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.imprintStageText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Interactive Visual Waveform Audio Deck
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val playBg = if (isPlayingAudio) palette.warningRed else palette.primaryAccent
                    val playFg = contentColorOnAccent(playBg)
                    Surface(
                        modifier = Modifier
                            .weight(1.45f)
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onTogglePlay() },
                        shape = RoundedCornerShape(14.dp),
                        color = playBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isLoadingAudio) {
                                CircularProgressIndicator(
                                    color = playFg,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    if (isPlayingAudio) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "Play Audio",
                                    tint = playFg,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Canvas(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                            ) {
                                val bars = 16
                                val gap = size.width / bars
                                for (i in 0 until bars) {
                                    val amp = if (isPlayingAudio) {
                                        (0.32f + 0.65f * kotlin.math.abs(sin(wavePhase + i * 0.55f)))
                                    } else {
                                        0.25f + (i % 3) * 0.14f
                                    }
                                    val bh = size.height * amp
                                    drawRoundRect(
                                        color = playFg.copy(alpha = 0.9f),
                                        topLeft = Offset(i * gap + 2f, (size.height - bh) / 2f),
                                        size = Size((gap - 4f).coerceAtLeast(3f), bh),
                                        cornerRadius = CornerRadius(4f, 4f)
                                    )
                                }
                            }
                            Text(
                                text = when {
                                    isLoadingAudio -> "Lädt..."
                                    isPlayingAudio -> "Stopp"
                                    else -> "Blind-Hören"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = playFg
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onToggleSpeed() },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.6f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = fastRateLabel,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = palette.secondaryAccent
                            )
                        }
                    }
                }

                Text(
                    text = "ⓘ $ttsDisclosureShort",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AnimatedVisibility(visible = showDetailText) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = fullSceneNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Replaces flat transcript text walls with an alternating Visual Comic Speech-Bubble Stage
 * featuring speaker avatars, tone badges, and 1-tap audio playback per bubble.
 */
@Composable
fun VisualComicDialogueStage(
    turns: List<TranscriptTurn>,
    onSpeakLine: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = listOf(CobaltBright, AmberGold, EmeraldSage, CoralCrimson)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        turns.forEachIndexed { idx, turn ->
            val alignRight = idx % 2 == 1
            val color = palette[idx % palette.size]
            val initials = turn.speaker.trim().take(2).uppercase()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (alignRight) Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                if (!alignRight) {
                    SpeakerAvatarBadge(initials = initials, accent = color)
                    Spacer(Modifier.width(8.dp))
                }

                Surface(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = if (alignRight) 18.dp else 4.dp,
                                bottomEnd = if (alignRight) 4.dp else 18.dp
                            )
                        )
                        .clickable { onSpeakLine(turn.line) },
                    color = color.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = turn.speaker,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = color
                            )
                            if (turn.cue.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = color.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = turn.cue,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(2.dp))
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "Anhören",
                                tint = color,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "„${turn.line}“",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (alignRight) {
                    Spacer(Modifier.width(8.dp))
                    SpeakerAvatarBadge(initials = initials, accent = color)
                }
            }
        }
    }
}

@Composable
private fun SpeakerAvatarBadge(initials: String, accent: Color) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = accent.copy(alpha = 0.2f),
        border = BorderStroke(1.5.dp, accent)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = accent
            )
        }
    }
}

/**
 * Replaces 4 stacked text cards (Understandable Baseline + 3 Register Alternatives)
 * with a single interactive Register Spectrum Dial + Word-Chip Spotlight!
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveRegisterSpectrumDial(
    baseline: UnderstandableBaseline,
    alternatives: List<RegisterAlternative>,
    onSpeakPhrase: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStop by remember(baseline.userPhrase) { mutableIntStateOf(1.coerceAtMost(alternatives.size)) }
    val stopsCount = 1 + alternatives.size
    val progress by animateFloatAsState(
        targetValue = (selectedStop.toFloat() / stopsCount.minus(1).coerceAtLeast(1)),
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "registerDial"
    )

    val signalTokens = remember {
        setOf(
            "doch", "halt", "eben", "mal", "eigentlich", "wohl", "fei", "gell", "kurz",
            "könnten", "dürfte", "würde", "vielleicht", "glaub", "glaube", "infolge",
            "zwecks", "vororientieren", "gegenpressing", "ftti", "rampe", "kennfeld"
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, EmeraldSage.copy(alpha = 0.55f))
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
                    Icon(Icons.Rounded.Tune, contentDescription = null, tint = EmeraldSage, modifier = Modifier.size(18.dp))
                    Text(
                        text = "REGISTER-REGLER · BASIS → C1 NATÜRLICH",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldSage
                    )
                }
                Text(
                    text = "Stufe ${selectedStop + 1}/$stopsCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberGold
                )
            }

            val palette = LocalVibePalette.current
            // Visual Gradient Dial Track
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            ) {
                val y = size.height / 2f
                drawLine(
                    brush = Brush.horizontalGradient(listOf(palette.goldAccent, palette.secondaryAccent, palette.primaryAccent)),
                    start = Offset(14f, y),
                    end = Offset(size.width - 14f, y),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
                val knobX = 14f + (size.width - 28f) * progress
                drawCircle(color = palette.imprintStageBg, radius = 11f, center = Offset(knobX, y))
                drawCircle(color = palette.primaryAccent, radius = 7f, center = Offset(knobX, y))
            }

            // Interactive Stop Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val labels = listOf("Basis ✓") + alternatives.map { it.register }
                labels.forEachIndexed { idx, label ->
                    val active = selectedStop == idx
                    val chipColor = if (idx == 0) palette.goldAccent else palette.primaryAccent
                    Surface(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 40.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .clickable { selectedStop = idx },
                        shape = RoundedCornerShape(99.dp),
                        color = if (active) chipColor else chipColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, chipColor.copy(alpha = 0.55f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (active) contentColorOnAccent(chipColor) else chipColor
                            )
                        }
                    }
                }
            }

            // Animated Spotlight Phrase Card with Highlighted Signal Pills
            AnimatedContent(targetState = selectedStop, label = "registerPhrase") { idx ->
                val isBase = idx == 0
                val phrase = if (isBase) baseline.userPhrase else alternatives.getOrElse(idx - 1) { alternatives.first() }.text
                val subtitle = if (isBase) baseline.verdict else "Antippen zum Anhören · Farbige Wörter = C1-Registeranker"
                val accent = if (isBase) palette.goldAccent else palette.tertiaryAccent

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSpeakPhrase(phrase) },
                    shape = RoundedCornerShape(16.dp),
                    color = palette.imprintStageBg
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            phrase.split(" ").forEach { rawWord ->
                                val clean = rawWord.lowercase().trim(',', '.', '?', '!', '„', '“', '–', ':')
                                val isSignal = !isBase && clean in signalTokens
                                if (isSignal) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = accent.copy(alpha = 0.28f),
                                        border = BorderStroke(1.dp, accent)
                                    ) {
                                        Text(
                                            text = rawWord,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = palette.primaryAccent,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = rawWord,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = palette.imprintStageText
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.78f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Replaces bullet lists of expressions, collocations, and grammar notes with an interactive
 * 3-Block Visual Sentence Blueprint + Modal-Particle Tone Meter!
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualChunkBlueprintBoard(
    expressions: List<String>,
    collocations: List<String>,
    grammarPatterns: List<String>,
    onSpeakChunk: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExprIdx by remember(expressions) { mutableIntStateOf(0) }
    var selectedParticleIdx by remember { mutableIntStateOf(0) }

    val particles = remember {
        listOf(
            Triple("doch mal", Triple(0.88f, 0.42f, 0.75f), "Warm & einladend abmildern"),
            Triple("wohl", Triple(0.62f, 0.35f, 0.90f), "Höfliche Vermutung ohne Vorwurf"),
            Triple("halt / eben", Triple(0.50f, 0.72f, 0.84f), "Pragmatische Akzeptanz im Alltag"),
            Triple("eigentlich", Triple(0.74f, 0.48f, 0.86f), "Sanfter Themenwechsel / Nachfrage"),
            Triple("fei (Schwäb.)", Triple(0.78f, 0.68f, 0.94f), "Süddeutscher Nachdruck: ‚Achtung, wichtig!‘")
        )
    }

    val activeExpr = expressions.getOrElse(selectedExprIdx) { "" }
    val words = activeExpr.split(" ").filter { it.isNotBlank() }
    val third = (words.size / 3).coerceAtLeast(1)
    val slot1 = words.take(third).joinToString(" ")
    val slot2 = words.drop(third).take(third.coerceAtLeast(1)).joinToString(" ")
    val slot3 = words.drop(third * 2).joinToString(" ").ifBlank { "…" }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val palette = LocalVibePalette.current
        // 1. Interactive 3-Slot Sentence Architecture Diagram
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = palette.imprintStageBg,
            border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.5f))
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
                    Text(
                        text = "SATZ-BAUKASTEN · ANTIPPEN ZUM HÖREN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.primaryAccent
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        expressions.forEachIndexed { idx, _ ->
                            Surface(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedExprIdx = idx
                                        onSpeakChunk(expressions[idx])
                                    },
                                shape = CircleShape,
                                color = if (selectedExprIdx == idx) palette.primaryAccent else Color.White.copy(alpha = 0.14f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selectedExprIdx == idx) contentColorOnAccent(palette.primaryAccent) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // 3 Visual Snap Blocks
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { if (activeExpr.isNotBlank()) onSpeakChunk(activeExpr) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    BlueprintBlock(tag = "EINSTIEG", text = slot1, accent = palette.secondaryAccent, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(14.dp))
                    BlueprintBlock(tag = "KERN-ANKER", text = slot2, accent = palette.tertiaryAccent, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(14.dp))
                    BlueprintBlock(tag = "KLAMMER", text = slot3, accent = palette.goldAccent, modifier = Modifier.weight(1f))
                }

                // Collocation Visual Pills (No bullet list!)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    collocations.forEach { col ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .clickable { onSpeakChunk(col.substringBefore("(").trim()) },
                            shape = RoundedCornerShape(99.dp),
                            color = Color.White.copy(alpha = 0.09f),
                            border = BorderStroke(1.dp, EmeraldSage.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = EmeraldSage, modifier = Modifier.size(13.dp))
                                Text(
                                    text = col,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Interactive Modal-Particle Tone Equalizer
        val (pName, meters, pCue) = particles[selectedParticleIdx]
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = palette.cardSurface,
            border = BorderStroke(1.dp, palette.borderStroke)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "PARTIKEL-TONREGLER · KLANGFARBE LIVE TESTEN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.secondaryAccent
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    particles.forEachIndexed { idx, item ->
                        val active = selectedParticleIdx == idx
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .clickable {
                                    selectedParticleIdx = idx
                                    onSpeakChunk(item.first.substringBefore(" "))
                                },
                            shape = RoundedCornerShape(99.dp),
                            color = if (active) palette.primaryAccent else palette.primaryAccent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = item.first,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (active) contentColorOnAccent(palette.primaryAccent) else palette.primaryAccent,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "$pName → $pCue",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = palette.textPrimary
                )

                ToneBarRow("Wärme & Nähe", meters.first, palette.tertiaryAccent)
                ToneBarRow("Direktheit", meters.second, palette.warningRed)
                ToneBarRow("Impliziter Subtext", meters.third, palette.goldAccent)

                if (grammarPatterns.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        grammarPatterns.forEach { gp ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = palette.goldAccent.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "⚡ $gp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textPrimary,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BlueprintBlock(tag: String, text: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, accent)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(tag, style = MaterialTheme.typography.labelSmall, color = accent)
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ToneBarRow(label: String, fraction: Float, color: Color) {
    val anim by animateFloatAsState(targetValue = fraction, animationSpec = tween(300), label = label)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(108.dp)
        )
        LinearProgressIndicator(
            progress = { anim },
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(99.dp))
        )
    }
}

/**
 * Replaces long textbook reading paragraphs with a 3-Node Visual Concept Flow Diagram
 * + optional raw text drawer.
 */
@Composable
fun VisualStoryMapFlow(
    title: String,
    rawText: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    var showRaw by remember(rawText) { mutableStateOf(false) }
    val clauses = remember(rawText) {
        rawText.split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(3)
            .ifEmpty { listOf(rawText) }
    }
    val palette = LocalVibePalette.current
    val nodeLabels = listOf("1 · AUSGANGSLAGE", "2 · WENDEPUNKT", "3 · C1-KERN")
    val nodeColors = listOf(palette.secondaryAccent, palette.goldAccent, palette.tertiaryAccent)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
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
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent
                )
                Text(
                    text = if (showRaw) "Grafik-Ansicht" else "Originaltext",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = palette.secondaryAccent,
                    modifier = Modifier.clickable { showRaw = !showRaw }
                )
            }

            if (showRaw) {
                Text(rawText, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
            } else {
                clauses.forEachIndexed { idx, sentence ->
                    val c = nodeColors[idx % nodeColors.size]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier.size(26.dp),
                                shape = CircleShape,
                                color = c
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = contentColorOnAccent(c)
                                    )
                                }
                            }
                            if (idx < clauses.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(22.dp)
                                        .background(c.copy(alpha = 0.4f))
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = c.copy(alpha = 0.09f),
                            border = BorderStroke(1.dp, c.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(nodeLabels.getOrElse(idx) { "KERN" }, style = MaterialTheme.typography.labelSmall, color = c)
                                Text(
                                    text = sentence,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual 3-Stage Mediation Funnel Diagram + Bird's-Eye Tactical Pitch Canvas for SituationsSimulatorScreen.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualMediationFunnelCard(
    mediationType: String,
    title: String,
    sourceMaterial: String,
    recipientContext: String,
    pitfalls: List<String>,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.secondaryAccent.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SPRACHMITTLUNGS-TRICHTER · ${mediationType.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = palette.primaryAccent
            )
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.imprintStageText)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BlueprintBlock(
                    tag = "QUELLE (FACHTEXT)",
                    text = sourceMaterial,
                    accent = palette.secondaryAccent,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Rounded.FilterAlt, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(20.dp))
                BlueprintBlock(
                    tag = "ZIEL-ADRESSAT",
                    text = recipientContext,
                    accent = palette.tertiaryAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pitfalls.forEach { pit ->
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = palette.warningRed.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, palette.warningRed.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "✕ $pit",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.imprintStageText,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTacticalPitchCanvas(
    matchPhase: String,
    title: String,
    isSolved: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val inf = rememberInfiniteTransition(label = "pitchAnim")
    val pulse by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "pulse"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0A2E1F),
        border = BorderStroke(1.5.dp, palette.tertiaryAccent)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TAKTIK-BOARD · ${matchPhase.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.primaryAccent
                )
                Text(
                    text = if (isSolved) "✓ Passweg offen" else "Lösung wählen",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.tertiaryAccent
                )
            }
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F3D2A))
            ) {
                val w = size.width
                val h = size.height
                val lineStroke = Stroke(width = 2f)
                drawRect(Color.White.copy(alpha = 0.22f), style = lineStroke)
                drawLine(Color.White.copy(alpha = 0.22f), Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = 2f)
                drawCircle(Color.White.copy(alpha = 0.22f), radius = h * 0.26f, center = Offset(w * 0.5f, h * 0.5f), style = lineStroke)
                drawRoundRect(
                    color = palette.primaryAccent.copy(alpha = 0.14f + 0.08f * pulse),
                    topLeft = Offset(w * 0.54f, h * 0.16f),
                    size = Size(w * 0.28f, h * 0.32f),
                    cornerRadius = CornerRadius(10f, 10f)
                )

                val ballCarrier = Offset(w * 0.30f, h * 0.62f)
                val halfSpaceRunner = Offset(w * 0.68f, h * 0.30f)
                val presser = Offset(w * 0.44f + 8f * pulse, h * 0.52f)

                drawLine(palette.warningRed, presser, ballCarrier, strokeWidth = 4f, cap = StrokeCap.Round)
                val passPath = Path().apply {
                    moveTo(ballCarrier.x, ballCarrier.y)
                    quadraticTo(w * 0.48f, h * 0.22f, halfSpaceRunner.x, halfSpaceRunner.y)
                }
                drawPath(
                    path = passPath,
                    color = if (isSolved) palette.tertiaryAccent else palette.primaryAccent.copy(alpha = 0.72f),
                    style = Stroke(width = if (isSolved) 6f else 4f, cap = StrokeCap.Round)
                )

                drawCircle(palette.secondaryAccent, radius = 11f, center = ballCarrier)
                drawCircle(Color.White, radius = 5f, center = Offset(ballCarrier.x + 8f, ballCarrier.y - 4f))
                drawCircle(palette.tertiaryAccent, radius = 11f + 3f * pulse, center = halfSpaceRunner)
                drawCircle(palette.warningRed, radius = 10f, center = presser)
            }
        }
    }
}

/**
 * Compact 5-Segment Visual Memory Stability Battery for SrsVisualTrainerScreen.
 */
@Composable
fun VisualMemoryBatteryBar(stabilityDays: Float, accent: Color, modifier: Modifier = Modifier) {
    val filledSegments = when {
        stabilityDays >= 21f -> 5
        stabilityDays >= 12f -> 4
        stabilityDays >= 6f -> 3
        stabilityDays >= 2f -> 2
        else -> 1
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (i in 1..5) {
            Box(
                modifier = Modifier
                    .width(9.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (i <= filledSegments) accent else accent.copy(alpha = 0.16f))
            )
        }
    }
}
