package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class NavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(viewModel: C1ViewModel, programmeViewModel: ProgrammeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val preferences by programmeViewModel.preferences.collectAsState()
    val language = preferences.language

    val navItems = listOf(
        NavItem(Screen.Home, language.text("Programm", "Program"), Icons.Default.Home, "nav_home"),
        NavItem(Screen.LegacyHome, language.text("Üben", "Practice"), Icons.AutoMirrored.Filled.MenuBook, "nav_practice"),
        NavItem(Screen.IntelligenceProfile, language.text("Profil", "Profile"), Icons.Default.Insights, "nav_profile")
    )

    // Keep focused tools distraction-free; their top-bar back action is the only navigation there.
    val showBottomBar = currentScreen is Screen.Home ||
        currentScreen is Screen.LegacyHome ||
        currentScreen is Screen.IntelligenceProfile

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 840.dp
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar && !useNavigationRail) {
                // Berlin Floating Dock: 30dp radius, 6dp padding, active dark pill with teal icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("main_bottom_nav"),
                        shape = RoundedCornerShape(30.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shadowElevation = 10.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            navItems.forEach { item ->
                                val isSelected = isNavigationItemSelected(currentScreen, item.screen)
                                Surface(
                                    onClick = {
                                        if (!isSelected) {
                                            viewModel.speechHelper.stop()
                                            viewModel.navigateTo(item.screen)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(item.tag),
                                    shape = RoundedCornerShape(24.dp),
                                    color = if (isSelected) com.example.ui.theme.BerlinTheme.hero else Color.Transparent
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = if (isSelected) com.example.ui.theme.BerlinTheme.accent else com.example.ui.theme.BerlinTheme.ink2,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.title,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = com.example.ui.theme.BarlowFontFamily,
                                            color = if (isSelected) com.example.ui.theme.BerlinTheme.onHero else com.example.ui.theme.BerlinTheme.ink2,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        ) { innerPadding ->
            Row(Modifier.fillMaxSize()) {
                if (showBottomBar && useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("main_navigation_rail")
                    ) {
                        navItems.forEach { item ->
                            val isSelected = isNavigationItemSelected(currentScreen, item.screen)
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        viewModel.speechHelper.stop()
                                        viewModel.navigateTo(item.screen)
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(item.title) },
                                alwaysShowLabel = true,
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(innerPadding)
                ) {
            when (currentScreen) {
                is Screen.Home -> com.example.programme.ProgrammeScreen(
                    viewModel = programmeViewModel,
                    onLegacyPractice = { viewModel.navigateTo(Screen.LegacyHome) },
                    onOpenListeningLab = { viewModel.navigateTo(Screen.ListeningLab) },
                    onOpenSpeakingLab = { viewModel.navigateTo(Screen.SpeakingLab) },
                    onOpenConversation = { viewModel.navigateTo(Screen.ConversationSimulator) }
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
    }
}

private fun isNavigationItemSelected(current: Screen, destination: Screen): Boolean =
    current::class == destination::class ||
        (destination is Screen.LegacyHome && (
            current is Screen.ListeningLab ||
                current is Screen.SpeakingLab ||
                current is Screen.ConversationSimulator ||
                current is Screen.ErrorIntelligence ||
                current is Screen.Mediation ||
                current is Screen.FootballCoachTrack
            ))
