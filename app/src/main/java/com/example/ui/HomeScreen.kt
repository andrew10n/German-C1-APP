package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SkillCategory
import com.example.ui.components.BerlinRadarChart
import com.example.ui.components.C1ProficiencyProgressChart
import com.example.ui.components.DailyStreakBadge
import com.example.ui.components.GrammarTopicsProgressChart
import com.example.ui.components.NativeListeningAudioPlayerComponent
import com.example.ui.components.VisualPerformanceDashboard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkTheme.collectAsState()
    val diagnosticReport by viewModel.repository.diagnosticReport.collectAsState()
    val dualBenchmark by viewModel.repository.dualBenchmark.collectAsState()
    val errorList by viewModel.repository.errorDatabase.collectAsState()
    val skillMap by viewModel.repository.skillScores.collectAsState()
    val streakState by viewModel.streakState.collectAsState()
    val topPriority = remember(diagnosticReport) { viewModel.repository.getTopTrainingPriority() }
    val activeErrorsCount = errorList.count { it.masteryStage.stepIndex < 5 }

    val selectedTabIndex = 0
    var selectedSessionDurationMinutes by remember { mutableIntStateOf(18) }

    // Pulsing live indicator for active system
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val tokens = BerlinTheme

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.bg)
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -------------------------------------------------------------
        // 1. Berlin U-Bahn Wayfinding Top Bar (Station Info & Theme Toggle)
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Station Icon & Headline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = tokens.surface,
                        border = BorderStroke(1.5.dp, tokens.line),
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.c1_app_logo),
                            contentDescription = "C1 Deutsch Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(tokens.accent.copy(alpha = pulseAlpha))
                            )
                            Text(
                                text = "BERLIN C1 LINIE",
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.accentInk,
                                letterSpacing = 1.1.sp
                            )
                        }

                        Text(
                            text = "Stationen zur Souveränität",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.ink,
                            letterSpacing = 0.2.sp
                        )
                    }
                }

                // Header Actions: Daily Streak Badge, Theme Toggle & Station Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DailyStreakBadge(
                        streakState = streakState,
                        onStartSessionClick = { viewModel.startDailySession() }
                    )

                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier
                            .testTag("btn_toggle_theme")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme wechseln",
                            tint = if (isDark) tokens.accent else tokens.ink,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        onClick = { viewModel.startDailySession() },
                        shape = RoundedCornerShape(99.dp),
                        color = tokens.accentSoft,
                        border = BorderStroke(1.dp, tokens.accent),
                        modifier = Modifier.testTag("action_baseline_diagnostic")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = null,
                                tint = tokens.accentInk,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Tagesprofil",
                                fontFamily = BarlowFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.accentInk
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. Dark "Hero" Card holding the Radar Chart (.hero component)
        // -------------------------------------------------------------
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_radar_card"),
                shape = RoundedCornerShape(30.dp),
                color = tokens.hero,
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(tokens.accent.copy(alpha = 0.22f), Color.Transparent),
                                    center = center.copy(y = size.height * 0.38f),
                                    radius = size.width * 0.62f
                                )
                            )
                        }
                        .padding(top = 22.dp, bottom = 18.dp, start = 16.dp, end = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Station Title Row inside Hero
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "C1 KOMPETENZ-RADAR",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accent,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Aktuelle Signal-Messung",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.onHero
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = tokens.accent.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = "TÄGLICH ADAPTIV",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accent,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // 8-Axis Radar Chart Canvas
                        BerlinRadarChart(
                            skillScores = skillMap,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )

                        // Status Station Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(tokens.accent)
                                )
                                Text(
                                    text = "Fokus wird nach jeder Übung neu geordnet",
                                    fontFamily = BarlowFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = tokens.onHero,
                                    maxLines = 1
                                )
                            }
                        }

                        // Primary Action Button: 54dp tall, 16dp radius, teal background, dark navy text
                        Button(
                            onClick = { viewModel.startDailySession() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_start_daily_session"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.accent,
                                contentColor = tokens.onAccent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = tokens.onAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Heutige Station starten (18 Min.)",
                                fontFamily = BarlowFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = tokens.onAccent
                            )
                        }
                    }
                }
            }
        }

        // =============================================================
        // MEIN PERSONALISIERTER TRAININGSPLAN
        // =============================================================
        if (selectedTabIndex == 0) {
            // Personalized daily station; priorities update from practice evidence.
            item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hero_daily_session_card"),
                        shape = RoundedCornerShape(24.dp),
                        color = tokens.surface,
                        border = BorderStroke(1.dp, tokens.line),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Top Bar with Priority Pill & Duration Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = tokens.accentSoft,
                                    border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = tokens.accentInk,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Personalisiert • ${topPriority.calculatedPriority}% Relevanz",
                                            fontFamily = BarlowFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.accentInk
                                        )
                                    }
                                }

                                // Duration Selector Toggle (18 Min vs 8 Min)
                                Row(
                                    modifier = Modifier
                                        .background(tokens.surface2, RoundedCornerShape(99.dp))
                                        .padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Surface(
                                        onClick = { selectedSessionDurationMinutes = 18 },
                                        shape = RoundedCornerShape(99.dp),
                                        color = if (selectedSessionDurationMinutes == 18) tokens.hero else Color.Transparent
                                    ) {
                                        Text(
                                            text = "18 Min",
                                            fontFamily = BarlowFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedSessionDurationMinutes == 18) tokens.onHero else tokens.ink2,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                        )
                                    }
                                    Surface(
                                        onClick = { selectedSessionDurationMinutes = 8 },
                                        shape = RoundedCornerShape(99.dp),
                                        color = if (selectedSessionDurationMinutes == 8) tokens.hero else Color.Transparent
                                    ) {
                                        Text(
                                            text = "8 Min",
                                            fontFamily = BarlowFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedSessionDurationMinutes == 8) tokens.onHero else tokens.ink2,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Challenge Title & Rationale
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = topPriority.recommendedTaskTitle,
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.ink,
                                    lineHeight = 26.sp
                                )
                                Text(
                                    text = topPriority.detailedRationale,
                                    fontFamily = BarlowFontFamily,
                                    fontSize = 13.sp,
                                    color = tokens.ink2,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
            }

            // Error Alert: .alert style with coral-soft background, coral text, 24dp radius
            if (activeErrorsCount > 0) {
                item {
                    Surface(
                        onClick = { viewModel.navigateTo(Screen.ErrorIntelligence) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_errors_callout"),
                        shape = RoundedCornerShape(24.dp),
                        color = tokens.coralSoft,
                        border = BorderStroke(1.5.dp, tokens.coral.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = tokens.coral,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PriorityHigh,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "$activeErrorsCount wiederkehrende Fehler fällig",
                                        fontFamily = BarlowCondensedFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = tokens.coral
                                    )
                                    Text(
                                        text = "Fehler-Recycling vor der nächsten freien Produktion",
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 12.sp,
                                        color = tokens.ink2
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Fehler trainieren",
                                tint = tokens.coral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Category Wayfinding Section: The 6 Core Intelligence Engines in Bento Grid
            item {
                Text(
                    text = "Die 6 Trainings-Stationen",
                    fontFamily = BarlowCondensedFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.ink,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Dedicated Speaking & Pronunciation Practice Card
            item {
                Surface(
                    onClick = { viewModel.navigateTo(Screen.SpeakingLab) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_speaking_pronunciation_lab"),
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.5.dp, tokens.accent.copy(alpha = 0.6f)),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = tokens.accentSoft,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = tokens.accentInk,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Aussprache-Labor",
                                        fontFamily = BarlowCondensedFontFamily,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.ink
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = tokens.accentSoft
                                    ) {
                                        Text(
                                            text = "MIKROFON",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.accentInk,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Echtzeit-Spracherfassung, Lautanalyse & Wort-Feedback",
                                    fontSize = 12.sp,
                                    color = tokens.ink2
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Starten",
                            tint = tokens.accentInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Specialized Football Coach (C-Jugend U15) Learning Track Card
            item {
                Surface(
                    onClick = { viewModel.navigateTo(Screen.FootballCoachTrack) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_football_coach_track"),
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.5.dp, tokens.sky.copy(alpha = 0.55f)),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = tokens.skySoft,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SportsSoccer,
                                        contentDescription = null,
                                        tint = tokens.sky,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Fußball-Trainer (U15)",
                                        fontFamily = BarlowCondensedFontFamily,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.ink
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = tokens.skySoft
                                    ) {
                                        Text(
                                            text = "C-JUGEND",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.sky,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "DFB-Taktikglossar, Spielszenarien & Drill-Kommandos",
                                    fontSize = 12.sp,
                                    color = tokens.ink2
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Lernpfad öffnen",
                            tint = tokens.sky,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Engine Bento Grid Row 1 (Listening - Sky Blue, Speaking - Teal Accent)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BerlinEngineCard(
                        title = "Hör-Labor",
                        subtitle = "Schnellsprech & Dialekte (1.0x - 1.35x)",
                        tagLabel = "DIALEKT",
                        icon = Icons.Default.Hearing,
                        tag = "lab_listening",
                        categoryColor = tokens.sky,
                        categorySoftColor = tokens.skySoft,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.ListeningLab) }
                    )
                    BerlinEngineCard(
                        title = "Sprech-Simulator",
                        subtitle = "Spontane Latenz & Gegenrede unter Druck",
                        tagLabel = "STRESSTEST",
                        icon = Icons.Default.RecordVoiceOver,
                        tag = "simulator_speaking",
                        categoryColor = tokens.accent,
                        categorySoftColor = tokens.accentSoft,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.ConversationSimulator) }
                    )
                }
            }

            // Engine Bento Grid Row 2 (Error Intelligence - Coral Alert, Mediation - Violet)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BerlinEngineCard(
                        title = "Fehler-Intelligenz",
                        subtitle = "$activeErrorsCount aktive Fehler im Recycling",
                        tagLabel = if (activeErrorsCount > 0) "$activeErrorsCount FÄLLIG" else "STABIL",
                        icon = Icons.Default.Autorenew,
                        tag = "error_intelligence",
                        badgeCount = activeErrorsCount,
                        categoryColor = tokens.coral,
                        categorySoftColor = tokens.coralSoft,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.ErrorIntelligence) }
                    )
                    BerlinEngineCard(
                        title = "Sprachmittlung",
                        subtitle = "Mediation & Register-Transfer",
                        tagLabel = "BÜRGERNAH",
                        icon = Icons.Default.Transform,
                        tag = "mediation_engine",
                        categoryColor = tokens.violet,
                        categorySoftColor = tokens.violetSoft,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Mediation) }
                    )
                }
            }

            // Engine Bento Grid Row 3 (Full 9D Radar Deep Dive)
            item {
                Surface(
                    onClick = { viewModel.navigateTo(Screen.IntelligenceProfile) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("intelligence_profile"),
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.dp, tokens.line),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.hero,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Insights,
                                        contentDescription = null,
                                        tint = tokens.accent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "9D Kompetenz-Radar & Profil",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = tokens.ink
                                )
                                Text(
                                    text = "Stärken-Analyse & CEFR C1-Kriterien",
                                    fontFamily = BarlowFontFamily,
                                    fontSize = 13.sp,
                                    color = tokens.ink2
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Details",
                            tint = tokens.accentInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // =============================================================
        // TAB 1: FORTSCHRITT & ANALYTIK
        // =============================================================
        if (selectedTabIndex == 1) {
            // Live C1 Fortschritts-Dashboard KPI Summary Card
            item {
                val resolvedErrorsCount = errorList.count { it.masteryStage.stepIndex >= 5 }
                val overallReadiness = (dualBenchmark.functionalC1Score + dualBenchmark.examC1Score) / 2
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("progress_dashboard_summary"),
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.dp, tokens.line),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Insights,
                                    contentDescription = null,
                                    tint = tokens.accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "C1 Fortschritts-Dashboard",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.ink
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = tokens.accentSoft,
                                border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "LIVE",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accentInk,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProgressKpiTile(
                                label = "Lern-Serie",
                                value = "${streakState.currentStreak} Tage",
                                subtitle = "Rekord: ${streakState.longestStreak} Tage",
                                icon = Icons.Default.LocalFireDepartment,
                                accentColor = Color(0xFFFF7A1A),
                                modifier = Modifier.weight(1f)
                            )
                            ProgressKpiTile(
                                label = "Einheiten",
                                value = "${streakState.totalSessionsCompleted}",
                                subtitle = "18-Min-Zyklen",
                                icon = Icons.Default.Bolt,
                                accentColor = tokens.accent,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProgressKpiTile(
                                label = "Fehler-Transfer",
                                value = "$resolvedErrorsCount / ${errorList.size}",
                                subtitle = "Gemeisterte Muster",
                                icon = Icons.Default.CheckCircle,
                                accentColor = tokens.sky,
                                modifier = Modifier.weight(1f)
                            )
                            ProgressKpiTile(
                                label = "C1-Reifegrad",
                                value = "$overallReadiness%",
                                subtitle = "Funktional & Prüfung",
                                icon = Icons.Default.TrendingUp,
                                accentColor = tokens.violet,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Dual Benchmark Intelligence Card in .card (24dp radius)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("benchmark_card"),
                    onClick = { viewModel.navigateTo(Screen.IntelligenceProfile) },
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.dp, tokens.line),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = tokens.accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Duales C1-Leistungs-Radar",
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.ink
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "9D-Radar",
                                    fontFamily = BarlowFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.accentInk
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = tokens.accentInk,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Metric 1: Functional Real-world C1 (Sky Blue Category)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = tokens.skySoft,
                                border = BorderStroke(1.dp, tokens.sky.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Funktional (Echtzeit)",
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = tokens.ink2
                                    )
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${dualBenchmark.functionalC1Score}",
                                            fontFamily = BarlowCondensedFontFamily,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.sky
                                        )
                                        Text(
                                            text = " / 100",
                                            fontFamily = BarlowFontFamily,
                                            fontSize = 12.sp,
                                            color = tokens.ink2,
                                            modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { dualBenchmark.functionalC1Score / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = tokens.sky,
                                        trackColor = tokens.line
                                    )
                                    Text(
                                        text = "Spontane Reaktionszeit",
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 10.sp,
                                        color = tokens.sky
                                    )
                                }
                            }

                            // Metric 2: Standard Formal Exam C1 (Violet Category)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = tokens.violetSoft,
                                border = BorderStroke(1.dp, tokens.violet.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Prüfungsreife (Goethe/telc)",
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = tokens.ink2
                                    )
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${dualBenchmark.examC1Score}",
                                            fontFamily = BarlowCondensedFontFamily,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.violet
                                        )
                                        Text(
                                            text = " %",
                                            fontFamily = BarlowFontFamily,
                                            fontSize = 12.sp,
                                            color = tokens.ink2,
                                            modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { dualBenchmark.examC1Score / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = tokens.violet,
                                        trackColor = tokens.line
                                    )
                                    Text(
                                        text = "Syntax & Textkohärenz",
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 10.sp,
                                        color = tokens.violet
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // C1 Proficiency Progress Over Time (Spline Area Chart)
            item {
                C1ProficiencyProgressChart()
            }

            // German Grammar Topics Progress (Recharts-Style Visualizer)
            item {
                GrammarTopicsProgressChart()
            }

            // Visual Performance Dashboard
            item {
                VisualPerformanceDashboard()
            }
        }

        // =============================================================
        // TAB 2: AUDIO-LABOR & NATIVE LISTENING
        // =============================================================
        if (selectedTabIndex == 2) {
            // Native-Speed Audio Player Component
            item {
                NativeListeningAudioPlayerComponent(viewModel = viewModel)
            }

            // Listening Strategy Cards in .card (24dp radius)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.dp, tokens.line),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tokens.skySoft,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Hearing,
                                        contentDescription = null,
                                        tint = tokens.sky,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Strategie: C1-Hörverstehen bei 1.2x – 1.35x",
                                fontFamily = BarlowCondensedFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = tokens.ink
                            )
                        }
                        Text(
                            text = "Muttersprachler sprechen im Diskurs ca. 140–170 Wörter pro Minute mit Laut-Tilgungen ('ham' statt 'haben', 'guck mal' statt 'schau einmal') und regionalen Färbungen. Unser Hör-Labor trainiert Ihr Gehör selektiv auf diese phonetischen Reduktionen.",
                            fontFamily = BarlowFontFamily,
                            fontSize = 13.sp,
                            color = tokens.ink2,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Bottom space so content is not obscured by the floating dock
        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
fun BerlinTabChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = BerlinTheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(99.dp),
        color = if (selected) tokens.hero else Color.Transparent,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontFamily = BarlowFontFamily,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) tokens.accent else tokens.ink2
            )
        }
    }
}

