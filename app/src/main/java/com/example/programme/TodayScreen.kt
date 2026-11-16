package com.example.programme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStreakState
import com.example.ui.theme.BerlinTheme

@Composable
internal fun TodayScreen(
    modifier: Modifier = Modifier,
    preferences: ProgrammePreferences,
    attempts: List<Attempt>,
    now: Long,
    historyAvailable: Boolean,
    adaptiveDecision: AdaptiveControlDecision,
    weaknessProfile: WeaknessProfile,
    streakState: DailyStreakState,
    onOpen: (CurriculumUnit, String) -> Unit
) {
    val language = preferences.language
    val accentColor = BerlinTheme.accent
    val next = AdaptiveDiagnosticRoute.nextUnit(attempts, now)
        ?: CurriculumCatalogue.coreUnits.firstOrNull { it.content != null }
    val sessionTask = adaptiveDecision.primaryTaskId ?: "retrieval"
    val hasStarted = next != null && attempts.any { it.unitId == next.id && it.timestamp <= now }
    val pulse by rememberInfiniteTransition(label = "session pulse").animateFloat(
        initialValue = 0.96f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "session pulse scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("today_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                language.text("C1 DEUTSCH", "C1 GERMAN"),
                style = MaterialTheme.typography.labelMedium,
                color = BerlinTheme.accentInk
            )
            Text(
                if (preferences.learnerName.isBlank()) {
                    language.text("Heute nur ein guter Schritt.", "Take one good step today.")
                } else {
                    language.text(
                        "Heute genügt ein guter Schritt, ${preferences.learnerName.trim()}.",
                        "${preferences.learnerName.trim()}, one good step is enough today."
                    )
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(99.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = BerlinTheme.accentInk,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        language.text(
                            "${streakState.currentStreak} Tage Serie",
                            "${streakState.currentStreak}-day streak"
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    language.text("Heute", "Today"),
                    style = MaterialTheme.typography.labelMedium,
                    color = BerlinTheme.ink2
                )
                Text(
                    if (streakState.isPracticedToday) {
                        language.text("Übung erfasst", "Practice logged")
                    } else {
                        language.text("Noch keine Übung", "No practice yet")
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("today_recommended_session"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.15f),
                                    androidx.compose.ui.graphics.Color.Transparent
                                ),
                                center = center.copy(x = size.width * 0.88f, y = size.height * 0.16f),
                                radius = size.width * 0.72f
                            )
                        )
                    }
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            language.text("DEIN NÄCHSTER SCHRITT", "YOUR NEXT SESSION"),
                            style = MaterialTheme.typography.labelMedium,
                            color = BerlinTheme.accentInk
                        )
                        Text(
                            next?.title ?: language.text("Dein Einstieg", "Your starting point"),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer(scaleX = pulse, scaleY = pulse),
                        shape = CircleShape,
                        color = BerlinTheme.hero
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${preferences.dailyMinutes}",
                                    color = BerlinTheme.onHero,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    language.text("MIN", "MIN"),
                                    color = BerlinTheme.onHero2,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                Text(
                    if (historyAvailable) {
                        adaptiveExplanation(adaptiveDecision, weaknessProfile, language)
                    } else {
                        language.text(
                            "Dein persönlicher Lernweg beginnt hier. Die Empfehlung wird angezeigt, sobald dein Lernverlauf bereit ist.",
                            "Your personal learning path starts here. The recommendation will appear when your practice history is ready."
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    language.text(
                        "Geplante Tageszeit · keine gemessene Sitzungsdauer",
                        "Planned daily time · session duration is not measured"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                next?.let { unit ->
                    Button(
                        onClick = { onOpen(unit, sessionTask) },
                        modifier = Modifier.fillMaxWidth().testTag("dashboard_quick_start")
                    ) {
                        Text(
                            if (hasStarted) language.text("Weiterüben", "Continue")
                            else language.text("Session starten", "Start session")
                        )
                    }
                }
            }
        }
    }
}
