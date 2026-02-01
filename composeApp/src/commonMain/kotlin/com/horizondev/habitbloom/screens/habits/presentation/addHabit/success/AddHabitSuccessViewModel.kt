package com.horizondev.habitbloom.screens.habits.presentation.addHabit.success

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase

/**
 * ViewModel for the habit addition success screen.
 */
class AddHabitSuccessViewModel(
    private val addHabitStateUseCase: AddHabitStateUseCase
) : BloomViewModel<AddHabitSuccessUiState, AddHabitSuccessUiIntent>(
    initialState = AddHabitSuccessUiState(
        habitName = addHabitStateUseCase.lastAddedHabitName.value
    )
) {
    init {
        launch {
            addHabitStateUseCase.lastAddedHabitName.collect { habitName ->
                updateState { it.copy(habitName = habitName) }
            }
        }
    }

    /**
     * Single entry point for handling UI events.
     */
    fun handleUiEvent(event: AddHabitSuccessUiEvent) {
        when (event) {
            AddHabitSuccessUiEvent.FinishFlow -> {
                addHabitStateUseCase.clearLastAddedHabitName()
                emitUiIntent(AddHabitSuccessUiIntent.FinishFlow)
            }

            AddHabitSuccessUiEvent.AddAnotherHabit -> {
                addHabitStateUseCase.clearLastAddedHabitName()
                emitUiIntent(AddHabitSuccessUiIntent.AddAnotherHabit)
            }
        }
    }
}

/**
 * UI State for the success screen.
 */
data class AddHabitSuccessUiState(
    val habitName: String? = null
)

/**
 * UI Events that can be triggered from the UI.
 */
sealed interface AddHabitSuccessUiEvent {
    data object FinishFlow : AddHabitSuccessUiEvent
    data object AddAnotherHabit : AddHabitSuccessUiEvent
}

/**
 * UI Intents emitted by the ViewModel.
 */
sealed interface AddHabitSuccessUiIntent {
    data object FinishFlow : AddHabitSuccessUiIntent
    data object AddAnotherHabit : AddHabitSuccessUiIntent
} 