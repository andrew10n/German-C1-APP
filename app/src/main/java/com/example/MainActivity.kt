package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.programme.ProgrammeViewModel
import com.example.ui.AdaptiveSessionScreen
import com.example.ui.BaselineDiagnosticScreen
import com.example.ui.C1ViewModel
import com.example.ui.ConversationSimulatorScreen
import com.example.ui.ErrorIntelligenceScreen
import com.example.ui.FootballCoachTrackScreen
import com.example.ui.HomeScreen
import com.example.ui.IntelligenceProfileScreen
import com.example.ui.ListeningLabScreen
import com.example.ui.MediationScreen
import com.example.ui.Screen
import com.example.ui.SpeakingPracticeScreen
import com.example.ui.theme.C1DeutschTheme
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SlateNavyBorder
import com.example.ui.theme.SlateNavyDark
import com.example.ui.theme.SlateNavySurface

class MainActivity : ComponentActivity() {

    private val viewModel: C1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure Room database and repositories are pre-initialized before UI composition
        (application as? MainApplication)?.let { app ->
            app.database
        }
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()
            val programmeViewModel: ProgrammeViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel()
            val preferences by programmeViewModel.preferences.collectAsState()
            C1DeutschTheme(darkTheme = isDark, palette = preferences.theme) {
                MainAppShell(viewModel = viewModel, programmeViewModel = programmeViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(viewModel: C1ViewModel, programmeViewModel: ProgrammeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val preferences by programmeViewModel.preferences.collectAsState()
    val language = preferences.language
    Scaffold(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                is Screen.Home -> com.example.programme.ProgrammeScreen(
                    viewModel = programmeViewModel,
                    onLegacyPractice = { viewModel.navigateTo(Screen.LegacyHome) },
                    onOpenIntelligenceProfile = { viewModel.navigateTo(Screen.IntelligenceProfile) },
                    onOpenSpeakingLab = { viewModel.navigateTo(Screen.SpeakingLab) },
                    onOpenConversation = { viewModel.navigateTo(Screen.ConversationSimulator) },
                    onOpenMediation = { viewModel.navigateTo(Screen.Mediation) }
                )
                is Screen.LegacyHome -> {
                    BackHandler { viewModel.navigateBack() }
                    Column {
                        androidx.compose.material3.TopAppBar(
                            title = { Text(language.text("Üben", "Practice")) },
                            navigationIcon = {
                                androidx.compose.material3.IconButton(
                                    onClick = { viewModel.navigateBack() }
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = language.text(
                                            "Zurück zum Programm",
                                            "Back to program"
                                        )
                                    )
                                }
                            }
                        )
                        androidx.compose.material3.Text(
                            language.text(
                                "Zusätzliche Übungstools für Hören, Sprechen, Fehler und Auswertung.",
                                "Extra tools for listening, speaking, error review and reflection."
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        HomeScreen(viewModel = viewModel)
                    }
                }
                is Screen.SessionRunner -> AdaptiveSessionScreen(viewModel = viewModel)
                is Screen.ListeningLab -> ListeningLabScreen(viewModel = viewModel)
                is Screen.SpeakingLab -> SpeakingPracticeScreen(viewModel = viewModel)
                is Screen.ConversationSimulator -> ConversationSimulatorScreen(viewModel = viewModel)
                is Screen.ErrorIntelligence -> ErrorIntelligenceScreen(viewModel = viewModel)
                is Screen.IntelligenceProfile -> IntelligenceProfileScreen(viewModel = viewModel)
                is Screen.Mediation -> MediationScreen(viewModel = viewModel)
                is Screen.BaselineDiagnostic -> BaselineDiagnosticScreen(viewModel = viewModel)
                is Screen.FootballCoachTrack -> FootballCoachTrackScreen(viewModel = viewModel)
            }
        }
    }
}
