package com.horizondev.habitbloom.calendar

import kotlinx.datetime.LocalDate

/**
 * Represents the UI state for the Calendar screen.
 */
data class CalendarUiState(
    val selectedDate: LocalDate,
    val habitsByDate: Map<LocalDate, List<String>>, //todo change later
    val isLoading: Boolean
)

/**
 * UI events that can be triggered from the Calendar screen.
 */
sealed class CalendarUiEvent {
    /**
     * Event to select a date on the calendar.
     */
    data class SelectDate(val date: LocalDate) : CalendarUiEvent()

    /**
     * Event to open habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : CalendarUiEvent()
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class CalendarUiIntent {
    /**
     * Intent to navigate to habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : CalendarUiIntent()
} 