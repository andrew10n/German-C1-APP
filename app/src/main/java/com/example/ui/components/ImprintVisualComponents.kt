package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Tune
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
import com.example.domain.engine.DailySessionBeat
import com.example.domain.model.GoetheExamModuleAudit
import com.example.ui.theme.AppVibeTheme
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import com.example.ui.theme.paletteForVibe
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Compact visual theme switcher for the C1 learning experience.
 */
@Composable
fun VibeThemeQuickStrip(
    selectedTheme: AppVibeTheme,
    onSelectTheme: (AppVibeTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = palette.elevatedSurface,
            border = BorderStroke(1.dp, palette.borderStroke),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Palette,
                    contentDescription = "Theme Vibe",
                    tint = palette.primaryAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        AppVibeTheme.entries.forEach { theme ->
            val themeSpec = paletteForVibe(theme)
            val isSelected = theme == selectedTheme
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.03f else 0.98f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                label = "vibeScale_${theme.id}"
            )
            val bg by animateColorAsState(
                targetValue = if (isSelected) themeSpec.primaryAccent else palette.cardSurface,
                animationSpec = tween(320),
                label = "vibeBg_${theme.id}"
            )
            Surface(
                modifier = Modifier
                    .scale(scale)
                    .defaultMinSize(minHeight = 36.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .clickable { onSelectTheme(theme) }
                    .testTag("vibe_chip_${theme.id}"),
                shape = RoundedCornerShape(99.dp),
                color = bg,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) themeSpec.primaryAccent else palette.borderStroke
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) contentColorOnAccent(bg) else themeSpec.primaryAccent)
                    )
                    Text(
                        text = theme.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) contentColorOnAccent(bg) else palette.textPrimary
                    )
                }
            }
        }
    }
}

/**
 * Illustrated visual theme cards for the progress and settings view.
 */
@Composable
fun GarageVibeGallerySelector(
    selectedTheme: AppVibeTheme,
    onSelectTheme: (AppVibeTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Rounded.Palette, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(18.dp))
                Text(
                    text = "SIGNATUR-DESIGN · DEIN COCKPIT",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = palette.primaryAccent
                )
            }
            Text(
                text = selectedTheme.badgeTag,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = palette.secondaryAccent
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AppVibeTheme.entries.forEach { theme ->
                val spec = paletteForVibe(theme)
                val active = theme == selectedTheme
                val cardScale by animateFloatAsState(
                    targetValue = if (active) 1f else 0.95f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                    label = "garageCard_${theme.id}"
                )
                Surface(
                    modifier = Modifier
                        .width(156.dp)
                        .scale(cardScale)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectTheme(theme) }
                        .testTag("garage_vibe_${theme.id}"),
                    shape = RoundedCornerShape(18.dp),
                    color = spec.imprintStageBg,
                    border = BorderStroke(
                        width = if (active) 2.2.dp else 1.dp,
                        color = if (active) spec.primaryAccent else palette.borderStroke
                    )
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                        ) {
                            Image(
                                painter = painterResource(id = spec.heroDrawableRes),
                                contentDescription = theme.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, spec.imprintStageBg.copy(alpha = 0.88f))
                                        )
                                    )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = spec.primaryAccent,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = theme.badgeTag,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = contentColorOnAccent(spec.primaryAccent),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (active) spec.primaryAccent else spec.imprintStageText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = theme.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = spec.imprintStageSubtext,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ultra-Clean Minimalist Daily Stint Header:
 * Combines compact expandable Theme Selector, Daily Session Summary, Streak,
 * Engine/Commander pill, and integrated segmented sector lap bars into one sleek card.
 */
