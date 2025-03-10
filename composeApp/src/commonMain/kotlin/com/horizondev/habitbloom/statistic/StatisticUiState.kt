package com.horizondev.habitbloom.statistic

import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.datetime.DayOfWeek

/**
 * Represents the UI state for the Statistics screen.
 */
data class StatisticUiState(
    val isLoading: Boolean = false,
    val userHasAnyCompleted: Boolean = true,
    val selectedTimeUnit: TimeUnit = TimeUnit.WEEK,
    val completeHabitsByTimeOfDay: Map<TimeOfDay, Int> = emptyMap(),
    val completedHabitsThisWeek: Map<DayOfWeek, Int> = emptyMap()
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
