package com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay

/**
 * ViewModel for the time of day choice screen.
 */
class AddHabitTimeOfDayViewModel :
    BloomViewModel<AddHabitTimeOfDayUiState, AddHabitTimeOfDayUiIntent>(
        initialState = AddHabitTimeOfDayUiState()
    ) {
    /**
     * Single entry point for handling UI events.
     */
    fun handleUiEvent(event: AddHabitTimeOfDayUiEvent) {
        when (event) {
            is AddHabitTimeOfDayUiEvent.SelectTimeOfDay -> {
                emitUiIntent(AddHabitTimeOfDayUiIntent.NavigateWithTimeOfDay(event.timeOfDay))
            }

            AddHabitTimeOfDayUiEvent.NavigateBack -> {
                emitUiIntent(AddHabitTimeOfDayUiIntent.NavigateBack)
            }
        }
    }
}

/**
 * UI State for the time of day choice screen.
 */
data class AddHabitTimeOfDayUiState(
    // Currently an empty state as we don't need to track any state for this screen
    val placeholder: Boolean = true
)

/**
 * UI Events that can be triggered from the UI.
 */
sealed interface AddHabitTimeOfDayUiEvent {
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay) : AddHabitTimeOfDayUiEvent
    data object NavigateBack : AddHabitTimeOfDayUiEvent
}

/**
 * UI Intents emitted by the ViewModel.
 */
sealed interface AddHabitTimeOfDayUiIntent {
    data class NavigateWithTimeOfDay(val timeOfDay: TimeOfDay) : AddHabitTimeOfDayUiIntent
    data object NavigateBack : AddHabitTimeOfDayUiIntent
} 