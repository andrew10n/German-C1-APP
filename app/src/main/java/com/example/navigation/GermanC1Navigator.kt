package com.example.navigation

import androidx.navigation.NavController
import com.example.ui.MainViewModel

/**
 * Centralized navigation logic for German C1 app
 * Reduces complexity in MainActivity.kt
 */
class GermanC1Navigator(
    private val navController: NavController,
    private val viewModel: MainViewModel
) {
    // Navigation constants
    const val ROUTE_HUB = "hub"
    const val ROUTE_ROADMAP = "roadmap"
    const val ROUTE_CONTROLLER = "controller"
    const val ROUTE_ARENA = "arena"
    const val ROUTE_AI_STUDIO = "ai_studio"
    const val ROUTE_REPO_UNIT = "unit/{unitId}"
    const val ROUTE_LESSON = "lesson/{moduleId}"

    fun navigateToHub() {
        navController.navigate(ROUTE_HUB) {
            popUpTo(ROUTE_HUB) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToRoadmap() {
        navController.navigate(ROUTE_ROADMAP) {
            popUpTo(ROUTE_HUB) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToController() {
        navController.navigate(ROUTE_CONTROLLER) {
            popUpTo(ROUTE_HUB) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun openSubScreen(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun popOrGoHome() {
        if (!navController.popBackStack()) {
            navigateToHub()
        }
    }

    fun isFocusedSubScreen(currentRoute: String?): Boolean {
        return currentRoute?.startsWith("lesson/") == true ||
                currentRoute?.startsWith("unit/") == true ||
                currentRoute == ROUTE_ARENA ||
                currentRoute == ROUTE_AI_STUDIO
    }
}