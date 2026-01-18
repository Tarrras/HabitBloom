package com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class AddHabitSummaryUiState(
    val timeOfDay: TimeOfDay? = null,
    val habitInfo: HabitInfo? = null,
    val habitCategory: HabitCategoryData? = null,
    val days: List<DayOfWeek> = emptyList(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val durationInDays: Int = 0,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime? = null,
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
