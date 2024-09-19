package com.horizondev.habitbloom.habits.presentation.home

import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo

data class HomeScreenUiState(
    val habitsCount: Int = 0,
    val completedHabitsCount: Int = 0,
    val userHabits: List<UserHabitRecordFullInfo> = emptyList(),
    val selectedTimeOfDay: TimeOfDay = TimeOfDay.Morning,
)

sealed interface HomeScreenUiEvent {
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay): HomeScreenUiEvent
}