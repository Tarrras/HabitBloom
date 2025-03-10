package com.horizondev.habitbloom.habits.presentation.addHabit

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.habits.presentation.navigation.HabitFlowNavHost

/**
 * Voyager screen that hosts the Add Habit flow implemented with KMP Navigation.
 * This serves as an adapter between Voyager and the official KMP Navigation.
 */
class AddHabitFlowScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Use our NavHost with KMP Navigation and type-safe routes
        HabitFlowNavHost(
            onFinishFlow = { navigator.pop() }
        )
    }
} 