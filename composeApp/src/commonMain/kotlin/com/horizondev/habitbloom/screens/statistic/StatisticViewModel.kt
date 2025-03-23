package com.horizondev.habitbloom.screens.statistic

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getEndOfMonth
import com.horizondev.habitbloom.utils.getShortTitleSuspend
import com.horizondev.habitbloom.utils.getTitleSuspend
import com.horizondev.habitbloom.utils.minusDays
import com.horizondev.habitbloom.utils.minusMonths
import com.horizondev.habitbloom.utils.minusYears
import com.horizondev.habitbloom.utils.plusDays
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

/**
 * ViewModel for the Statistics screen.
 */
class StatisticViewModel(
    private val repository: HabitsRepository
) : BloomViewModel<StatisticUiState, StatisticUiIntent>(
    StatisticUiState(isLoading = true)
) {

    private val filteredHabitFlow = combine(
        state.map { it.selectedTimeUnit }.distinctUntilChanged(),
        state.map { it.selectedPeriodOffset }.distinctUntilChanged(),
        repository.getListOfAllUserHabitRecordsFlow(
            untilDate = getCurrentDate().plus(DatePeriod(years = 1))
        )
    ) { selectedTimeUnit, selectedPeriodOffset, habitRecords ->
        handleHabitsList(
            habitRecords = habitRecords,
            selectedTimeUnit = selectedTimeUnit,
            selectedPeriodOffset = selectedPeriodOffset
        )
    }.onStart {
        updateState { it.copy(isLoading = true) }
    }.onCompletion {
        updateState { it.copy(isLoading = false) }
    }.catch {
        updateState { it.copy(isLoading = false) }
    }.launchIn(viewModelScope)

    fun handleUiEvent(uiEvent: StatisticUiEvent) {
        when (uiEvent) {
            is StatisticUiEvent.SelectTimeUnit, is StatisticUiEvent.TimeUnitChanged -> {
                // Extract the time unit from either event type
                val timeUnit = when (uiEvent) {
                    is StatisticUiEvent.SelectTimeUnit -> uiEvent.timeUnit
                    is StatisticUiEvent.TimeUnitChanged -> uiEvent.timeUnit
                    else -> return
                }

                // Reset the period offset when changing the time unit
                updateState {
                    it.copy(
                        selectedTimeUnit = timeUnit,
                        selectedPeriodOffset = 0
                    )
                }

                // Update the period label to reflect the new time unit
                updatePeriodLabel()
            }

            is StatisticUiEvent.PreviousPeriod -> {
                updateState { it.copy(selectedPeriodOffset = it.selectedPeriodOffset - 1) }
                updatePeriodLabel()
            }

            is StatisticUiEvent.NextPeriod -> {
                // Don't allow future periods
                updateState {
                    if (it.selectedPeriodOffset < 0) {
                        it.copy(selectedPeriodOffset = it.selectedPeriodOffset + 1)
                    } else {
                        it
                    }
                }
                updatePeriodLabel()
            }

            is StatisticUiEvent.CurrentPeriod -> {
                updateState { it.copy(selectedPeriodOffset = 0) }
                updatePeriodLabel()
            }

            is StatisticUiEvent.OpenHabitDetails -> TODO()
        }
    }

    /**
     * Updates the appropriate period label based on the selected time unit and period offset
     */
    private fun updatePeriodLabel() = viewModelScope.launch {
        val currentState = state.value
        val currentDate = getCurrentDate()
        val periodOffset = currentState.selectedPeriodOffset

        val periodLabel = when (currentState.selectedTimeUnit) {
            TimeUnit.WEEK -> {
                val startOfWeek =
                    currentDate.calculateStartOfWeek().minusDays((-periodOffset * 7).toLong())
                val endOfWeek = startOfWeek.plusDays(6)
                formatDateRange(startOfWeek, endOfWeek)
            }

            TimeUnit.MONTH -> {
                val targetMonth = currentDate.minusMonths(-periodOffset.toLong())
                "${
                    targetMonth.month.getTitleSuspend().lowercase()
                        .replaceFirstChar { it.uppercase() }
                } ${targetMonth.year}"
            }

            TimeUnit.YEAR -> {
                val targetYear = currentDate.minusYears(-periodOffset.toLong())
                targetYear.year.toString()
            }
        }

        updateState { it.copy(selectedPeriodLabel = periodLabel) }
    }

    private fun handleHabitsList(
        habitRecords: List<UserHabitRecordFullInfo>,
        selectedTimeUnit: TimeUnit,
        selectedPeriodOffset: Int
    ) = viewModelScope.launch {
        val completedHabits = habitRecords.filter { it.isCompleted }
        val completeHabitsByTimeOfDay = handleGeneralHabitStatistic(
            completedHabits = completedHabits,
            selectedTimeUnit = selectedTimeUnit,
            periodOffset = selectedPeriodOffset
        )

        // Process data based on the selected time unit
        val (completedHabitsByPeriod, scheduledHabitsByPeriod, periodLabel, formattedChartData) = when (selectedTimeUnit) {
            TimeUnit.WEEK -> {
                val weeklyResult = handleWeeklyHabitStatistic(
                    habitRecords = habitRecords,
                    weekOffset = selectedPeriodOffset
                )

                // Prepare formatted chart data for weekly view
                val categories = DayOfWeek.entries.map { it.getShortTitleSuspend() }
                val completedData = weeklyResult.first.mapKeys { it.key.getShortTitleSuspend() }
                val scheduledData = weeklyResult.second.mapKeys { it.key.getShortTitleSuspend() }

                val formattedChartData = ChartData.WeekData(
                    weeklyCategories = categories,
                    weeklyCompletedData = completedData,
                    weeklyScheduledData = scheduledData
                )

                Quadruple(
                    weeklyResult.first.mapKeys { it.key.getShortTitleSuspend() },
                    weeklyResult.second.mapKeys { it.key.getShortTitleSuspend() },
                    weeklyResult.third,
                    formattedChartData
                )
            }

            TimeUnit.MONTH -> {
                val monthlyResult = handleMonthlyHabitStatistic(
                    habitRecords = habitRecords,
                    monthOffset = selectedPeriodOffset
                )

                // Prepare formatted chart data for monthly view
                val categories = monthlyResult.first.keys.toList()
                val formattedChartData = ChartData.MonthData(
                    monthlyCategories = categories,
                    monthlyCompletedData = monthlyResult.first,
                    monthlyScheduledData = monthlyResult.second
                )

                Quadruple(
                    monthlyResult.first,
                    monthlyResult.second,
                    monthlyResult.third,
                    formattedChartData
                )
            }

            TimeUnit.YEAR -> {
                val yearlyResult = handleYearlyHabitStatistic(
                    habitRecords = habitRecords,
                    yearOffset = selectedPeriodOffset
                )

                // Prepare formatted chart data for yearly view
                val monthCategories = Month.entries.map { it.getShortTitleSuspend() }
                val formattedChartData = ChartData.YearData(
                    yearlyCategories = monthCategories,
                    yearlyCompletedData = yearlyResult.first,
                    yearlyScheduledData = yearlyResult.second
                )

                Quadruple(
                    yearlyResult.first,
                    yearlyResult.second,
                    yearlyResult.third,
                    formattedChartData
                )
            }
        }

        updateState {
            it.copy(
                isLoading = false,
                completeHabitsByTimeOfDay = completeHabitsByTimeOfDay,
                completedHabitsByPeriod = completedHabitsByPeriod,
                scheduledHabitsByPeriod = scheduledHabitsByPeriod,
                selectedPeriodLabel = periodLabel,
                formattedChartData = formattedChartData,
                userHasAnyCompleted = completedHabits.isNotEmpty()
            )
        }
    }

    private fun handleGeneralHabitStatistic(
        completedHabits: List<UserHabitRecordFullInfo>,
        selectedTimeUnit: TimeUnit,
        periodOffset: Int
    ): Map<TimeOfDay, Int> {
        val currentDate = getCurrentDate()
        val endDate: LocalDate
        val startDate: LocalDate

        when (selectedTimeUnit) {
            TimeUnit.WEEK -> {
                val startOfCurrentWeek = currentDate.calculateStartOfWeek()
                // Apply the period offset to get the target week
                startDate = startOfCurrentWeek.minusDays((-periodOffset * 7).toLong())
                endDate = startDate.plusDays(6)
            }

            TimeUnit.MONTH -> {
                // Calculate the target month
                val targetMonth = currentDate.minusMonths(-periodOffset.toLong())
                startDate = LocalDate(targetMonth.year, targetMonth.month, 1)
                endDate = startDate.getEndOfMonth()
            }

            TimeUnit.YEAR -> {
                // Calculate the start and end of the target year
                val targetYear = currentDate.year + periodOffset
                startDate = LocalDate(targetYear, Month.JANUARY, 1)
                endDate = LocalDate(targetYear, Month.DECEMBER, 31)
            }
        }

        val completedHabitsFiltered = completedHabits
            .asSequence()
            .filter { it.date in startDate..endDate }
            .groupBy { it.timeOfDay }
            .mapValues { (_, value) -> value.size }

        val result = TimeOfDay.entries
            .sortedBy { it.ordinal }
            .associateWith { completedHabitsFiltered[it] ?: 0 }

        return result
    }

    /**
     * Helper function to check if a year is a leap year
     */
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Calculate habit statistics for a specific week.
     *
     * @param habitRecords List of habits
     * @param weekOffset Offset from current week (0 = current week, -1 = previous week, etc.)
     * @return Triple of (completed habits map, all scheduled habits map, formatted date range string)
     */
    private suspend fun handleWeeklyHabitStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        weekOffset: Int = 0
    ): Triple<Map<DayOfWeek, Int>, Map<DayOfWeek, Int>, String> {
        val currentDate = getCurrentDate()
        val startOfCurrentWeek = currentDate.calculateStartOfWeek()

        // Calculate the start of the target week by applying the offset
        val startOfTargetWeek = startOfCurrentWeek.minusDays((-weekOffset * 7).toLong())
        val endOfTargetWeek = startOfTargetWeek.plusDays(6)

        // Create a formatted string to display the date range
        val weekLabel = formatDateRange(startOfTargetWeek, endOfTargetWeek)

        // Filter habits for this week
        val habitFilteredForGivenWeek = habitRecords
            .asSequence()
            .filter {
                it.date in startOfTargetWeek..endOfTargetWeek
            }
        val completedHabitFilteredForGivenWeek = habitFilteredForGivenWeek.filter { it.isCompleted }

        // Create a map of days to habits
        val weekDaysWithAllScheduledHabits = DayOfWeek.entries.associateWith { day ->
            val date = startOfTargetWeek.plusDays(day.ordinal.toLong())
            habitFilteredForGivenWeek.count { it.date == date }
        }

        // Create a map of days to completed habits
        val weekDaysWithCompletedHabits = DayOfWeek.entries.associateWith { day ->
            val date = startOfTargetWeek.plusDays(day.ordinal.toLong())
            completedHabitFilteredForGivenWeek.count { it.date == date }
        }

        return Triple(
            weekDaysWithCompletedHabits,
            weekDaysWithAllScheduledHabits,
            weekLabel
        )
    }

    /**
     * Calculate habit statistics for a specific month.
     *
     * @param habitRecords List of habits
     * @param monthOffset Offset from current month (0 = current month, -1 = previous month, etc.)
     * @return Triple of (completed habits map, all scheduled habits map, formatted month string)
     */
    private suspend fun handleMonthlyHabitStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        monthOffset: Int = 0
    ): Triple<Map<String, Int>, Map<String, Int>, String> {
        val currentDate = getCurrentDate()
        val targetMonthDate = currentDate.minusMonths(-monthOffset.toLong())

        // Calculate the start and end of the target month
        val startOfMonth = LocalDate(targetMonthDate.year, targetMonthDate.month, 1)
        val endOfMonth = targetMonthDate.getEndOfMonth()

        val monthName = targetMonthDate.month.getTitleSuspend()

        // Filter habits for this month
        val habitsFilteredForGivenMonth = habitRecords
            .filter { it.date in startOfMonth..endOfMonth }

        val completedHabitsFilteredForGivenMonth =
            habitsFilteredForGivenMonth.filter { it.isCompleted }

        // Create day-based maps instead of week-based
        val monthlyCompletedHabits = mutableMapOf<String, Int>()
        val monthlyScheduledHabits = mutableMapOf<String, Int>()

        // Count scheduled habits for this month
        val scheduledCount = habitsFilteredForGivenMonth.count()
        monthlyScheduledHabits[monthName] = scheduledCount

        // Count completed habits for this month
        val completedCount = completedHabitsFilteredForGivenMonth.count()
        monthlyCompletedHabits[monthName] = completedCount

        return Triple(
            monthlyCompletedHabits,
            monthlyScheduledHabits,
            monthName
        )
    }

    /**
     * Calculate habit statistics for a specific year.
     *
     * @param habitRecords List of habits
     * @param yearOffset Offset from current year (0 = current year, -1 = previous year, etc.)
     * @return Triple of (completed habits map, all scheduled habits map, formatted year string)
     */
    private suspend fun handleYearlyHabitStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        yearOffset: Int = 0
    ): Triple<Map<String, Int>, Map<String, Int>, String> {
        val currentDate = getCurrentDate()

        // Calculate the target year
        val targetYear = currentDate.year + yearOffset
        val yearLabel = targetYear.toString()

        // Calculate the start and end of the target year
        val startOfYear = LocalDate(targetYear, Month.JANUARY, 1)
        val endOfYear = LocalDate(targetYear, Month.DECEMBER, 31)

        // Filter habits for this year
        val habitsFilteredForGivenYear = habitRecords
            .asSequence()
            .filter { it.date in startOfYear..endOfYear }

        val completedHabitsFilteredForGivenYear =
            habitsFilteredForGivenYear.filter { it.isCompleted }

        // Group by month
        val monthlyCompletedHabits = mutableMapOf<String, Int>()
        val monthlyScheduledHabits = mutableMapOf<String, Int>()

        // Initialize the maps with zeros for all months
        val monthAbbreviations = Month.entries.map { it.getShortTitleSuspend() }

        for (monthAbbr in monthAbbreviations) {
            monthlyCompletedHabits[monthAbbr] = 0
            monthlyScheduledHabits[monthAbbr] = 0
        }

        // Process each month of the year
        for (monthOrdinal in 1..12) {
            val month = Month(monthOrdinal)
            val monthAbbr = monthAbbreviations[monthOrdinal - 1]

            // Calculate days in this month
            val daysInMonth = when (month) {
                Month.FEBRUARY -> if (isLeapYear(targetYear)) 29 else 28
                Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
                else -> 31
            }

            // Calculate habits for each day in this month
            for (day in 1..daysInMonth) {
                val date = LocalDate(targetYear, month, day)

                // Count scheduled habits for this day
                val scheduledCount = habitsFilteredForGivenYear.count { it.date == date }
                monthlyScheduledHabits[monthAbbr] =
                    (monthlyScheduledHabits[monthAbbr] ?: 0) + scheduledCount

                // Count completed habits for this day
                val completedCount = completedHabitsFilteredForGivenYear.count { it.date == date }
                monthlyCompletedHabits[monthAbbr] =
                    (monthlyCompletedHabits[monthAbbr] ?: 0) + completedCount
            }
        }

        return Triple(
            monthlyCompletedHabits,
            monthlyScheduledHabits,
            yearLabel
        )
    }

    /**
     * Format a date range into a human-readable string.
     */
    private suspend fun formatDateRange(start: LocalDate, end: LocalDate): String {
        return "${
            start.month.getTitleSuspend().lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ${start.dayOfMonth} - ${end.dayOfMonth}, ${start.year}"
    }

    /**
     * Helper class to hold 4 values since Kotlin doesn't have a built-in Quadruple
     */
    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
} 