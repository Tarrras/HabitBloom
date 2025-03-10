package com.horizondev.habitbloom.habits.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.navigation.CommonNavigator
import com.horizondev.habitbloom.core.navigation.NavigationComponent
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowGlobalNavEntryPoint
import com.horizondev.habitbloom.habits.presentation.createHabit.createPersonalHabitFlowGraph
import com.horizondev.habitbloom.platform.StatusBarColors
import org.koin.compose.koinInject

/**
 * NavHost for the Add Habit flow using type-safe navigation.
 */
@Composable
fun HabitFlowNavHost(
    navigator: CommonNavigator = koinInject(),
    onFinishFlow: () -> Unit,
) {
    val navController = rememberNavController()

    StatusBarColors(
        statusBarColor = BloomTheme.colors.background,
        navBarColor = BloomTheme.colors.background
    )

    NavigationComponent(
        navController = navController,
        navigator = navigator,
        startDestination = AddHabitFlowGlobalNavEntryPoint
    ) {
        // Add all routes from our sealed class
        /*addHabitFlowGraph(
            navController = navController,
            onFinishFlow = onFinishFlow,
            onNavigateToCreateCustomHabit = { timeOfDay ->
                navController.navigate(CreatePersonalHabitFlowRoute.CreateHabit(timeOfDay))
            }
        )*/

        createPersonalHabitFlowGraph(
            navController = navController
        )
    }
}