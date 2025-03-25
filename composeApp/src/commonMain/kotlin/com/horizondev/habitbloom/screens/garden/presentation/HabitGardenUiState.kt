package com.horizondev.habitbloom.screens.garden.presentation

import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

/**
 * Represents the UI state for the Habit Garden screen.
 */
data class HabitGardenUiState(
    val isLoading: Boolean = false,
    val habitFlowers: List<HabitFlower> = emptyList(),
    val selectedTimeOfDay: TimeOfDay = TimeOfDay.Morning,
    val errorMessage: String? = null,
    val themeOption: ThemeOption
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
     * Event to open flower details.
     */
    data class OpenFlowerDetails(val habitId: Long) : HabitGardenUiEvent()

    /**
     * Event to refresh garden data.
     */
    data object RefreshGarden : HabitGardenUiEvent()

    /**
     * Event to go back.
     */
    data object BackPressed : HabitGardenUiEvent()
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class HabitGardenUiIntent {
    /**
     * Intent to navigate to habit details.
     */
    data class OpenFlowerDetails(val habitId: Long) : HabitGardenUiIntent()

    /**
     * Intent to navigate back.
     */
    data object NavigateBack : HabitGardenUiIntent()
} 