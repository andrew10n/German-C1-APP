package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SevereCold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyStreakState
import com.example.ui.theme.BarlowCondensedFontFamily
import com.example.ui.theme.BarlowFontFamily
import com.example.ui.theme.BerlinTheme

// Distinctive Flame & Milestone colors (dark-theme friendly translucent tints)
val FlameOrange = Color(0xFFFF7A1A)
val FlameGold = Color(0xFFFFC107)
val FlameSoftBg = Color(0xFFFF7A1A).copy(alpha = 0.16f)
val FreezeBlue = Color(0xFF38BDF8)
val FreezeSoftBg = Color(0xFF38BDF8).copy(alpha = 0.14f)

/**
 * Interactive Top-Corner Daily Streak Badge that sits in the HomeScreen header.
 * Displays the current consecutive days streak with an animated flame icon and opens
 * the detailed Gamified Streak Dialog on click.
 */
@Composable
fun DailyStreakBadge(
    streakState: DailyStreakState,
    onStartSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // Subtle gentle flame pulse
    val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    Surface(
        onClick = { showDialog = true },
        shape = RoundedCornerShape(99.dp),
        color = if (streakState.isPracticedToday) FlameSoftBg else BerlinTheme.surface2,
        border = BorderStroke(
            1.5.dp,
            if (streakState.isPracticedToday) FlameOrange.copy(alpha = 0.8f) else BerlinTheme.line
        ),
        shadowElevation = 2.dp,
        modifier = modifier.testTag("daily_streak_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak Flamme",
                tint = if (streakState.isPracticedToday) FlameOrange else BerlinTheme.ink2,
                modifier = Modifier
                    .size(18.dp)
                    .scale(if (streakState.isPracticedToday) flameScale else 1.0f)
            )

            Text(
                text = "${streakState.currentStreak}",
                fontFamily = BarlowCondensedFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (streakState.isPracticedToday) FlameOrange else BerlinTheme.ink
            )

            Text(
                text = if (streakState.currentStreak == 1) "Tag" else "Tage",
                fontFamily = BarlowFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                color = if (streakState.isPracticedToday) FlameGold else BerlinTheme.ink2
            )
        }
    }

    if (showDialog) {
        DailyStreakDialog(
            streakState = streakState,
            onDismiss = { showDialog = false },
            onStartSessionClick = {
                showDialog = false
                onStartSessionClick()
            }
        )
    }
}

/**
 * Gamified Streak Dialog showcasing:
 * 1. Current streak flame animation & record streak
 * 2. Weekly activity calendar strip (Mo-So)
 * 3. Next Milestone progress bar & reward
 * 4. Active Streak-Freeze protection status
 * 5. Motivational C1 quote & 18-minute session quick launch
 */
@Composable
fun DailyStreakDialog(
    streakState: DailyStreakState,
    onDismiss: () -> Unit,
    onStartSessionClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("daily_streak_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = BerlinTheme.surface,
            border = BorderStroke(1.dp, BerlinTheme.line),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top close action & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FlameSoftBg,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = FlameOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "C1 KONTINUITÄTS-RADAR",
                            fontFamily = BarlowCondensedFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BerlinTheme.ink2,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("streak_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = BerlinTheme.ink2
                        )
                    }
                }

                // Giant Burning Flame & Current Streak Display
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(FlameGold.copy(alpha = 0.35f), FlameOrange.copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = FlameOrange,
                            modifier = Modifier.size(46.dp)
                        )
                        Text(
                            text = "${streakState.currentStreak}",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = BerlinTheme.ink,
                            lineHeight = 32.sp
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${streakState.currentStreak} Tage in Folge aktiv!",
                        fontFamily = BarlowCondensedFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BerlinTheme.ink
                    )
                    Text(
                        text = if (streakState.isPracticedToday) {
                            "Heute bereits trainiert • Dein C1-Fokus brennt!"
                        } else {
                            "Heute noch nicht trainiert • Halte die Serie am Leben!"
                        },
                        fontSize = 13.sp,
                        color = if (streakState.isPracticedToday) BerlinTheme.ok else FlameOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // 7-Day Weekly Calendar Strip
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BerlinTheme.surface2,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    modifier = Modifier.fillMaxWidth()
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
                                text = "WOCHEN-AKTIVITÄT",
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.ink2
                            )
                            Text(
                                text = "Rekord: ${streakState.longestStreak} Tage",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.accentInk
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            streakState.weeklyDays.forEach { day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = day.shortLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                                        color = if (day.isToday) BerlinTheme.ink else BerlinTheme.ink2
                                    )

                                    Surface(
                                        shape = CircleShape,
                                        color = when {
                                            day.isCompleted -> FlameOrange
                                            day.isToday -> FlameSoftBg
                                            else -> BerlinTheme.surface
                                        },
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (day.isToday) FlameOrange else BerlinTheme.line
                                        ),
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (day.isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else if (day.isToday) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(FlameOrange)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Milestone Progress Card
                val milestone = streakState.nextMilestone
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BerlinTheme.surface,
                    border = BorderStroke(1.dp, BerlinTheme.line),
                    modifier = Modifier.fillMaxWidth()
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = FlameGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = milestone.titleDe,
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BerlinTheme.ink
                                )
                            }

                            Text(
                                text = "${streakState.currentStreak}/${milestone.targetDays} Tage",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.ink2
                            )
                        }

                        LinearProgressIndicator(
                            progress = { milestone.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(99.dp)),
                            color = FlameOrange,
                            trackColor = BerlinTheme.surface2,
                        )

                        Text(
                            text = "Belohnung: ${milestone.rewardDescriptionDe}",
                            fontSize = 11.5.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                }

                // Streak Freeze Protection Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FreezeSoftBg,
                    border = BorderStroke(1.dp, FreezeBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SevereCold,
                            contentDescription = null,
                            tint = FreezeBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${streakState.streakFreezesAvailable} Streak-Freeze verfügbar (schützt vor 1 verpassten Tag)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = FreezeBlue
                        )
                    }
                }

                // Primary CTA button
                Button(
                    onClick = onStartSessionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("streak_dialog_start_session_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BerlinTheme.accent,
                        contentColor = BerlinTheme.onAccent
                    )
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "18-Min. Session starten",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
