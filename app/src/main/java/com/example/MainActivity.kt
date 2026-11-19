package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ai.configureAppCheck
import com.example.navigation.GermanC1Navigator
import com.example.ui.ArenaMode
import com.example.ui.MainViewModel
import com.example.ui.screens.AiC1StudioScreen
import com.example.ui.screens.ControllerCenterScreen
import com.example.ui.screens.DailySessionScreen
import com.example.ui.screens.DiagnosticArenaScreen
import com.example.ui.screens.RepoUnitStudioScreen
import com.example.ui.screens.RoadmapScreen
import com.example.ui.screens.VisualLessonScreen
import com.example.ui.theme.GermanC1Theme
import com.example.ui.theme.LocalVibePalette
import com.example.ui.theme.contentColorOnAccent

const val ROUTE_HUB = "hub"
const val ROUTE_ROADMAP = "roadmap"
const val ROUTE_CONTROLLER = "controller"
const val ROUTE_ARENA = "arena"
const val ROUTE_AI_STUDIO = "ai_studio"
const val ROUTE_REPO_UNIT = "unit/{unitId}"
const val ROUTE_LESSON = "lesson/{moduleId}"

private data class NavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

/**
 * Visual 3-tab learning architecture:
 * 1. Learn — One clear, focused daily lesson session.
 * 2. Journey — A visual learning path through the C1 curriculum.
 * 3. Progress — Personal readiness, settings, and adaptation controls.
 */
private val TopDestinations = listOf(
    NavDestination(
        route = ROUTE_HUB,
        label = "Learn",
        selectedIcon = Icons.Filled.PlayCircle,
        unselectedIcon = Icons.Outlined.PlayCircle,
        testTag = "nav_hub"
    ),
    NavDestination(
        route = ROUTE_ROADMAP,
        label = "Journey",
        selectedIcon = Icons.Filled.Route,
        unselectedIcon = Icons.Outlined.Route,
        testTag = "nav_roadmap"
    ),
    NavDestination(
        route = ROUTE_CONTROLLER,
        label = "Progress",
        selectedIcon = Icons.Filled.Speed,
        unselectedIcon = Icons.Outlined.Speed,
        testTag = "nav_engine"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory Step 1: Configure App Check with launch intent before UI or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            val rootViewModel: MainViewModel = viewModel()
            val vibeTheme by rootViewModel.vibeTheme.collectAsStateWithLifecycle()
            GermanC1Theme(vibeTheme = vibeTheme) {
                GermanC1AppRoot(viewModel = rootViewModel)
            }
        }
    }
}

