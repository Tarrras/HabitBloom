package com.horizondev.habitbloom.screens.garden.presentation

import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

/**
 * Represents the UI state for the Habit Garden screen.
 */
data class HabitGardenUiState(
    val isLoading: Boolean = false,
    val habitFlowers: List<HabitFlower> = emptyList(),
    val selectedTimeOfDay: TimeOfDay = TimeOfDay.Morning,
    val errorMessage: String? = null
)

/**
 * UI events that can be triggered from the Habit Garden screen.
 */
sealed class HabitGardenUiEvent {
    /**
     * Event to select a time of day filter.
     */
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay) : HabitGardenUiEvent()

    /**
     * Event to open habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : HabitGardenUiEvent()

    /**
     * Event to refresh garden data.
     */
    data object RefreshGarden : HabitGardenUiEvent()
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class HabitGardenUiIntent {
    /**
     * Intent to navigate to habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : HabitGardenUiIntent()
} 