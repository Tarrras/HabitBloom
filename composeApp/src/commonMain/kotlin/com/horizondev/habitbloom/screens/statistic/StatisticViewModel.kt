package com.horizondev.habitbloom.screens.statistic

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.statistic.domain.models.MonthlyStatistic
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticPeriodAggregate
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticPeriodRange
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticSummaryStats
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticTimeUnit
import com.horizondev.habitbloom.screens.statistic.domain.models.WeeklyStatistic
import com.horizondev.habitbloom.screens.statistic.domain.models.YearlyStatistic
import com.horizondev.habitbloom.screens.statistic.domain.usecases.BuildStatisticSummaryUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.CalculateStatisticPeriodAggregateUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.CalculateStatisticTimeOfDayUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.GetStatisticPeriodRangeUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.ObserveStatisticHabitRecordsUseCase
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitleSuspend
import com.horizondev.habitbloom.utils.getTitleSuspend
import com.horizondev.habitbloom.utils.minusMonths
import com.horizondev.habitbloom.utils.minusYears
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

/**
 * ViewModel for the Statistics screen.
 */
class StatisticViewModel(
    private val observeStatisticHabitRecords: ObserveStatisticHabitRecordsUseCase,
    private val getStatisticPeriodRange: GetStatisticPeriodRangeUseCase,
    private val calculateStatisticTimeOfDay: CalculateStatisticTimeOfDayUseCase,
    private val calculateStatisticPeriodAggregate: CalculateStatisticPeriodAggregateUseCase,
    private val buildStatisticSummary: BuildStatisticSummaryUseCase
) : BloomViewModel<StatisticUiState, StatisticUiIntent>(
    StatisticUiState(isLoading = true)
) {

    private val filteredHabitFlow = combine(
        state.map { it.selectedTimeUnit }.distinctUntilChanged(),
        state.map { it.selectedPeriodOffset }.distinctUntilChanged(),
        observeStatisticHabitRecords()
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
            is StatisticUiEvent.SelectTimeUnit -> {
                updateState {
                    it.copy(
                        selectedTimeUnit = uiEvent.timeUnit,
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
        val periodLabel = formatPeriodLabel(
            selectedTimeUnit = currentState.selectedTimeUnit,
            periodOffset = currentState.selectedPeriodOffset,
            periodRange = getStatisticPeriodRange(
                selectedTimeUnit = currentState.selectedTimeUnit.toDomain(),
                periodOffset = currentState.selectedPeriodOffset
            )
        )

        updateState { it.copy(selectedPeriodLabel = periodLabel) }
    }

    private fun handleHabitsList(
        habitRecords: List<UserHabitRecordFullInfo>,
        selectedTimeUnit: TimeUnit,
        selectedPeriodOffset: Int
    ) = viewModelScope.launch {
        val completedHabits = habitRecords.filter { it.isCompleted }
        val periodRange = getStatisticPeriodRange(
            selectedTimeUnit = selectedTimeUnit.toDomain(),
            periodOffset = selectedPeriodOffset
        )
        val periodHabitRecords = habitRecords.filter {
            it.date in periodRange.startDate..periodRange.endDate
        }
        val completeHabitsByTimeOfDay = calculateStatisticTimeOfDay(
            completedHabits = completedHabits,
            periodRange = periodRange
        )

        val periodData = mapStatisticPeriodAggregate(
            calculateStatisticPeriodAggregate(
                habitRecords = habitRecords,
                periodRange = periodRange,
                selectedTimeUnit = selectedTimeUnit.toDomain()
            )
        )

        updateState {
            it.copy(
                isLoading = false,
                completeHabitsByTimeOfDay = completeHabitsByTimeOfDay,
                completedHabitsByPeriod = periodData.completedByPeriod,
                scheduledHabitsByPeriod = periodData.scheduledByPeriod,
                selectedPeriodLabel = periodData.periodLabel,
                formattedChartData = periodData.chartData,
                summary = buildStatisticSummary(
                    periodHabitRecords = periodHabitRecords,
                    completedByTimeOfDay = completeHabitsByTimeOfDay
                ).toUiState(),
                userHasAnyCompleted = completedHabits.isNotEmpty()
            )
        }
    }

    private suspend fun mapStatisticPeriodAggregate(
        aggregate: StatisticPeriodAggregate
    ): StatisticPeriodUiData {
        return when (aggregate) {
            is StatisticPeriodAggregate.Week -> mapWeeklyStatistic(aggregate.statistic)
            is StatisticPeriodAggregate.Month -> mapMonthlyStatistic(aggregate.statistic)
            is StatisticPeriodAggregate.Year -> mapYearlyStatistic(aggregate.statistic)
        }
    }

    private suspend fun mapWeeklyStatistic(statistic: WeeklyStatistic): StatisticPeriodUiData {
        val categories = DayOfWeek.entries.map { it.getShortTitleSuspend() }
        val completedData = statistic.completedByDay.mapKeys { it.key.getShortTitleSuspend() }
        val scheduledData = statistic.scheduledByDay.mapKeys { it.key.getShortTitleSuspend() }
        val label = formatDateRange(statistic.periodRange.startDate, statistic.periodRange.endDate)

        return StatisticPeriodUiData(
            completedByPeriod = completedData,
            scheduledByPeriod = scheduledData,
            periodLabel = label,
            chartData = ChartData.WeekData(
                weeklyCategories = categories,
                weeklyCompletedData = completedData,
                weeklyScheduledData = scheduledData
            )
        )
    }

    private suspend fun mapMonthlyStatistic(statistic: MonthlyStatistic): StatisticPeriodUiData {
        val monthLabel = statistic.periodRange.startDate.month.getTitleSuspend()
        val monthShortLabel = statistic.periodRange.startDate.month.getShortTitleSuspend()
        val chartData = ChartData.MonthData(
            monthlyCategories = statistic.completedByDay.keys.map { it.toString() },
            monthlyCompletedData = statistic.completedByDay.mapKeys { it.key.toString() },
            monthlyScheduledData = statistic.scheduledByDay.mapKeys { it.key.toString() },
            monthLabel = monthLabel,
            xAxisStartLabel = "${statistic.periodRange.startDate.day} $monthShortLabel",
            xAxisEndLabel = "${statistic.periodRange.endDate.day} $monthShortLabel"
        )

        return StatisticPeriodUiData(
            completedByPeriod = chartData.monthlyCompletedData,
            scheduledByPeriod = chartData.monthlyScheduledData,
            periodLabel = monthLabel,
            chartData = chartData
        )
    }

    private suspend fun mapYearlyStatistic(statistic: YearlyStatistic): StatisticPeriodUiData {
        val monthCategories = Month.entries.map { it.getShortTitleSuspend() }
        val completedData = statistic.completedByMonth.mapKeys { it.key.getShortTitleSuspend() }
        val scheduledData = statistic.scheduledByMonth.mapKeys { it.key.getShortTitleSuspend() }

        return StatisticPeriodUiData(
            completedByPeriod = completedData,
            scheduledByPeriod = scheduledData,
            periodLabel = statistic.year.toString(),
            chartData = ChartData.YearData(
                yearlyCategories = monthCategories,
                yearlyCompletedData = completedData,
                yearlyScheduledData = scheduledData
            )
        )
    }

    private suspend fun formatPeriodLabel(
        selectedTimeUnit: TimeUnit,
        periodOffset: Int,
        periodRange: StatisticPeriodRange
    ): String {
        val currentDate = getCurrentDate()
        return when (selectedTimeUnit) {
            TimeUnit.WEEK -> formatDateRange(periodRange.startDate, periodRange.endDate)

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
    }

    private suspend fun formatDateRange(start: LocalDate, end: LocalDate): String {
        return "${
            start.month.getTitleSuspend().lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ${start.day} - ${end.day}, ${start.year}"
    }

    private fun StatisticSummaryStats.toUiState(): StatisticSummary {
        return StatisticSummary(
            completedHabits = completedHabits,
            longestStreak = longestStreak,
            averageCompletionRate = averageCompletionRate,
            bestHabitName = bestHabitName,
            bestHabitCompletionRate = bestHabitCompletionRate,
            timeOfDayCompletionRates = timeOfDayCompletionRates
        )
    }

    private data class StatisticPeriodUiData(
        val completedByPeriod: Map<String, Int>,
        val scheduledByPeriod: Map<String, Int>,
        val periodLabel: String,
        val chartData: ChartData
    )
}

private fun TimeUnit.toDomain(): StatisticTimeUnit {
    return when (this) {
        TimeUnit.WEEK -> StatisticTimeUnit.WEEK
        TimeUnit.MONTH -> StatisticTimeUnit.MONTH
        TimeUnit.YEAR -> StatisticTimeUnit.YEAR
    }
}