@Composable
fun GermanC1AppRoot(
    viewModel: MainViewModel = viewModel()
) {
    // Simplified state collection - reduced from 15+ properties to essential ones
    val controlState by viewModel.controlState.collectAsStateWithLifecycle()
    val vibeTheme by viewModel.vibeTheme.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navigator = GermanC1Navigator(navController, viewModel)

    val state = controlState
    if (state == null) {
        Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val palette = LocalVibePalette.current
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val isFocusedSubScreen = navigator.isFocusedSubScreen(currentRoute)

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = palette.canvasBg,
            bottomBar = {
                if (!useNavigationRail && !isFocusedSubScreen) {
                    NavigationBar(
                        containerColor = palette.cardSurface
                    ) {
                        val selectedFg = contentColorOnAccent(palette.primaryAccent)
                        TopDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigator.openSubScreen(dest.route) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = selectedFg,
                                    selectedTextColor = palette.primaryAccent,
                                    indicatorColor = palette.primaryAccent,
                                    unselectedIconColor = palette.textSecondary,
                                    unselectedTextColor = palette.textSecondary
                                ),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail && !isFocusedSubScreen) {
                    NavigationRail(
                        containerColor = palette.cardSurface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        val selectedFg = contentColorOnAccent(palette.primaryAccent)
                        TopDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigator.openSubScreen(dest.route) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = selectedFg,
                                    selectedTextColor = palette.primaryAccent,
                                    indicatorColor = palette.primaryAccent,
                                    unselectedIconColor = palette.textSecondary,
                                    unselectedTextColor = palette.textSecondary
                                ),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = ROUTE_HUB,
                    enterTransition = {
                        fadeIn(tween(320, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(tween(340, easing = FastOutSlowInEasing)) { it / 10 } +
                            scaleIn(initialScale = 0.97f, animationSpec = tween(320))
                    },
                    exitTransition = {
                        fadeOut(tween(240)) +
                            slideOutHorizontally(tween(260)) { -it / 12 } +
                            scaleOut(targetScale = 0.98f, animationSpec = tween(240))
                    },
                    popEnterTransition = {
                        fadeIn(tween(320, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(tween(340, easing = FastOutSlowInEasing)) { -it / 10 }
                    },
                    popExitTransition = {
                        fadeOut(tween(240)) +
                            slideOutHorizontally(tween(260)) { it / 10 }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    composable(ROUTE_HUB) {
                        DailySessionScreen(
                            controlState = state,
                            selectedVibeTheme = vibeTheme,
                            onSelectVibeTheme = { theme -> viewModel.selectVibeTheme(theme) },
                            onBeatCompleted = { exId, beat ->
                                viewModel.recordPersonalizedBeat(exId, beat)
                            },
                            onEvaluateSpontaneous = { exercise, reply ->
                                viewModel.evaluateSpontaneousExercise(exercise, reply)
                            },
                            onResetSpontaneousEval = {
                                viewModel.resetSpontaneousEval()
                            },
                            onRateSrsCard = { card, quality ->
                                viewModel.rateSrsCard(card, quality)
                            },
                            onRecordRepoStep = { uId, stepType, boost ->
                                viewModel.recordRepoUnitStep(uId, stepType, boost)
                            },
                            onRecordMediaMission = { missionId, ok ->
                                viewModel.recordMediaMissionCompleted(missionId, ok)
                            },
                            onSubmitQuizProbe = { item, selectedIndex, responseTimeMs, markCalibrated ->
                                viewModel.submitEmbeddedQuizProbe(
                                    item = item,
                                    selectedIndex = selectedIndex,
                                    responseTimeMs = responseTimeMs,
                                    markCalibrated = markCalibrated
                                )
                            },
                            onEvaluateSituationReply = { title, prompt, obj, reply, pillar ->
                                viewModel.evaluateSituationReply(title, prompt, obj, reply, pillar)
                            },
                            onResetSituationEval = {
                                viewModel.resetSituationEval()
                            },
                            onConcludeStint = { completedCount ->
                                viewModel.concludeStintAndAdapt(completedCount)
                            },
                            onAdvanceStintCycle = {
                                viewModel.advanceStintCycle()
                            },
                            onOpenEngineSettings = {
                                navigator.navigateToController()
                            }
                        )
                    }

                    composable(ROUTE_ROADMAP) {
                        RoadmapScreen(
                            controlState = state,
                            onOpenUnitStudio = { unitId ->
                                viewModel.resetSituationEval()
                                navigator.openSubScreen("unit/$unitId")
                            },
                            onStartUnitDrill = { pillar ->
                                viewModel.startArenaSession(
                                    mode = ArenaMode.PINNED_PILLAR,
                                    pinnedPillar = pillar
                                )
                                navigator.openSubScreen(ROUTE_ARENA)
                            },
                            onPinPillarToEngine = { pillar ->
                                val alreadyPinned = state.commanderConfig.commanderEnabled &&
                                    state.commanderConfig.pinnedFocusPillar == pillar
                                viewModel.pinFocusPillarInCommander(if (alreadyPinned) null else pillar)
                            },
                            onOpenCalibration = {
                                viewModel.startArenaSession(ArenaMode.CALIBRATION_SWEEP)
                                navigator.openSubScreen(ROUTE_ARENA)
                            },
                            onOpenAiStudio = { navigator.openSubScreen(ROUTE_AI_STUDIO) }
                        )
                    }

                    composable(ROUTE_CONTROLLER) {
                        ControllerCenterScreen(
                            controlState = state,
                            selectedVibeTheme = vibeTheme,
                            onSelectVibeTheme = { theme -> viewModel.selectVibeTheme(theme) },
                            onRunAiOptimizer = { viewModel.runAiCurriculumOptimizer() },
                            onRunParetoPruneSweep = { viewModel.runParetoPruneSweep() },
                            onRestorePrunedItem = { id -> viewModel.restorePrunedItem(id) },
                            onLaunchMistakeRepair = {
                                navigator.navigateToHub()
                            },
                            onSelectStrategy = { mode -> viewModel.setStrategyMode(mode) },
                            onUpdateDailyGoal = { mins -> viewModel.setDailyGoal(mins) },
                            onSelectContextTrack = { track -> viewModel.selectContextTrack(track) },
                            onStartCalibration = {
                                viewModel.startArenaSession(ArenaMode.CALIBRATION_SWEEP)
                                navigator.openSubScreen(ROUTE_ARENA)
                            },
                            onJumpToPillarModule = { pillar ->
                                val targetUnit = state.prioritizedRepoUnits
                                    .firstOrNull { it.unit.mappedPillar == pillar }?.unit
                                    ?: state.orderedModules.firstOrNull { it.module.pillar == pillar }?.module
                                        ?: state.orderedModules.first().module
                                if (targetUnit != null) {
                                    viewModel.resetSituationEval()
                                    navigator.openSubScreen("unit/${targetUnit.id}")
                                } else {
                                    val mod = state.orderedModules.firstOrNull { it.module.pillar == pillar }?.module
                                        ?: state.orderedModules.first().module
                                    navigator.openSubScreen("lesson/${mod.id}")
                                }
                            },
                            onOpenAiStudio = { navigator.openSubScreen(ROUTE_AI_STUDIO) },
                            onResetTelemetry = { viewModel.resetDiagnostics() },
                            onSetCommanderEnabled = { enabled -> viewModel.setCommanderEnabled(enabled) },
                            onPinFocusPillar = { pillar -> viewModel.pinFocusPillarInCommander(pillar) },
                            onToggleBeatType = { beatType -> viewModel.toggleBeatTypeInCommander(beatType) },
                            onSetAudioSpeed = { speed -> viewModel.setCommanderAudioSpeed(speed) },
                            onSetDifficultyOffset = { offset -> viewModel.setCommanderDifficultyOffset(offset) },
                            onResetCommanderAutopilot = { viewModel.resetCommanderToAutopilot() }
                        )
                    }

                    composable(
                        route = ROUTE_REPO_UNIT,
                        arguments = listOf(navArgument("unitId") { type = NavType.StringType })
                    ) { backStack ->
                        val unitId = backStack.arguments?.getString("unitId") ?: "s0-profile"
                        val repoUnit = viewModel.getRepoUnitById(unitId)
                        if (repoUnit != null) {
                            RepoUnitStudioScreen(
                                unit = repoUnit,
                                activeContextTrack = state.selectedContextTrack,
                                onSelectContextTrack = { track -> viewModel.selectContextTrack(track) },
                                onStepCompleted = { uId, stepType, boost ->
                                    viewModel.recordRepoUnitStep(uId, stepType, boost)
                                },
                                onEvaluateSituationReply = { title, prompt, obj, reply, pillar ->
                                    viewModel.evaluateSituationReply(title, prompt, obj, reply, pillar)
                                },
                                onResetSituationEval = { viewModel.resetSituationEval() },
                                onLaunchPillarDrill = { pillar ->
                                    viewModel.startArenaSession(
                                        mode = ArenaMode.PINNED_PILLAR,
                                        pinnedPillar = pillar
                                    )
                                    navController.popBackStack()
                                    navigator.openSubScreen(ROUTE_ARENA)
                                },
                                onBack = { navigator.popOrGoHome() }
                            )
                        }
                    }

                    composable(
                        route = ROUTE_LESSON,
                        arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
                    ) { backStack ->
                        val moduleId = backStack.arguments?.getString("moduleId")
                        val module = moduleId?.let { viewModel.getModuleById(it) }
                            ?: state.orderedModules.first().module
                        VisualLessonScreen(
                            module = module,
                            onSlideViewed = { pillar, idx, cpCorrect ->
                                viewModel.recordSlideSeen(pillar, idx, cpCorrect)
                            },
                            onLaunchPillarDrill = { pillar ->
                                viewModel.startArenaSession(
                                    mode = ArenaMode.PINNED_PILLAR,
                                    pinnedPillar = pillar
                                )
                                navController.popBackStack()
                                navigator.openSubScreen(ROUTE_ARENA)
                            },
                            onBack = { navigator.popOrGoHome() }
                        )
                    }

                    composable(ROUTE_ARENA) {
                        DiagnosticArenaScreen(
                            controlState = state,
                            onStartSession = { mode -> viewModel.startArenaSession(mode) },
                            onSelectOption = { idx -> viewModel.selectArenaOption(idx) },
                            onSubmitAnswer = { viewModel.submitArenaAnswer() },
                            onDismissIntervention = { viewModel.dismissIntervention() },
                            onAdvanceQuestion = { viewModel.advanceArenaQuestion() },
                            onOpenAiForge = { navigator.openSubScreen(ROUTE_AI_STUDIO) },
                            onBack = { navigator.popOrGoHome() }
                        )
                    }

                    composable(ROUTE_AI_STUDIO) {
                        AiC1StudioScreen(
                            controlState = state,
                            onEvaluateWriting = { draft, topic -> viewModel.evaluateWriting(draft, topic) },
                            onGenerateDrills = { pillar -> viewModel.generateAdaptiveAiDrills(pillar) },
                            onLaunchGeneratedDrills = { items ->
                                viewModel.startArenaSession(
                                    mode = ArenaMode.AI_CUSTOM_DRILLS,
                                    customAiItems = items
                                )
                                navigator.openSubScreen(ROUTE_ARENA)
                            },
                            onAskCoach = { q -> viewModel.askSocraticCoach(q) },
                            onBack = { navigator.popOrGoHome() }
                        )
                    }
                }
            }
        }
    }
}