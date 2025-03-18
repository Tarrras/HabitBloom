package com.horizondev.habitbloom.screens.statistic

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.minusDays
import com.horizondev.habitbloom.utils.plusDays
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * ViewModel for the Statistics screen.
 */
class StatisticViewModel(
    private val repository: HabitsRepository
) : BloomViewModel<StatisticUiState, StatisticUiIntent>(
    StatisticUiState(isLoading = true)
) {
    init {
        // Initialize the week label on creation
        val currentDate = getCurrentDate()
        val startOfWeek = currentDate.calculateStartOfWeek()
        val endOfWeek = startOfWeek.plusDays(6)
        val initialWeekLabel = formatDateRange(startOfWeek, endOfWeek)

        updateState { it.copy(selectedWeekLabel = initialWeekLabel) }
    }
    
    private val filteredHabitFlow = combine(
        state.map { it.selectedTimeUnit }.distinctUntilChanged(),
        state.map { it.selectedWeekOffset }.distinctUntilChanged(),
        repository.getListOfAllUserHabitRecordsFlow()
    ) { selectedTimeUnit, selectedWeekOffset, habitRecords ->
        handleHabitsList(
            habitRecords = habitRecords,
            selectedTimeUnit = selectedTimeUnit,
            selectedWeekOffset = selectedWeekOffset
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
                updateState { it.copy(selectedTimeUnit = uiEvent.timeUnit) }
            }

            is StatisticUiEvent.PreviousWeek -> {
                updateState { it.copy(selectedWeekOffset = it.selectedWeekOffset - 1) }
            }

            is StatisticUiEvent.NextWeek -> {
                // Don't allow future weeks
                updateState {
                    if (it.selectedWeekOffset < 0) {
                        it.copy(selectedWeekOffset = it.selectedWeekOffset + 1)
                    } else {
                        it
                    }
                }
            }

            is StatisticUiEvent.CurrentWeek -> {
                updateState { it.copy(selectedWeekOffset = 0) }
            }

            is StatisticUiEvent.OpenHabitDetails -> TODO()
        }
    }

    private fun handleHabitsList(
        habitRecords: List<UserHabitRecordFullInfo>,
        selectedTimeUnit: TimeUnit,
        selectedWeekOffset: Int
    ) {
        val completedHabits = habitRecords.filter { it.isCompleted }
        val completeHabitsByTimeOfDay = handleGeneralHabitStatistic(
            completedHabits = completedHabits,
            selectedTimeUnit = selectedTimeUnit
        )

        val weeklyStatisticResult = handleWeeklyHabitStatistic(
            completedHabits = completedHabits,
            weekOffset = selectedWeekOffset
        )

        updateState {
            it.copy(
                isLoading = false,
                completeHabitsByTimeOfDay = completeHabitsByTimeOfDay,
                completedHabitsThisWeek = weeklyStatisticResult.first,
                selectedWeekLabel = weeklyStatisticResult.second,
                userHasAnyCompleted = completedHabits.isNotEmpty()
            )
        }
    }

    private fun handleGeneralHabitStatistic(
        completedHabits: List<UserHabitRecordFullInfo>,
        selectedTimeUnit: TimeUnit
    ): Map<TimeOfDay, Int> {
        val currentDate = getCurrentDate()
        val endDate = getCurrentDate()
        val startDate = when (selectedTimeUnit) {
            TimeUnit.WEEK -> {
                currentDate.calculateStartOfWeek()
            }

            TimeUnit.MONTH -> {
                currentDate.minusDays(currentDate.dayOfMonth.toLong())
            }

            TimeUnit.YEAR -> {
                currentDate.minusDays(currentDate.dayOfYear.toLong())
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
     * Calculate habit statistics for a specific week.
     *
     * @param completedHabits List of completed habits
     * @param weekOffset Offset from current week (0 = current week, -1 = previous week, etc.)
     * @return Pair of (map of day to completed count, formatted date range string)
     */
    private fun handleWeeklyHabitStatistic(
        completedHabits: List<UserHabitRecordFullInfo>,
        weekOffset: Int = 0
    ): Pair<Map<DayOfWeek, Int>, String> {
        val currentDate = getCurrentDate()
        val startOfCurrentWeek = currentDate.calculateStartOfWeek()

        // Calculate the start of the target week by applying the offset
        val startOfTargetWeek = startOfCurrentWeek.minusDays((-weekOffset * 7).toLong())
        val endOfTargetWeek = startOfTargetWeek.plusDays(6)

        // Create a formatted string to display the date range
        val weekLabel = formatDateRange(startOfTargetWeek, endOfTargetWeek)

        val completedHabitsFiltered = completedHabits
            .asSequence()
            .filter {
                it.date in startOfTargetWeek..endOfTargetWeek
            }.sortedBy {
                it.date
            }

        val weekDaysWithCompletedHabits = DayOfWeek.entries.associateWith { day ->
            val date = startOfTargetWeek.plusDays(day.ordinal.toLong())
            completedHabitsFiltered.count { it.date == date }
        }

        return Pair(weekDaysWithCompletedHabits, weekLabel)
    }

    /**
     * Format a date range into a human-readable string.
     */
    private fun formatDateRange(start: LocalDate, end: LocalDate): String {
        return "${
            start.month.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ${start.dayOfMonth} - ${end.dayOfMonth}, ${start.year}"
    }
} 