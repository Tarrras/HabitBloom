package com.horizondev.habitbloom.statistic

import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay

data class StatisticUiState(
    val isLoading: Boolean = false,
    val userHasAnyCompleted: Boolean = true,
    val selectedTimeUnit: TimeUnit = TimeUnit.WEEK,
    val completeHabitsCountData: Map<TimeOfDay, Int> = emptyMap()
)

sealed interface StatisticUiEvent {
    data class SelectTimeUnit(val timeUnit: TimeUnit) : StatisticUiEvent
}
