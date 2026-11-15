package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun FootballCoachTrackScreen(
    viewModel: C1ViewModel,
    modifier: Modifier = Modifier
) {
    val tokens = BerlinTheme

    BackHandler {
        viewModel.speechHelper.stop()
        viewModel.navigateTo(Screen.Home)
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Glossar, 1 = Szenarien, 2 = Drills
    var selectedCategory by remember { mutableStateOf<CoachTermCategory?>(null) }

    // Track completed tactical scenarios
    val selectedAnswers = remember { mutableStateMapOf<String, Int>() }
    val completedDrills = remember { mutableStateMapOf<String, Boolean>() }

    val filteredTerms = remember(selectedCategory) {
        if (selectedCategory == null) FootballCoachU15Data.terms
        else FootballCoachU15Data.terms.filter { it.category == selectedCategory }
    }

    val solvedScenarioCount = FootballCoachU15Data.scenarios.count { scenario ->
        selectedAnswers[scenario.id] == scenario.correctIndex
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.bg)
            .testTag("football_coach_track_screen")
    ) {
        // Top Header Bar
        Surface(
            color = tokens.hero,
            border = BorderStroke(1.dp, tokens.line)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.speechHelper.stop()
                                viewModel.navigateTo(Screen.Home)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("coach_track_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Zurück",
                                tint = tokens.ink
                            )
                        }

                        Column {
                            Text(
                                text = "SPEZIAL-MODUL · DFB C-JUGEND (U15)",
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.accentInk,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Fußball-Trainer Lernpfad",
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.ink
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = tokens.accentSoft,
                        border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "$solvedScenarioCount/${FootballCoachU15Data.scenarios.size} TAKTIK",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.accentInk,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // 3-Tab Track Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(tokens.surface2)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("Taktik-Glossar", "Spielszenarien", "Drill-Kommandos")
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = selectedTab == idx
                        Surface(
                            onClick = { selectedTab = idx },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 44.dp)
                                .testTag("coach_tab_$idx"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) tokens.accent else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                                Text(
                                    text = label,
                                    fontFamily = BarlowCondensedFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) tokens.onAccent else tokens.ink2,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scrollable Track Body
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedTab == 0) {
                // Category Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null },
                                label = {
                                    Text(
                                        "Alle (${FootballCoachU15Data.terms.size})",
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tokens.accentSoft,
                                    selectedLabelColor = tokens.accentInk,
                                    containerColor = tokens.surface,
                                    labelColor = tokens.ink2
                                )
                            )
                        }
                        items(CoachTermCategory.entries) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = if (selectedCategory == cat) null else cat
                                },
                                label = {
                                    Text(
                                        cat.label,
                                        fontFamily = BarlowFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tokens.accentSoft,
                                    selectedLabelColor = tokens.accentInk,
                                    containerColor = tokens.surface,
                                    labelColor = tokens.ink2
                                )
                            )
                        }
                    }
                }

                items(filteredTerms, key = { it.id }) { termItem ->
                    CoachTermCard(
                        item = termItem,
                        onSpeakCommand = {
                            viewModel.speechHelper.speak(termItem.pitchCommand, 1.0f)
                            viewModel.recordPracticeActivity("C-Jugend Taktik-Glossar")
                        }
                    )
                }
            }

            if (selectedTab == 1) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = tokens.surface,
                        border = BorderStroke(1.dp, tokens.line)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = null,
                                tint = tokens.accent,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Entscheide dich in echten C-Jugend (U15) Spielsituationen für die taktisch und sprachlich stärkste Trainerreaktion.",
                                fontFamily = BarlowFontFamily,
                                fontSize = 13.sp,
                                color = tokens.ink2,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                items(FootballCoachU15Data.scenarios, key = { it.id }) { scenario ->
                    TacticalScenarioCard(
                        scenario = scenario,
                        selectedOption = selectedAnswers[scenario.id],
                        onSelectOption = { idx ->
                            selectedAnswers[scenario.id] = idx
                            if (idx == scenario.correctIndex) {
                                viewModel.recordPracticeActivity("C-Jugend Taktik-Szenario")
                            }
                        },
                        onSpeakCommand = {
                            viewModel.speechHelper.speak(scenario.keyCommandToShout, 1.05f)
                        }
                    )
                }
            }

            if (selectedTab == 2) {
                items(FootballCoachU15Data.drills, key = { it.id }) { drill ->
                    val isDone = completedDrills[drill.id] == true
                    DrillCommunicationCard(
                        drill = drill,
                        isCompleted = isDone,
                        onSpeakCommand = { cmd ->
                            viewModel.speechHelper.speak(cmd, 1.05f)
                        },
                        onToggleComplete = {
                            val next = !isDone
                            completedDrills[drill.id] = next
                            if (next) {
                                viewModel.recordPracticeActivity("C-Jugend Drill-Kommunikation")
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun CoachTermCard(
    item: FootballCoachTerm,
    onSpeakCommand: () -> Unit
) {
    val tokens = BerlinTheme
    val catColor = when (item.category) {
        CoachTermCategory.OFFENSIV -> tokens.accent
        CoachTermCategory.DEFENSIV -> tokens.sky
        CoachTermCategory.UMSCHALTEN -> tokens.violet
        CoachTermCategory.KOMMUNIKATION -> Color(0xFFFFB020)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = tokens.surface,
        border = BorderStroke(1.dp, tokens.line)
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
                Text(
                    text = item.term,
                    fontFamily = BarlowCondensedFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.ink,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = catColor.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, catColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = item.category.label.uppercase(),
                        fontFamily = BarlowCondensedFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = catColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = item.tacticalMeaning,
                fontFamily = BarlowFontFamily,
                fontSize = 13.sp,
                color = tokens.ink2,
                lineHeight = 19.sp
            )

            // Sideline Command Box with TTS button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = tokens.surface2,
                border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "PLATZ-KOMMANDO",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.accentInk,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "„${item.pitchCommand}“",
                            fontFamily = BarlowFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.ink,
                            lineHeight = 18.sp
                        )
                    }
                    IconButton(
                        onClick = onSpeakCommand,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(tokens.accentSoft)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Kommando anhören",
                            tint = tokens.accentInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Avoid Denglisch / Negative Cue
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = tokens.coral,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Vermeiden: „${item.avoidInstead}“",
                    fontFamily = BarlowFontFamily,
                    fontSize = 11.5.sp,
                    color = tokens.coral
                )
            }
        }
    }
}

@Composable
private fun TacticalScenarioCard(
    scenario: TacticalScenario,
    selectedOption: Int?,
    onSelectOption: (Int) -> Unit,
    onSpeakCommand: () -> Unit
) {
    val tokens = BerlinTheme
    val hasAnswered = selectedOption != null
    val isCorrect = selectedOption == scenario.correctIndex

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = tokens.surface,
        border = BorderStroke(
            width = 1.dp,
            color = when {
                !hasAnswered -> tokens.line
                isCorrect -> tokens.ok
                else -> tokens.coral
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(99.dp),
                color = tokens.skySoft
            ) {
                Text(
                    text = scenario.matchPhase,
                    fontFamily = BarlowCondensedFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.sky,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Text(
                text = scenario.title,
                fontFamily = BarlowCondensedFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.ink
            )

            Text(
                text = scenario.situationDescription,
                fontFamily = BarlowFontFamily,
                fontSize = 13.sp,
                color = tokens.ink2,
                lineHeight = 19.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                scenario.options.forEachIndexed { idx, optText ->
                    val isThisSelected = selectedOption == idx
                    val isThisCorrect = idx == scenario.correctIndex
                    val borderColor = when {
                        !hasAnswered -> tokens.line
                        isThisCorrect -> tokens.ok
                        isThisSelected -> tokens.coral
                        else -> tokens.line
                    }
                    val bgColor = when {
                        !hasAnswered -> tokens.surface2
                        isThisCorrect -> tokens.okBg
                        isThisSelected -> tokens.coralSoft
                        else -> tokens.surface2
                    }

                    Surface(
                        onClick = { onSelectOption(idx) },
                        shape = RoundedCornerShape(14.dp),
                        color = bgColor,
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${('A' + idx)}.",
                                fontFamily = BarlowCondensedFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasAnswered && isThisCorrect) tokens.ok else tokens.accentInk
                            )
                            Text(
                                text = optText,
                                fontFamily = BarlowFontFamily,
                                fontSize = 13.sp,
                                color = tokens.ink,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = hasAnswered) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = tokens.surface2,
                    border = BorderStroke(1.dp, if (isCorrect) tokens.ok.copy(alpha = 0.5f) else tokens.coral.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isCorrect) "Taktisch stark gelöst!" else "Noch nicht optimal — DFB-Analyse:",
                            fontFamily = BarlowCondensedFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) tokens.ok else tokens.coral
                        )
                        Text(
                            text = scenario.coachingExplanation,
                            fontFamily = BarlowFontFamily,
                            fontSize = 12.5.sp,
                            color = tokens.ink2,
                            lineHeight = 18.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Zuruf: „${scenario.keyCommandToShout}“",
                                fontFamily = BarlowFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.accentInk,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onSpeakCommand, modifier = Modifier.size(40.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Zuruf anhören",
                                    tint = tokens.accent
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
private fun DrillCommunicationCard(
    drill: DrillCommunicationPattern,
    isCompleted: Boolean,
    onSpeakCommand: (String) -> Unit,
    onToggleComplete: () -> Unit
) {
    val tokens = BerlinTheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = tokens.surface,
        border = BorderStroke(1.dp, if (isCompleted) tokens.ok else tokens.line)
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
                    shape = RoundedCornerShape(99.dp),
                    color = tokens.violetSoft
                ) {
                    Text(
                        text = drill.ageGroupFocus,
                        fontFamily = BarlowCondensedFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.violet,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Übung abhaken",
                        tint = if (isCompleted) tokens.ok else tokens.ink2
                    )
                }
            }

            Text(
                text = drill.drillTitle,
                fontFamily = BarlowCondensedFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.ink
            )

            Text(
                text = drill.setupSummary,
                fontFamily = BarlowFontFamily,
                fontSize = 13.sp,
                color = tokens.ink2,
                lineHeight = 18.sp
            )

            Text(
                text = "LIVE-COACHING-KOMMANDOS (ANTIPPEN ZUM ANHÖREN)",
                fontFamily = BarlowCondensedFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.accentInk,
                letterSpacing = 0.7.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                drill.liveCommands.forEach { cmd ->
                    Surface(
                        onClick = { onSpeakCommand(cmd) },
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.surface2,
                        border = BorderStroke(1.dp, tokens.line),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Anhören",
                                tint = tokens.accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "„$cmd“",
                                fontFamily = BarlowFontFamily,
                                fontSize = 12.5.sp,
                                color = tokens.ink,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // Correction Formula
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = tokens.surface2,
                border = BorderStroke(1.dp, tokens.line)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Statt: „${drill.avoidPhrase}“",
                        fontFamily = BarlowFontFamily,
                        fontSize = 11.5.sp,
                        color = tokens.coral
                    )
                    Text(
                        text = "Besser (DFB-Coaching): „${drill.positiveCorrection}“",
                        fontFamily = BarlowFontFamily,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.ok,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
