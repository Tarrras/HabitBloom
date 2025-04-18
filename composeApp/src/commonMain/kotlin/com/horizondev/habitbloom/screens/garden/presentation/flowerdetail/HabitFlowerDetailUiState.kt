package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail

/**
 * UI state for the Habit Flower Detail screen.
 *
 * @property isLoading Whether the data is currently loading
 * @property habitFlowerDetail The habit flower detail data to display
 * @property errorMessage Error message to display if loading fails
 * @property showWateringAnimation Whether to show the watering animation
 * @property themeOption App's theme
 */
data class HabitFlowerDetailUiState(
    val isLoading: Boolean = false,
    val habitFlowerDetail: HabitFlowerDetail? = null,
    val errorMessage: String? = null,
    val showWateringAnimation: Boolean = false,
    val themeOption: ThemeOption
)

/**
 * UI intents for the Habit Flower Detail screen.
 * These are one-time events that the ViewModel sends to the UI.
 */
sealed class HabitFlowerDetailUiIntent {
    /**
     * Navigate back to the previous screen
     */
    data object NavigateBack : HabitFlowerDetailUiIntent()

    /**
     * Navigate to the habit details screen
     *
     * @property habitId The ID of the habit to edit
     */
    data class NavigateToHabitDetails(val habitId: Long) : HabitFlowerDetailUiIntent()

    /**
     * Show a snackbar message
     *
     * @property visuals The message to display
     */
    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : HabitFlowerDetailUiIntent()
}

/**
 * UI events for the Habit Flower Detail screen.
 * These are events that the UI sends to the ViewModel.
 */
sealed class HabitFlowerDetailUiEvent {
    /**
     * Water (complete) today's habit
     */
    data object WaterTodaysHabit : HabitFlowerDetailUiEvent()

    /**
     * Navigate to the edit habit screen
     *
     * @property habitId The ID of the habit to edit
     */
    data class NavigateToHabitDetails(val habitId: Long) : HabitFlowerDetailUiEvent()

    /**
     * Navigate back to the previous screen
     */
    data object NavigateBack : HabitFlowerDetailUiEvent()
} 