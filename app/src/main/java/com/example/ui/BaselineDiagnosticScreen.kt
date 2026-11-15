package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.LearningPathMilestone
import com.example.data.model.SkillCategory
import com.example.ui.diagnostic.DiagnosticEngine
import com.example.ui.diagnostic.DiagnosticTaskType
import com.example.ui.diagnostic.DiagnosticUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaselineDiagnosticScreen(
    viewModel: C1ViewModel,
    diagnosticEngine: DiagnosticEngine = viewModel(),
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val uiState by diagnosticEngine.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MCQ-Übungsscreening",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BarlowCondensedFontFamily,
                            color = BerlinTheme.ink
                        )
                        Text(
                            text = "Kurze Übung • keine GER-/CEFR-Einstufung",
                            fontSize = 12.sp,
                            color = BerlinTheme.ink2
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("diagnostic_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = BerlinTheme.ink)
                    }
                },
                actions = {
                    if (uiState is DiagnosticUiState.Completed) {
                        IconButton(
                            onClick = { diagnosticEngine.startGrammarVocabAssessment() },
                            modifier = Modifier.testTag("action_restart_diagnostic")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Neue Übung starten", tint = BerlinTheme.accent)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BerlinTheme.surface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BerlinTheme.bg)
        ) {
            when (val state = uiState) {
                is DiagnosticUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BerlinTheme.accent)
                    }
                }

                is DiagnosticUiState.Evaluating -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = BerlinTheme.accent)
                            Text(
                                text = "Auswahlantworten werden ausgewertet…",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = BarlowCondensedFontFamily,
                                color = BerlinTheme.ink
                            )
                        }
                    }
                }

                is DiagnosticUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = BerlinTheme.coral, modifier = Modifier.size(48.dp))
                            Text(text = state.message, color = BerlinTheme.coral, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { diagnosticEngine.startGrammarVocabAssessment() },
                                modifier = Modifier.height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BerlinTheme.accent, contentColor = BerlinTheme.onAccent)
                            ) {
                                Text("Erneut versuchen", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                is DiagnosticUiState.Idle -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .testTag("diagnostic_idle_scroll"),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Main Intro Card (24dp radius, Berlin tokens)
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                color = BerlinTheme.surface,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = BerlinTheme.accentSoft,
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Assessment,
                                                    contentDescription = null,
                                                    tint = BerlinTheme.accentInk,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = "KURZES MCQ-ÜBUNGSSCREENING",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                fontFamily = BarlowCondensedFontFamily,
                                                color = BerlinTheme.ink
                                            )
                                            Text(
                                                text = "Begrenzte Stichprobe, kein Kompetenznachweis",
                                                fontSize = 12.sp,
                                                color = BerlinTheme.ink2
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Diese kurze Multiple-Choice-Übung prüft das Erkennen vorgegebener Antworten. Sie liefert keine GER-/CEFR-Einstufung und misst weder Sprechen noch Schreiben, Interaktion, Mediation oder Aussprache/Prosodie.",
                                        fontSize = 13.sp,
                                        color = BerlinTheme.ink2,
                                        lineHeight = 19.sp
                                    )

                                    Text(
                                        text = "Nicht beurteilte Bereiche bleiben bei 0 (keine Evidenz, nicht fehlende Fähigkeit). Ergebnisse gelten nur für diese Sitzung; vorhandene Baselines bleiben unverändert. Ihr evidenzbasiertes Lernprogramm finden Sie auf der Startseite.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BerlinTheme.accentInk
                                    )
                                }
                            }
                        }

                        // Option 1: Focused C1 Grammar & Vocabulary Test (Primary)
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_mode_grammar_vocab"),
                                shape = RoundedCornerShape(24.dp),
                                color = BerlinTheme.surface,
                                border = BorderStroke(2.dp, BerlinTheme.accent),
                                shadowElevation = 3.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(99.dp),
                                            color = BerlinTheme.accentSoft
                                        ) {
                                            Text(
                                                text = "10 MULTIPLE-CHOICE-AUFGABEN",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = BerlinTheme.accentInk,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = "ca. 5 Minuten",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BerlinTheme.ink2
                                        )
                                    }

                                    Text(
                                        text = "Grammatik- & Wortschatz-Erkennung",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        fontFamily = BarlowCondensedFontFamily,
                                        color = BerlinTheme.ink
                                    )

                                    Text(
                                        text = "Prüft Passiversatzformen (sich lassen / sein+zu), Partizipialkonstruktionen, Konjunktiv I, feste Nomen-Verb-Verbindungen, gehobene Synonymik und Registerkontrolle.",
                                        fontSize = 13.sp,
                                        color = BerlinTheme.ink2,
                                        lineHeight = 18.sp
                                    )

                                    Button(
                                        onClick = { diagnosticEngine.startGrammarVocabAssessment() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("button_start_initial_diagnostic"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BerlinTheme.accent,
                                            contentColor = BerlinTheme.onAccent
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp), tint = BerlinTheme.onAccent)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Grammatik- & Wortschatz-Übung starten ›", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        // Option 2: Full 8-Competency Diagnostic
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_mode_full_competency"),
                                shape = RoundedCornerShape(24.dp),
                                color = BerlinTheme.surface,
                                border = BorderStroke(1.dp, BerlinTheme.line)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Gemischte MCQ-Übung",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            fontFamily = BarlowCondensedFontFamily,
                                            color = BerlinTheme.ink
                                        )
                                        Text(
                                            text = "8 Aufgaben • ca. 8 Min.",
                                            fontSize = 11.sp,
                                            color = BerlinTheme.ink2
                                        )
                                    }

                                    Text(
                                        text = "Acht Auswahlaufgaben, darunter Textverständnis und synthetisch vorgelesene Beispiele. Formulierungen werden nur ausgewählt, nicht selbst produziert. Kein vollständiges Fertigkeitsprofil und kein authentischer Dialekttest.",
                                        fontSize = 12.sp,
                                        color = BerlinTheme.ink2,
                                        lineHeight = 17.sp
                                    )

                                    OutlinedButton(
                                        onClick = { diagnosticEngine.startFullAssessment() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("button_start_full_diagnostic"),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(2.dp, BerlinTheme.line),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BerlinTheme.ink)
                                    ) {
                                        Text("Gemischte MCQ-Übung starten ›", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                is DiagnosticUiState.InProgress -> {
                    val currentTask = state.currentTask
                    val selectedOption = state.answers[state.currentStep]?.selectedOptionIndex
                    val isGrammar = currentTask.skillCategory == SkillCategory.GRAMMATIK
                    val categoryColor = if (isGrammar) BerlinTheme.sky else BerlinTheme.violet
                    val categorySoft = if (isGrammar) BerlinTheme.skySoft else BerlinTheme.violetSoft

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("diagnostic_in_progress_scroll"),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Progress Header Bar
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                            shape = RoundedCornerShape(99.dp),
                                            color = categorySoft
                                        ) {
                                            Text(
                                                text = "${currentTask.skillCategory.titleDe} • MCQ",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = BarlowCondensedFontFamily,
                                                color = categoryColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Text(
                                            text = "Frage ${state.currentStep + 1} von ${state.totalSteps}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BerlinTheme.ink
                                        )
                                    }
                                    Text(
                                        text = "${((state.currentStep + 1) * 100) / state.totalSteps}%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BarlowCondensedFontFamily,
                                        color = BerlinTheme.accentInk
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { (state.currentStep + 1) / state.totalSteps.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = BerlinTheme.accent,
                                    trackColor = BerlinTheme.line
                                )
                            }
                        }

                        // Task Question Card (24dp radius)
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                color = BerlinTheme.surface,
                                border = BorderStroke(1.dp, BerlinTheme.line),
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Subtopic Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentTask.headlineDe,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            fontFamily = BarlowCondensedFontFamily,
                                            color = BerlinTheme.ink,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // Context Excerpt Box
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = BerlinTheme.surface2,
                                        border = BorderStroke(1.dp, BerlinTheme.line),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = null,
                                                tint = BerlinTheme.accentInk,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = currentTask.contextDe,
                                                fontSize = 12.sp,
                                                color = BerlinTheme.ink2,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    // Spoken Audio playback if present
                                    if (currentTask.spokenAudioText != null) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = BerlinTheme.surface2,
                                            border = BorderStroke(1.dp, BerlinTheme.line),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                IconButton(
                                                    onClick = { diagnosticEngine.playTaskAudio(currentTask) },
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .background(BerlinTheme.accent, CircleShape)
                                                        .testTag("diag_play_audio")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = "Audio abspielen",
                                                        tint = BerlinTheme.onAccent
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "Hörprobe abspielen (${currentTask.speechSpeed}x)",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = BerlinTheme.ink
                                                    )
                                                    if (currentTask.dialectNote != null) {
                                                        Text(
                                                            text = currentTask.dialectNote,
                                                            fontSize = 11.sp,
                                                            color = BerlinTheme.ink2
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Nur Antwortauswahl; keine Messung produktiver Fertigkeiten oder GER-/CEFR-Einstufung.",
                                        fontSize = 12.sp,
                                        color = BerlinTheme.ink2
                                    )
                                    if (currentTask.taskType == DiagnosticTaskType.RECORDED_SPEAKING ||
                                        currentTask.taskType == DiagnosticTaskType.INTERACTION_DIALOGUE) {
                                        OutlinedButton(
                                            onClick = {},
                                            enabled = false,
                                            modifier = Modifier.fillMaxWidth().testTag("diagnostic_recording_unavailable")
                                        ) {
                                            Text("Sprachaufnahme und Bewertung nicht verfügbar")
                                        }
                                    }

                                    // Prompt Text
                                    Text(
                                        text = currentTask.promptText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BerlinTheme.ink,
                                        lineHeight = 21.sp
                                    )

                                    // Multiple Choice Option Rows (.opt style: 2dp border, 16dp radius, min-height 56dp)
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        currentTask.multipleChoiceOptions.forEachIndexed { optIndex, option ->
                                            val isSelected = selectedOption == optIndex
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = if (isSelected) BerlinTheme.accentSoft else BerlinTheme.surface,
                                                border = if (isSelected) BorderStroke(2.dp, BerlinTheme.accent) else BorderStroke(2.dp, BerlinTheme.line),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .defaultMinSize(minHeight = 56.dp)
                                                    .clickable { diagnosticEngine.recordAnswer(state.currentStep, optIndex) }
                                                    .testTag("diag_option_${optIndex}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(14.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    RadioButton(
                                                        selected = isSelected,
                                                        onClick = { diagnosticEngine.recordAnswer(state.currentStep, optIndex) },
                                                        colors = RadioButtonDefaults.colors(
                                                            selectedColor = BerlinTheme.accent,
                                                            unselectedColor = BerlinTheme.ink2
                                                        )
                                                    )
                                                    Text(
                                                        text = option,
                                                        fontSize = 14.sp,
                                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                        color = BerlinTheme.ink,
                                                        lineHeight = 19.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Action Navigation Buttons (54dp height, 16dp radius, teal container)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        if (state.currentStep > 0) {
                                            OutlinedButton(
                                                onClick = { diagnosticEngine.previousTask() },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(54.dp),
                                                shape = RoundedCornerShape(16.dp),
                                                border = BorderStroke(2.dp, BerlinTheme.line),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BerlinTheme.ink)
                                            ) {
                                                Text("‹ Zurück", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                            }
                                        }

                                        Button(
                                            onClick = { diagnosticEngine.nextTask() },
                                            enabled = selectedOption != null,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(54.dp)
                                                .testTag("button_diagnostic_next"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BerlinTheme.accent,
                                                contentColor = BerlinTheme.onAccent,
                                                disabledContainerColor = BerlinTheme.line,
                                                disabledContentColor = BerlinTheme.ink2
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text(
                                                text = if (state.currentStep == state.totalSteps - 1) "Übung auswerten" else "Weiter ›",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is DiagnosticUiState.Completed -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("diagnostic_completed_scroll"),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Success & Status Banner (24dp radius, ok green)
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("diagnostic_result_card"),
                                shape = RoundedCornerShape(24.dp),
                                color = BerlinTheme.surface,
                                border = BorderStroke(2.dp, BerlinTheme.ok),
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
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
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BerlinTheme.ok)
                                            Text(
                                                text = "ÜBUNG AUSGEWERTET",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                fontFamily = BarlowCondensedFontFamily,
                                                color = BerlinTheme.ok
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(99.dp),
                                            color = BerlinTheme.okBg
                                        ) {
                                            Text(
                                                text = "Nur diese Sitzung",
                                                fontSize = 11.sp,
                                                color = BerlinTheme.ok,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    // Dual Index Cards: Grammar & Vocabulary Proficiency
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Grammar Score Card (Sky Blue)
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = BerlinTheme.surface2,
                                            border = BorderStroke(1.dp, BerlinTheme.line),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(text = "Grammatik: MCQ-Anteil richtig", fontSize = 11.sp, color = BerlinTheme.ink2)
                                                Row(verticalAlignment = Alignment.Bottom) {
                                                    Text(
                                                        text = if (SkillCategory.GRAMMATIK in state.assessedSkills) "${state.grammarScore}" else "—",
                                                        fontSize = 28.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = BarlowCondensedFontFamily,
                                                        color = BerlinTheme.sky
                                                    )
                                                    Text(text = " / 100", fontSize = 12.sp, color = BerlinTheme.ink2, modifier = Modifier.padding(bottom = 3.dp))
                                                }
                                                LinearProgressIndicator(
                                                    progress = { state.grammarScore / 100f },
                                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                                    color = BerlinTheme.sky,
                                                    trackColor = BerlinTheme.line
                                                )
                                                Text(
                                                    text = if (SkillCategory.GRAMMATIK in state.assessedSkills) "Nur Auswahlantworten, kein GER-Niveau" else "Nicht beurteilt (intern 0)",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BerlinTheme.sky
                                                )
                                            }
                                        }

                                        // Vocabulary Score Card (Violet)
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = BerlinTheme.surface2,
                                            border = BorderStroke(1.dp, BerlinTheme.line),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(text = "Wortschatz: MCQ-Anteil richtig", fontSize = 11.sp, color = BerlinTheme.ink2)
                                                Row(verticalAlignment = Alignment.Bottom) {
                                                    Text(
                                                        text = if (SkillCategory.WORTSCHATZ in state.assessedSkills) "${state.vocabularyScore}" else "—",
                                                        fontSize = 28.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = BarlowCondensedFontFamily,
                                                        color = BerlinTheme.violet
                                                    )
                                                    Text(text = " / 100", fontSize = 12.sp, color = BerlinTheme.ink2, modifier = Modifier.padding(bottom = 3.dp))
                                                }
                                                LinearProgressIndicator(
                                                    progress = { state.vocabularyScore / 100f },
                                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                                    color = BerlinTheme.violet,
                                                    trackColor = BerlinTheme.line
                                                )
                                                Text(
                                                    text = if (SkillCategory.WORTSCHATZ in state.assessedSkills) "Nur Auswahlantworten, kein GER-Niveau" else "Nicht beurteilt (intern 0)",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BerlinTheme.violet
                                                )
                                            }
                                        }
                                    }

                                    // Diagnosed Primary Bottleneck Callout (.alert style: Coral Soft)
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = BerlinTheme.coralSoft,
                                        border = BorderStroke(1.dp, BerlinTheme.coral.copy(alpha = 0.35f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = BerlinTheme.coral, modifier = Modifier.size(24.dp))
                                            Column {
                                                Text(
                                                    text = "KEINE ENGPASSDIAGNOSE",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = BarlowCondensedFontFamily,
                                                    color = BerlinTheme.coral
                                                )
                                                Text(
                                                    text = state.primaryLimitingFactor,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BerlinTheme.ink
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = state.summaryDe,
                                modifier = Modifier.testTag("diagnostic_limitations"),
                                fontSize = 13.sp,
                                color = BerlinTheme.ink2
                            )
                        }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Bereiche dieser MCQ-Stichprobe", fontWeight = FontWeight.Bold, color = BerlinTheme.ink)
                                state.nineDimensionalProfile.forEach { (category, score) ->
                                    if (category in state.assessedSkills) {
                                        DiagnosticTopicRow(name = "${category.titleDe} (nur MCQ)", score = score.score)
                                    } else {
                                        Text(
                                            text = "${category.titleDe}: nicht beurteilt (0 = keine Evidenz)",
                                            fontSize = 12.sp,
                                            color = BerlinTheme.ink2
                                        )
                                    }
                                }
                                Text(
                                    text = "Kein Gesamt-C1-Wert, keine Alltagsbereitschaft und keine validierte Konfidenz ableitbar. Das evidenzbasierte Lernprogramm bleibt unverändert.",
                                    fontSize = 12.sp,
                                    color = BerlinTheme.ink2
                                )
                            }
                        }

                        // 3. Granular Topic Competence Matrix
                        if (state.grammarTopicScores.isNotEmpty() || state.vocabTopicScores.isNotEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    color = BerlinTheme.surface,
                                    border = BorderStroke(1.dp, BerlinTheme.line)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "BEANTWORTETE MCQ-STICHPROBEN",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            fontFamily = BarlowCondensedFontFamily,
                                            color = BerlinTheme.ink
                                        )

                                        (state.grammarTopicScores + state.vocabTopicScores).forEach { (topic, score) ->
                                            DiagnosticTopicRow(name = topic, score = score)
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Primary CTA: Activate & Start Personalized Training (54dp height, 16dp radius, teal container)
                        item {
                            Button(
                                onClick = {
                                    viewModel.navigateTo(Screen.Home)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("btn_finish_diagnostic"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BerlinTheme.accent,
                                    contentColor = BerlinTheme.onAccent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp), tint = BerlinTheme.onAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Zum evidenzbasierten Lernprogramm ›", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneRow(milestone: LearningPathMilestone) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (milestone.isPrimaryFocus) BerlinTheme.accentSoft else BerlinTheme.surface2,
        border = BorderStroke(
            if (milestone.isPrimaryFocus) 2.dp else 1.dp,
            if (milestone.isPrimaryFocus) BerlinTheme.accent else BerlinTheme.line
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (milestone.isPrimaryFocus) BerlinTheme.accent else BerlinTheme.line,
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${milestone.stage}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (milestone.isPrimaryFocus) BerlinTheme.onAccent else BerlinTheme.ink2
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = milestone.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BerlinTheme.ink
                    )
                    if (milestone.isPrimaryFocus) {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = BerlinTheme.accent
                        ) {
                            Text(
                                text = "START-FOKUS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BerlinTheme.onAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = milestone.targetArea,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (milestone.isPrimaryFocus) BerlinTheme.accentInk else BerlinTheme.ink2
                )
                Text(
                    text = milestone.focusDescription,
                    fontSize = 11.sp,
                    color = BerlinTheme.ink2,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun DiagnosticTopicRow(name: String, score: Int) {
    val isGood = score >= 75
    val rowColor = if (isGood) BerlinTheme.ok else BerlinTheme.coral
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 13.sp, color = BerlinTheme.ink, modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { (score / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .width(80.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = rowColor,
                trackColor = BerlinTheme.line
            )
            Text(
                text = "$score%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = rowColor
            )
        }
    }
}
