package com.horizondev.habitbloom.habits.presentation.addHabit.success

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel

/**
 * ViewModel for the habit addition success screen.
 */
class AddHabitSuccessViewModel : BloomViewModel<AddHabitSuccessUiState, AddHabitSuccessUiIntent>(
    initialState = AddHabitSuccessUiState()
) {
    /**
     * Single entry point for handling UI events.
     */
    fun handleUiEvent(event: AddHabitSuccessUiEvent) {
        when (event) {
            AddHabitSuccessUiEvent.FinishFlow -> {
                emitUiIntent(AddHabitSuccessUiIntent.FinishFlow)
            }
        }
    }
}

/**
 * UI State for the success screen.
 */
data class AddHabitSuccessUiState(
    // Currently an empty state as we don't need to track any state for this screen
    val placeholder: Boolean = true
)

/**
 * UI Events that can be triggered from the UI.
 */
sealed interface AddHabitSuccessUiEvent {
    data object FinishFlow : AddHabitSuccessUiEvent
}

/**
 * UI Intents emitted by the ViewModel.
 */
sealed interface AddHabitSuccessUiIntent {
    data object FinishFlow : AddHabitSuccessUiIntent
} 