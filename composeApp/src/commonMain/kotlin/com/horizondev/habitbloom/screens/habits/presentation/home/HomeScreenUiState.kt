package com.horizondev.habitbloom.screens.habits.presentation.home

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo

data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val selectedTimeOfDay: TimeOfDay = TimeOfDay.Morning,
    val habitsCount: Int = 0,
    val completedHabitsCount: Int = 0,
    val userCompletedAllHabitsForTimeOfDay: Boolean = false,
    val userHabits: List<UserHabitRecordFullInfo> = emptyList(),
)

sealed interface HomeScreenUiEvent {
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay) : HomeScreenUiEvent
    data class OpenHabitDetails(val userHabitId: Long) : HomeScreenUiEvent
    data class ChangeHabitCompletionStatus(
        val id: Long,
        val isCompleted: Boolean
    ) : HomeScreenUiEvent
}

sealed interface HomeScreenUiIntent {
    data class OpenHabitDetails(val userHabitId: Long) : HomeScreenUiIntent
}