@Composable
fun BerlinEngineCard(
    title: String,
    subtitle: String,
    tagLabel: String,
    icon: ImageVector,
    tag: String,
    categoryColor: Color,
    categorySoftColor: Color,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    onClick: () -> Unit
) {
    val tokens = BerlinTheme
    Surface(
        onClick = onClick,
        modifier = modifier.testTag(tag),
        shape = RoundedCornerShape(24.dp),
        color = tokens.surface,
        border = BorderStroke(1.dp, tokens.line),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = categorySoftColor,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = if (badgeCount != null && badgeCount > 0) tokens.coralSoft else tokens.surface2,
                    border = BorderStroke(1.dp, if (badgeCount != null && badgeCount > 0) tokens.coral else tokens.line)
                ) {
                    Text(
                        text = tagLabel,
                        color = if (badgeCount != null && badgeCount > 0) tokens.coral else tokens.ink2,
                        fontFamily = BarlowCondensedFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    fontFamily = BarlowCondensedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = tokens.ink
                )
                Text(
                    text = subtitle,
                    fontFamily = BarlowFontFamily,
                    fontSize = 12.sp,
                    color = tokens.ink2,
                    lineHeight = 15.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ProgressKpiTile(
    label: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val tokens = BerlinTheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = tokens.surface2,
        border = BorderStroke(1.dp, tokens.line)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    fontFamily = BarlowFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.ink2
                )
            }
            Text(
                text = value,
                fontFamily = BarlowCondensedFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.ink
            )
            Text(
                text = subtitle,
                fontFamily = BarlowFontFamily,
                fontSize = 10.sp,
                color = accentColor
            )
        }
    }
}