@Composable
fun MinimalDailyStintHeader(
    selectedTheme: AppVibeTheme,
    dailyGoalMinutes: Int,
    minutesToday: Int,
    functionalC1Percent: Int,
    streakDays: Int,
    commanderActive: Boolean,
    beats: List<DailySessionBeat>,
    activeBeat: DailySessionBeat?,
    completedBeatIds: Set<String>,
    onSelectTheme: (AppVibeTheme) -> Unit,
    onSelectBeat: (DailySessionBeat) -> Unit,
    onOpenEngineSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val progress = (minutesToday.toFloat() / dailyGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0.06f, 1f)
    val activeIdx = beats.indexOfFirst { it.id == activeBeat?.id }.let { if (it < 0) 0 else it }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.52f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Crossfade(
                targetState = palette.heroDrawableRes,
                animationSpec = tween(550, easing = FastOutSlowInEasing),
                label = "minimalHeroArt"
            ) { drawableId ->
                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = selectedTheme.displayName,
                    contentScale = ContentScale.Crop,
                    alpha = 0.34f,
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
                                palette.imprintStageBg.copy(alpha = 0.78f),
                                palette.imprintStageBg.copy(alpha = 0.92f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Global Theme Toggle Segmented Control | Streak | Engine Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Global Theme Segmented Selector
                    Surface(
                        modifier = Modifier
                            .testTag("compact_theme_toggle")
                            .clip(RoundedCornerShape(99.dp)),
                        shape = RoundedCornerShape(99.dp),
                        color = palette.imprintStageSurface,
                        border = BorderStroke(1.dp, palette.borderStroke.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(2.5.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppVibeTheme.entries.forEach { theme ->
                                val isSelected = theme == selectedTheme
                                val spec = paletteForVibe(theme)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .clickable { onSelectTheme(theme) }
                                        .testTag("vibe_chip_${theme.id}"),
                                    shape = RoundedCornerShape(99.dp),
                                    color = if (isSelected) spec.primaryAccent else Color.Transparent
                                ) {
                                    Text(
                                        text = theme.shortChip,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) contentColorOnAccent(spec.primaryAccent) else palette.imprintStageSubtext,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = palette.goldAccent.copy(alpha = 0.20f),
                            border = BorderStroke(1.dp, palette.goldAccent.copy(alpha = 0.55f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = palette.goldAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${streakDays}d",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.goldAccent
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .testTag("open_controller_pill")
                                .clip(RoundedCornerShape(99.dp))
                                .clickable { onOpenEngineSettings() },
                            shape = RoundedCornerShape(99.dp),
                            color = Color.White.copy(alpha = 0.11f),
                            border = BorderStroke(
                                1.dp,
                                (if (commanderActive) palette.primaryAccent else palette.secondaryAccent).copy(alpha = 0.6f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (commanderActive) Icons.Rounded.Bolt else Icons.Rounded.Tune,
                                    contentDescription = "Lernprofil öffnen",
                                    tint = if (commanderActive) palette.primaryAccent else palette.secondaryAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (commanderActive) "Commander" else "C1 $functionalC1Percent%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.imprintStageText
                                )
                            }
                        }
                    }
                }

                // Row 2: Daily Stint Summary & Circular Progress Ring
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Heute lernen",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.imprintStageText
                        )
                        if (activeBeat != null && beats.isNotEmpty()) {
                            Text(
                                text = "Schritt ${activeIdx + 1}/${beats.size} · ${activeBeat.beatType.shortSectorLabel}",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.imprintStageSubtext,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            activeBeat.adaptation?.let { spec ->
                                Surface(
                                    modifier = Modifier
                                        .testTag("controller_live_adaptation_strip")
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onOpenEngineSettings() },
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.secondaryAccent.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "${spec.adaptationBadge} · <${spec.targetLatencySeconds}s",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = palette.secondaryAccent,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Tages-Ziel ${dailyGoalMinutes}m abgeschlossen",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.tertiaryAccent
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    DailyProgressRing(
                        progress = progress,
                        centerTitle = "${minutesToday}m",
                        centerSubtitle = "/${dailyGoalMinutes}m",
                        accentColor = palette.primaryAccent,
                        size = 64.dp,
                        centerTextColor = palette.imprintStageText
                    )
                }

                // Row 3: Integrated Interactive Segmented Sector Bar
                if (beats.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        beats.forEach { beat ->
                            val isDone = beat.id in completedBeatIds
                            val isCurrent = !isDone && beat.id == activeBeat?.id
                            val barColor = when {
                                isCurrent -> palette.primaryAccent
                                isDone -> palette.tertiaryAccent
                                else -> Color.White.copy(alpha = 0.20f)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(barColor)
                                    .clickable { onSelectBeat(beat) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Illustrated Imprint-Grade Hero Header for Daily Stint with smooth animated artwork Crossfade,
 * circular telemetry dial, streak fire, and 1-tap Engine shortcut.
 */
@Composable
fun ImprintHeroStintHeader(
    selectedTheme: AppVibeTheme,
    dailyGoalMinutes: Int,
    minutesToday: Int,
    functionalC1Percent: Int,
    streakDays: Int,
    onSelectTheme: (AppVibeTheme) -> Unit,
    onOpenEngineSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val progress = (minutesToday.toFloat() / dailyGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0.06f, 1f)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = palette.imprintStageBg,
            border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Crossfade(
                    targetState = palette.heroDrawableRes,
                    animationSpec = tween(550, easing = FastOutSlowInEasing),
                    label = "heroArtCrossfade"
                ) { drawableId ->
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = selectedTheme.displayName,
                        contentScale = ContentScale.Crop,
                        alpha = 0.52f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(148.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    palette.imprintStageBg.copy(alpha = 0.94f),
                                    palette.imprintStageBg.copy(alpha = 0.68f),
                                    palette.imprintStageBg.copy(alpha = 0.88f)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = palette.primaryAccent
                            ) {
                                Text(
                                    text = "${selectedTheme.badgeTag} · ${dailyGoalMinutes}M STINT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = contentColorOnAccent(palette.primaryAccent),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = palette.goldAccent.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, palette.goldAccent.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = palette.goldAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${streakDays}d",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = palette.goldAccent
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Tages-Training",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = palette.imprintStageText
                        )

                        Surface(
                            modifier = Modifier
                                .testTag("open_controller_pill")
                                .clip(RoundedCornerShape(99.dp))
                                .clickable { onOpenEngineSettings() },
                            shape = RoundedCornerShape(99.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, palette.secondaryAccent.copy(alpha = 0.55f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Tune,
                                    contentDescription = "Engine Einstellungen",
                                    tint = palette.secondaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Engine · C1 $functionalC1Percent%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = palette.imprintStageText
                                )
                            }
                        }
                    }

                    DailyProgressRing(
                        progress = progress,
                        centerTitle = "${minutesToday}m",
                        centerSubtitle = "/ ${dailyGoalMinutes}m",
                        accentColor = palette.primaryAccent,
                        size = 80.dp,
                        centerTextColor = palette.imprintStageText
                    )
                }
            }
        }

        VibeThemeQuickStrip(
            selectedTheme = selectedTheme,
            onSelectTheme = onSelectTheme
        )
    }
}

/**
 * Visual Motorsport Circuit Lap Track for Daily Sectors (replaces crowded sector pill row).
 */
@Composable
fun ImprintSectorLapTrack(
    beats: List<DailySessionBeat>,
    activeBeat: DailySessionBeat,
    completedBeatIds: Set<String>,
    onSelectBeat: (DailySessionBeat) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val inf = rememberInfiniteTransition(label = "lapPulse")
    val pulse by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.38f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "activePulse"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(1.dp, palette.borderStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                beats.forEachIndexed { idx, beat ->
                    val isDone = beat.id in completedBeatIds
                    val isCurrent = !isDone && beat.id == activeBeat.id
                    val nodeColor = when {
                        isCurrent -> palette.primaryAccent
                        isDone -> palette.tertiaryAccent
                        else -> palette.elevatedSurface
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectBeat(beat) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(34.dp)) {
                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .scale(pulse)
                                            .clip(CircleShape)
                                            .background(palette.primaryAccent.copy(alpha = 0.25f))
                                    )
                                }
                                Surface(
                                    modifier = Modifier.size(26.dp),
                                    shape = CircleShape,
                                    color = nodeColor,
                                    border = BorderStroke(
                                        width = 1.2.dp,
                                        color = if (isCurrent || isDone) nodeColor else palette.borderStroke
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isDone) {
                                            Icon(
                                                Icons.Rounded.Check,
                                                contentDescription = "Erledigt",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${idx + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isCurrent) contentColorOnAccent(palette.primaryAccent) else palette.textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = beat.beatType.shortSectorLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = when {
                                    isCurrent -> palette.primaryAccent
                                    isDone -> palette.tertiaryAccent
                                    else -> palette.textSecondary
                                }
                            )
                        }

                        if (idx < beats.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(
                                        if (isDone) palette.tertiaryAccent else palette.borderStroke
                                    )
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(15.dp))
                    Text(
                        text = activeBeat.controllerReason,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = palette.elevatedSurface
                ) {
                    Text(
                        text = "~${activeBeat.estimatedMinutes}m",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

/**
 * 240-Degree Porsche/AMG Radial Tachometer Gauge for Live Functional C1 vs. Goethe C1 Readiness.
 */
@Composable
fun ImprintTelemetryTachometer(
    functionalPercent: Int,
    examPercent: Int,
    precisionPercent: Int,
    automaticityPercent: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val animFunc by animateFloatAsState(
        targetValue = (functionalPercent.coerceIn(5, 100) / 100f),
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 95f),
        label = "tachFunc"
    )
    val animExam by animateFloatAsState(
        targetValue = (examPercent.coerceIn(5, 100) / 100f),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 95f),
        label = "tachExam"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.2.dp, palette.primaryAccent.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(116.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeOuter = 9.dp.toPx()
                    val strokeInner = 6.dp.toPx()
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val padOuter = strokeOuter / 2f + 4.dp.toPx()
                    val padInner = padOuter + 12.dp.toPx()

                    // Outer track + tick marks
                    drawArc(
                        color = Color.White.copy(alpha = 0.12f),
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(padOuter, padOuter),
                        size = Size(size.width - padOuter * 2, size.height - padOuter * 2),
                        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                    )
                    // Redline danger zone at top 85-100%
                    drawArc(
                        color = palette.warningRed.copy(alpha = 0.35f),
                        startAngle = 150f + 240f * 0.85f,
                        sweepAngle = 240f * 0.15f,
                        useCenter = false,
                        topLeft = Offset(padOuter, padOuter),
                        size = Size(size.width - padOuter * 2, size.height - padOuter * 2),
                        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                    )
                    // Active Live Functional C1 Sweep
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(palette.secondaryAccent, palette.primaryAccent, palette.goldAccent)
                        ),
                        startAngle = 150f,
                        sweepAngle = 240f * animFunc,
                        useCenter = false,
                        topLeft = Offset(padOuter, padOuter),
                        size = Size(size.width - padOuter * 2, size.height - padOuter * 2),
                        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                    )

                    // Inner Goethe Exam C1 Arc
                    drawArc(
                        color = Color.White.copy(alpha = 0.10f),
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(padInner, padInner),
                        size = Size(size.width - padInner * 2, size.height - padInner * 2),
                        style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = palette.secondaryAccent,
                        startAngle = 150f,
                        sweepAngle = 240f * animExam,
                        useCenter = false,
                        topLeft = Offset(padInner, padInner),
                        size = Size(size.width - padInner * 2, size.height - padInner * 2),
                        style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                    )

                    // Sweeping Chronograph Needle
                    val angleDeg = 150f + 240f * animFunc
                    val angleRad = (angleDeg * PI / 180.0).toFloat()
                    val needleLen = size.minDimension / 2f - padOuter - 4.dp.toPx()
                    drawLine(
                        color = palette.primaryAccent,
                        start = Offset(cx, cy),
                        end = Offset(cx + cos(angleRad) * needleLen, cy + sin(angleRad) * needleLen),
                        strokeWidth = 2.6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(color = palette.imprintStageText, radius = 5.dp.toPx(), center = Offset(cx, cy))
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 42.dp)
                ) {
                    Text(
                        text = "$functionalPercent%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = palette.primaryAccent
                    )
                    Text(
                        text = "LIVE C1",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.imprintStageSubtext
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CHRONO C1-TELEMETRIE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.primaryAccent
                )
                TelemetryGaugeRow("Goethe-Prüfung", examPercent, palette.secondaryAccent)
                TelemetryGaugeRow("IRT-Präzision", precisionPercent, palette.goldAccent)
                TelemetryGaugeRow("Direkt-Automatik", automaticityPercent, palette.tertiaryAccent)
            }
        }
    }
}

@Composable
private fun TelemetryGaugeRow(label: String, percent: Int, color: Color) {
    val palette = LocalVibePalette.current
    val frac by animateFloatAsState(
        targetValue = (percent.coerceIn(0, 100) / 100f),
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = label
    )
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
            Text("$percent%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(frac.coerceAtLeast(0.05f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(color)
            )
        }
    }
}

/**
 * Graphical 4-Card Goethe C1 Coverage Matrix (replaces dense text rows in Engine Audit).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImprintGoetheVisualMatrix(
    modules: List<GoetheExamModuleAudit>,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val icons = listOf(
        Icons.AutoMirrored.Rounded.MenuBook,
        Icons.Rounded.Headphones,
        Icons.Rounded.EditNote,
        Icons.Rounded.RecordVoiceOver
    )
    val accents = listOf(
        palette.primaryAccent,
        palette.secondaryAccent,
        palette.goldAccent,
        palette.tertiaryAccent
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        modules.chunked(2).forEachIndexed { rowIdx, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEachIndexed { colIdx, mod ->
                    val idx = rowIdx * 2 + colIdx
                    val col = accents[idx % accents.size]
                    val ico = icons[idx % icons.size]
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = palette.imprintStageSurface,
                        border = BorderStroke(1.dp, col.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = col.copy(alpha = 0.18f),
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(ico, contentDescription = null, tint = col, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = col
                                ) {
                                    Text(
                                        text = "${mod.embeddedPracticeCount}×",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = contentColorOnAccent(col),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = mod.moduleCode,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = palette.imprintStageText,
                                maxLines = 1
                            )
                            Text(
                                text = mod.officialFormatDe,
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.imprintStageSubtext,
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

/**
 * Animated Imprint Equalizer Canvas used inside Native Media Broadcast Decks.
 */
@Composable
fun ImprintLiveWaveformBars(
    isPlaying: Boolean,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val inf = rememberInfiniteTransition(label = "mediaEq")
    val phase by inf.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(tween(if (isPlaying) 900 else 2800, easing = LinearEasing), RepeatMode.Restart),
        label = "eqPhase"
    )
    Canvas(modifier = modifier.height(34.dp)) {
        val barCount = 24
        val step = size.width / barCount
        for (i in 0 until barCount) {
            val wave = sin(phase + i * 0.55f)
            val amp = if (isPlaying) 0.30f + 0.68f * kotlin.math.abs(wave) else 0.16f + 0.18f * (i % 4) / 3f
            val bh = size.height * amp
            drawLine(
                color = if (i % 4 == 0) accent else accent.copy(alpha = 0.62f),
                start = Offset(i * step + step / 2f, (size.height - bh) / 2f),
                end = Offset(i * step + step / 2f, (size.height + bh) / 2f),
                strokeWidth = (step * 0.55f).coerceIn(3f, 8f),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Tactile Visual Difficulty Speed-Bars (L1-L4) replacing plain "TIER L3" text.
 */
@Composable
fun ImprintTierMeterPills(
    tier: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (level in 1..4) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height((6 + level * 2).dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (level <= tier) accent else palette.borderStroke.copy(alpha = 0.55f))
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = "L$tier",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = accent
        )
    }
}
