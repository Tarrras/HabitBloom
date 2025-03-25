package com.horizondev.habitbloom.screens.habits.presentation.home

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.datetime.LocalDate

data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val selectedTimeOfDay: TimeOfDay = TimeOfDay.Morning,
    val selectedDate: LocalDate = getCurrentDate(),
    val habitsCount: Int = 0,
    val completedHabitsCount: Int = 0,
    val userCompletedAllHabitsForTimeOfDay: Boolean = false,
    val userHabits: List<UserHabitRecordFullInfo> = emptyList(),
    val habitStatusEditMode: Boolean = true
)

sealed interface HomeScreenUiEvent {
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay) : HomeScreenUiEvent
    data class SelectDate(val date: LocalDate) : HomeScreenUiEvent
    data class OpenHabitDetails(val userHabitId: Long) : HomeScreenUiEvent
    data class ChangeHabitCompletionStatus(
        val id: Long,
        val isCompleted: Boolean
    ) : HomeScreenUiEvent
}

sealed class HomeScreenUiIntent {
    data class OpenHabitDetails(val userHabitId: Long) : HomeScreenUiIntent()
}