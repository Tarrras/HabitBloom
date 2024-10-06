package com.horizondev.habitbloom.statistic

import androidx.compose.runtime.Immutable
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.datetime.DayOfWeek

@Immutable
data class StatisticUiState(
    val isLoading: Boolean = false,
    val userHasAnyCompleted: Boolean = true,
    val selectedTimeUnit: TimeUnit = TimeUnit.WEEK,
    val completeHabitsByTimeOfDay: Map<TimeOfDay, Int> = emptyMap(),
    val completedHabitsThisWeek: Map<DayOfWeek, Int> = emptyMap()
)

sealed interface StatisticUiEvent {
    data class SelectTimeUnit(val timeUnit: TimeUnit) : StatisticUiEvent
}
