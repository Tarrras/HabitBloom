package com.horizondev.habitbloom.screens.statistic

import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.datetime.DayOfWeek

/**
 * Represents the UI state for the Statistics screen.
 */
data class StatisticUiState(
    val isLoading: Boolean = false,
    val userHasAnyCompleted: Boolean = true,
    val selectedTimeUnit: TimeUnit = TimeUnit.WEEK,
    val selectedWeekOffset: Int = 0, // 0 represents current week, -1 previous week, etc.
    val completeHabitsByTimeOfDay: Map<TimeOfDay, Int> = emptyMap(),
    val completedHabitsThisWeek: Map<DayOfWeek, Int> = emptyMap(),
    val selectedWeekLabel: String = "" // To display the current selected week period
)

/**
 * UI events that can be triggered from the Statistics screen.
 */
sealed class StatisticUiEvent {
    /**
     * Event to open habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : StatisticUiEvent()
    data class SelectTimeUnit(val timeUnit: TimeUnit) : StatisticUiEvent()

    // Week navigation events
    data object PreviousWeek : StatisticUiEvent()
    data object NextWeek : StatisticUiEvent()
    data object CurrentWeek : StatisticUiEvent() // Reset to current week
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class StatisticUiIntent {
    /**
     * Intent to navigate to habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : StatisticUiIntent()
}
