package com.horizondev.habitbloom.screens.statistic

import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

/**
 * Represents the UI state for the Statistics screen.
 */
data class StatisticUiState(
    val isLoading: Boolean = false,
    val userHasAnyCompleted: Boolean = true,
    val selectedTimeUnit: TimeUnit = TimeUnit.WEEK,
    val selectedPeriodOffset: Int = 0, // 0 represents current period, -1 previous period, etc.
    val completeHabitsByTimeOfDay: Map<TimeOfDay, Int> = emptyMap(),

    // Consolidated period data - holds data for the currently selected time unit
    val completedHabitsByPeriod: Map<String, Int> = emptyMap(),
    val scheduledHabitsByPeriod: Map<String, Int> = emptyMap(),
    val selectedPeriodLabel: String = "", // Label for the current selected period (week, month, year)

    // Formatted chart data (populated by ViewModel)
    val formattedChartData: ChartData = ChartData.WeekData(emptyList(), emptyMap(), emptyMap())
)

/**
 * Represents formatted chart data for different time units in a type-safe way.
 */
sealed class ChartData {
    /**
     * Chart data for weekly view.
     *
     * @property weeklyCategories List of day abbreviations (e.g., "Mon", "Tue", etc.)
     * @property weeklyCompletedData Map of day abbreviation to completed count
     * @property weeklyScheduledData Map of day abbreviation to scheduled count
     */
    data class WeekData(
        val weeklyCategories: List<String>,
        val weeklyCompletedData: Map<String, Int>,
        val weeklyScheduledData: Map<String, Int>
    ) : ChartData()

    /**
     * Chart data for monthly view.
     *
     * @property monthlyCategories List of week labels (e.g., "Week 1", "Week 2", etc.)
     * @property monthlyCompletedData Map of week label to completed count
     * @property monthlyScheduledData Map of week label to scheduled count
     */
    data class MonthData(
        val monthlyCategories: List<String>,
        val monthlyCompletedData: Map<String, Int>,
        val monthlyScheduledData: Map<String, Int>
    ) : ChartData()

    /**
     * Chart data for yearly view.
     *
     * @property yearlyCategories List of month abbreviations (e.g., "Jan", "Feb", etc.)
     * @property yearlyCompletedData Map of month abbreviation to completed count
     * @property yearlyScheduledData Map of month abbreviation to scheduled count
     */
    data class YearData(
        val yearlyCategories: List<String>,
        val yearlyCompletedData: Map<String, Int>,
        val yearlyScheduledData: Map<String, Int>
    ) : ChartData()

    /**
     * Get the categories for the chart X-axis.
     */
    fun getCategories(): List<String> = when (this) {
        is WeekData -> weeklyCategories
        is MonthData -> monthlyCategories
        is YearData -> yearlyCategories
    }

    /**
     * Get the completed habits data.
     */
    fun getCompletedData(): Map<String, Int> = when (this) {
        is WeekData -> weeklyCompletedData
        is MonthData -> monthlyCompletedData
        is YearData -> yearlyCompletedData
    }

    /**
     * Get the scheduled habits data.
     */
    fun getScheduledData(): Map<String, Int> = when (this) {
        is WeekData -> weeklyScheduledData
        is MonthData -> monthlyScheduledData
        is YearData -> yearlyScheduledData
    }
}

/**
 * UI events that can be triggered from the Statistics screen.
 */
sealed class StatisticUiEvent {
    /**
     * Event to open habit details.
     */
    data class OpenHabitDetails(val habitId: Long) : StatisticUiEvent()
    data class SelectTimeUnit(val timeUnit: TimeUnit) : StatisticUiEvent()
    data class TimeUnitChanged(val timeUnit: TimeUnit) : StatisticUiEvent()

    // Period navigation events
    data object PreviousPeriod : StatisticUiEvent()
    data object NextPeriod : StatisticUiEvent()
    data object CurrentPeriod : StatisticUiEvent() // Reset to current period
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
