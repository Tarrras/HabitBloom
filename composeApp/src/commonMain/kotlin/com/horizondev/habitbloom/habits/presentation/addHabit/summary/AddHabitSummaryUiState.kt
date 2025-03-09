package com.horizondev.habitbloom.habits.presentation.addHabit.summary

import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class AddHabitSummaryUiState(
    val timeOfDay: TimeOfDay,
    val habitInfo: HabitInfo,
    val days: List<DayOfWeek>,
    val duration: Int,
    val startDate: LocalDate,
    val weekStartOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK,
    val isLoading: Boolean = false
)

sealed interface AddHabitSummaryUiEvent {
    data object Confirm : AddHabitSummaryUiEvent
    data object BackPressed : AddHabitSummaryUiEvent
}

sealed interface AddHabitSummaryUiIntent {
    data class ShowSnackBar(
        val visuals: BloomSnackbarVisuals
    ) : AddHabitSummaryUiIntent

    data object NavigateToSuccess : AddHabitSummaryUiIntent
    data object NavigateBack : AddHabitSummaryUiIntent
}
