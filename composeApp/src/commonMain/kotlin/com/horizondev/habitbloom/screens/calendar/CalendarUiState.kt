package com.horizondev.habitbloom.screens.calendar

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.kizitonwose.calendar.core.YearMonth
import kotlinx.datetime.LocalDate

/**
 * Represents the UI state for the Calendar screen.
 */
data class CalendarUiState(
    // Basic state
    val isLoading: Boolean = false,

    // Calendar state
    val selectedDate: LocalDate,
    val currentMonth: YearMonth,
    val showBottomSheet: Boolean = false,

    // Filtered view state
    val selectedTimeOfDayFilter: TimeOfDay? = null, // null means all

    // Data for display
    val habitsByDate: Map<LocalDate, List<UserHabitRecordFullInfo>> = emptyMap(),

    // Selected date data
    val habitsForSelectedDate: List<UserHabitRecordFullInfo> = emptyList(),

    // Monthly statistics
    val monthlyStats: MonthlyStatistics = MonthlyStatistics(),

    // Streak data
    val habitsWithStreaks: Map<Long, HabitStreakInfo> = emptyMap(),

    // Celebrating habit
    val celebratingHabitId: Long? = null
)

/**
 * Holds statistics for the currently visible month
 */
data class MonthlyStatistics(
    val totalHabits: Int = 0,
    val completedHabits: Int = 0,
    val completionRate: Float = 0f,
    val longestStreak: Int = 0
)

/**
 * Holds streak information for a specific habit
 */
data class HabitStreakInfo(
    val userHabitId: Long,
    val habitName: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
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

    /**
     * Event to change month.
     */
    data class ChangeMonth(val yearMonth: YearMonth) : CalendarUiEvent()

    /**
     * Event to filter habits by time of day.
     */
    data class FilterByTimeOfDay(val timeOfDay: TimeOfDay?) : CalendarUiEvent()

    /**
     * Event to toggle habit completion status.
     */
    data class ToggleHabitCompletion(
        val habitId: Long,
        val date: LocalDate,
        val completed: Boolean
    ) : CalendarUiEvent()

    /**
     * Event to close the bottom sheet.
     */
    data object CloseBottomSheet : CalendarUiEvent()

    /**
     * Event to jump to today.
     */
    object JumpToToday : CalendarUiEvent()

    /**
     * Event to show streak celebration.
     */
    data class ShowStreakCelebration(val habitId: Long) : CalendarUiEvent()
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