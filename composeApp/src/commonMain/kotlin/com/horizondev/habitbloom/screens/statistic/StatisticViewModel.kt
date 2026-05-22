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
                val timeUnit = when (uiEvent) {
                    is StatisticUiEvent.SelectTimeUnit -> uiEvent.timeUnit
                    is StatisticUiEvent.TimeUnitChanged -> uiEvent.timeUnit
                    else -> return
                }

                updateState {
                    it.copy(
                        selectedTimeUnit = timeUnit,
                        selectedPeriodOffset = 0
                    )
                }

                updatePeriodLabel()
            }

            is StatisticUiEvent.PreviousPeriod -> {
                updateState { it.copy(selectedPeriodOffset = it.selectedPeriodOffset - 1) }
                updatePeriodLabel()
            }

            is StatisticUiEvent.NextPeriod -> {
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

            is StatisticUiEvent.NavigateToAddHabit -> {
                emitUiIntent(StatisticUiIntent.NavigateToAddHabit)
            }
        }
    }

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
        val (periodStartDate, periodEndDate) = getPeriodRange(
            selectedTimeUnit = selectedTimeUnit,
            periodOffset = selectedPeriodOffset
        )
        val periodHabitRecords = habitRecords.filter { it.date in periodStartDate..periodEndDate }
        val completeHabitsByTimeOfDay = handleGeneralHabitStatistic(
            completedHabits = completedHabits,
            selectedTimeUnit = selectedTimeUnit,
            periodOffset = selectedPeriodOffset
        )

        val (completedHabitsByPeriod, scheduledHabitsByPeriod, periodLabel, formattedChartData) = when (selectedTimeUnit) {
            TimeUnit.WEEK -> {
                val weeklyResult = handleWeeklyHabitStatistic(
                    habitRecords = habitRecords,
                    weekOffset = selectedPeriodOffset
                )

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

                Quadruple(
                    monthlyResult.first,
                    monthlyResult.second,
                    monthlyResult.third,
                    monthlyResult.fourth
                )
            }

            TimeUnit.YEAR -> {
                val yearlyResult = handleYearlyHabitStatistic(
                    habitRecords = habitRecords,
                    yearOffset = selectedPeriodOffset
                )

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
                summary = buildStatisticSummary(
                    periodHabitRecords = periodHabitRecords,
                    completedByTimeOfDay = completeHabitsByTimeOfDay,
                    completedByPeriod = completedHabitsByPeriod,
                    scheduledByPeriod = scheduledHabitsByPeriod
                ),
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
                startDate = startOfCurrentWeek.minusDays((-periodOffset * 7).toLong())
                endDate = startDate.plusDays(6)
            }

            TimeUnit.MONTH -> {
                val targetMonth = currentDate.minusMonths(-periodOffset.toLong())
                startDate = LocalDate(targetMonth.year, targetMonth.month, 1)
                endDate = startDate.getEndOfMonth()
            }

            TimeUnit.YEAR -> {
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

    private fun getPeriodRange(
        selectedTimeUnit: TimeUnit,
        periodOffset: Int
    ): Pair<LocalDate, LocalDate> {
        val currentDate = getCurrentDate()
        return when (selectedTimeUnit) {
            TimeUnit.WEEK -> {
                val startOfCurrentWeek = currentDate.calculateStartOfWeek()
                val startDate = startOfCurrentWeek.minusDays((-periodOffset * 7).toLong())
                startDate to startDate.plusDays(6)
            }

            TimeUnit.MONTH -> {
                val targetMonth = currentDate.minusMonths(-periodOffset.toLong())
                val startDate = LocalDate(targetMonth.year, targetMonth.month, 1)
                startDate to startDate.getEndOfMonth()
            }

            TimeUnit.YEAR -> {
                val targetYear = currentDate.year + periodOffset
                LocalDate(targetYear, Month.JANUARY, 1) to LocalDate(targetYear, Month.DECEMBER, 31)
            }
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private suspend fun handleWeeklyHabitStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        weekOffset: Int = 0
    ): Triple<Map<DayOfWeek, Int>, Map<DayOfWeek, Int>, String> {
        val currentDate = getCurrentDate()
        val startOfCurrentWeek = currentDate.calculateStartOfWeek()

        val startOfTargetWeek = startOfCurrentWeek.minusDays((-weekOffset * 7).toLong())
        val endOfTargetWeek = startOfTargetWeek.plusDays(6)

        val weekLabel = formatDateRange(startOfTargetWeek, endOfTargetWeek)

        val habitFilteredForGivenWeek = habitRecords
            .asSequence()
            .filter {
                it.date in startOfTargetWeek..endOfTargetWeek
            }
        val completedHabitFilteredForGivenWeek = habitFilteredForGivenWeek.filter { it.isCompleted }

        val weekDaysWithAllScheduledHabits = DayOfWeek.entries.associateWith { day ->
            val date = startOfTargetWeek.plusDays(day.ordinal.toLong())
            habitFilteredForGivenWeek.count { it.date == date }
        }

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

    private suspend fun handleMonthlyHabitStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        monthOffset: Int = 0
    ): Quadruple<Map<String, Int>, Map<String, Int>, String, ChartData.MonthData> {
        val currentDate = getCurrentDate()
        val targetMonthDate = currentDate.minusMonths(-monthOffset.toLong())

        val startOfMonth = LocalDate(targetMonthDate.year, targetMonthDate.month, 1)
        val endOfMonth = targetMonthDate.getEndOfMonth()

        val monthName = targetMonthDate.month.getTitleSuspend()
        val monthShortName = targetMonthDate.month.getShortTitleSuspend()

        val habitsFilteredForGivenMonth = habitRecords
            .filter { it.date in startOfMonth..endOfMonth }

        val formattedChartData = buildMonthlyChartData(
            habitRecords = habitsFilteredForGivenMonth,
            startOfMonth = startOfMonth,
            endOfMonth = endOfMonth,
            monthLabel = monthName,
            monthShortLabel = monthShortName
        )

        return Quadruple(
            formattedChartData.monthlyCompletedData,
            formattedChartData.monthlyScheduledData,
            monthName,
            formattedChartData
        )
    }

    private suspend fun handleYearlyHabitStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        yearOffset: Int = 0
    ): Triple<Map<String, Int>, Map<String, Int>, String> {
        val currentDate = getCurrentDate()

        val targetYear = currentDate.year + yearOffset
        val yearLabel = targetYear.toString()

        val startOfYear = LocalDate(targetYear, Month.JANUARY, 1)
        val endOfYear = LocalDate(targetYear, Month.DECEMBER, 31)

        val habitsFilteredForGivenYear = habitRecords
            .asSequence()
            .filter { it.date in startOfYear..endOfYear }

        val completedHabitsFilteredForGivenYear =
            habitsFilteredForGivenYear.filter { it.isCompleted }

        val monthlyCompletedHabits = mutableMapOf<String, Int>()
        val monthlyScheduledHabits = mutableMapOf<String, Int>()

        val monthAbbreviations = Month.entries.map { it.getShortTitleSuspend() }

        for (monthAbbr in monthAbbreviations) {
            monthlyCompletedHabits[monthAbbr] = 0
            monthlyScheduledHabits[monthAbbr] = 0
        }

        for (monthOrdinal in 1..12) {
            val month = Month(monthOrdinal)
            val monthAbbr = monthAbbreviations[monthOrdinal - 1]

            val daysInMonth = when (month) {
                Month.FEBRUARY -> if (isLeapYear(targetYear)) 29 else 28
                Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
                else -> 31
            }

            for (day in 1..daysInMonth) {
                val date = LocalDate(targetYear, month, day)

                val scheduledCount = habitsFilteredForGivenYear.count { it.date == date }
                monthlyScheduledHabits[monthAbbr] =
                    (monthlyScheduledHabits[monthAbbr] ?: 0) + scheduledCount

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

    private suspend fun formatDateRange(start: LocalDate, end: LocalDate): String {
        return "${
            start.month.getTitleSuspend().lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ${start.day} - ${end.day}, ${start.year}"
    }

    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
} 
