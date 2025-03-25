package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail

/**
 * UI state for the Habit Flower Detail screen.
 *
 * @property isLoading Whether the data is currently loading
 * @property habitFlowerDetail The habit flower detail data to display
 * @property errorMessage Error message to display if loading fails
 * @property showWateringAnimation Whether to show the watering animation
 */
data class HabitFlowerDetailUiState(
    val isLoading: Boolean = false,
    val habitFlowerDetail: HabitFlowerDetail? = null,
    val errorMessage: String? = null,
    val showWateringAnimation: Boolean = false
)

/**
 * UI intents for the Habit Flower Detail screen.
 * These are one-time events that the ViewModel sends to the UI.
 */
sealed class HabitFlowerDetailUiIntent {
    /**
     * Navigate back to the previous screen
     */
    object NavigateBack : HabitFlowerDetailUiIntent()

    /**
     * Navigate to the edit habit screen
     *
     * @property habitId The ID of the habit to edit
     */
    data class NavigateToEditHabit(val habitId: Long) : HabitFlowerDetailUiIntent()

    /**
     * Show a snackbar message
     *
     * @property message The message to display
     */
    data class ShowSnackbar(val message: String) : HabitFlowerDetailUiIntent()
}

/**
 * UI events for the Habit Flower Detail screen.
 * These are events that the UI sends to the ViewModel.
 */
sealed class HabitFlowerDetailUiEvent {
    /**
     * Water (complete) today's habit
     */
    object WaterTodaysHabit : HabitFlowerDetailUiEvent()

    /**
     * Navigate to the edit habit screen
     *
     * @property habitId The ID of the habit to edit
     */
    data class NavigateToEditHabit(val habitId: Long) : HabitFlowerDetailUiEvent()

    /**
     * Navigate back to the previous screen
     */
    object NavigateBack : HabitFlowerDetailUiEvent()
} 