package com.horizondev.habitbloom.screens.habits.presentation.addHabit

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel

/**
 * Minimal ViewModel for Add Habit flow that only handles snackbar display.
 * This serves as the central point for snackbar management in the flow.
 */
class AddHabitFlowViewModel : BloomViewModel<Unit, AddHabitFlowUiIntent>(
    initialState = Unit
) {

    /**
     * Shows a snackbar with the given visuals.
     */
    fun showSnackbar(visuals: BloomSnackbarVisuals) {
        emitUiIntent(AddHabitFlowUiIntent.ShowShackbar(visuals))
    }
}

/**
 * UI Intents emitted by the Add Habit Flow ViewModel.
 */
sealed interface AddHabitFlowUiIntent {
    data class ShowShackbar(val visuals: BloomSnackbarVisuals) : AddHabitFlowUiIntent
}
