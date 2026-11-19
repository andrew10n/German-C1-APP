package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Visual learning-week component with concentric progress rings,
 * seven-day activity history, milestone badges, and theme accents.
 */
@Composable
fun ImprintStreakTrackerComponent(
    streakDays: Int,
    minutesToday: Int,
    dailyGoalMinutes: Int,
    totalXp: Int,
    recentStudyDaysActive: List<Boolean> = listOf(true, true, true, true, true, false, true),
    modifier: Modifier = Modifier
) {
    val palette = LocalVibePalette.current
    val dailyProgress = (minutesToday.toFloat() / dailyGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0.04f, 1f)
    
    // Milestone progress towards next 7-day checkpoint (e.g. 7, 14, 21, 30 days)
    val nextMilestone = ((streakDays / 7) + 1) * 7
    val streakMilestoneProgress = (streakDays.toFloat() / nextMilestone.toFloat()).coerceIn(0.05f, 1f)

    val inf = rememberInfiniteTransition(label = "streakFlamePulse")
    val flameScale by inf.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(850, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flamePulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_tracker_component"),
        shape = RoundedCornerShape(24.dp),
        color = palette.imprintStageBg,
        border = BorderStroke(1.4.dp, palette.goldAccent.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Title + Flame & Streak Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(palette.goldAccent.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Streak Flame",
                            tint = palette.goldAccent,
                            modifier = Modifier
                                .size(22.dp)
                                .scale(flameScale)
                        )
                    }
                    Column {
                        Text(
                            text = "MEINE LERNWOCHE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = palette.goldAccent
                        )
                        Text(
                            text = "$streakDays Tage am Stück gelernt",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = palette.imprintStageText
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = palette.primaryAccent
                ) {
                    Text(
                        text = "$totalXp XP",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = contentColorOnAccent(palette.primaryAccent),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Concentric Dual Progress Rings + Summary Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Dual Concentric Rings visual representation
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ConcentricStreakRings(
                        dailyProgress = dailyProgress,
                        streakMilestoneProgress = streakMilestoneProgress,
                        accentColor = palette.primaryAccent,
                        secondaryAccent = palette.goldAccent,
                        trackColor = palette.imprintStageSurface
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$streakDays d",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = palette.imprintStageText
                        )
                        Text(
                            text = "Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.imprintStageSubtext
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Breakdown metrics cards
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StreakMetricRow(
                        label = "Tages-Stint Ziel",
                        value = "${minutesToday}m / ${dailyGoalMinutes}m",
                        progress = dailyProgress,
                        accent = palette.primaryAccent
                    )
                    StreakMetricRow(
                        label = "Meilenstein ($nextMilestone Tage)",
                        value = "${(streakMilestoneProgress * 100).roundToInt()}% erreicht",
                        progress = streakMilestoneProgress,
                        accent = palette.goldAccent
                    )
                }
            }

            // 7-Day Consistency Sparkline History (Mon - Sun)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WÖCHENTLICHE KONTINUITÄT (7 TAGE)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.imprintStageSubtext
                    )
                    Text(
                        text = "${recentStudyDaysActive.count { it }}/7 Aktiv",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.secondaryAccent
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val dayLabels = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
                    recentStudyDaysActive.forEachIndexed { idx, isActive ->
                        val label = dayLabels.getOrNull(idx) ?: "Tag"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(34.dp),
                                shape = CircleShape,
                                color = if (isActive) palette.secondaryAccent else palette.imprintStageSurface,
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isActive) palette.secondaryAccent else palette.borderStroke
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isActive) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = label,
                                            tint = contentColorOnAccent(palette.secondaryAccent),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(palette.imprintStageSubtext.copy(alpha = 0.4f))
                                        )
                                    }
                                }
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isActive) palette.imprintStageText else palette.imprintStageSubtext
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConcentricStreakRings(
    dailyProgress: Float,
    streakMilestoneProgress: Float,
    accentColor: Color,
    secondaryAccent: Color,
    trackColor: Color,
    size: Dp = 100.dp
) {
    val animDaily by animateFloatAsState(
        targetValue = dailyProgress.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "ringDaily"
    )
    val animMilestone by animateFloatAsState(
        targetValue = streakMilestoneProgress.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "ringMilestone"
    )

    Canvas(modifier = Modifier.size(size)) {
        val strokeOuter = 8.dp.toPx()
        val strokeInner = 6.dp.toPx()
        val cx = size.toPx() / 2f
        val cy = size.toPx() / 2f

        val padOuter = strokeOuter / 2f + 2.dp.toPx()
        val padInner = padOuter + strokeOuter / 2f + strokeInner / 2f + 3.dp.toPx()

        // Outer Ring (Daily Minutes Goal)
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(padOuter, padOuter),
            size = Size(size.toPx() - padOuter * 2, size.toPx() - padOuter * 2),
            style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
        )
        drawArc(
            color = accentColor,
            startAngle = -90f,
            sweepAngle = 360f * animDaily,
            useCenter = false,
            topLeft = Offset(padOuter, padOuter),
            size = Size(size.toPx() - padOuter * 2, size.toPx() - padOuter * 2),
            style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
        )

        // Inner Ring (Streak Milestone Progress)
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(padInner, padInner),
            size = Size(size.toPx() - padInner * 2, size.toPx() - padInner * 2),
            style = Stroke(width = strokeInner, cap = StrokeCap.Round)
        )
        drawArc(
            color = secondaryAccent,
            startAngle = -90f,
            sweepAngle = 360f * animMilestone,
            useCenter = false,
            topLeft = Offset(padInner, padInner),
            size = Size(size.toPx() - padInner * 2, size.toPx() - padInner * 2),
            style = Stroke(width = strokeInner, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun StreakMetricRow(
    label: String,
    value: String,
    progress: Float,
    accent: Color
) {
    val palette = LocalVibePalette.current
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0.04f, 1f),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = label
    )

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = palette.imprintStageSubtext)
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = accent)
        }
        LinearProgressIndicator(
            progress = { animProgress },
            color = accent,
            trackColor = palette.imprintStageSurface,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(99.dp))
        )
    }
}
