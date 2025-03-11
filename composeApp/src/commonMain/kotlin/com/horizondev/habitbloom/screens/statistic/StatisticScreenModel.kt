package com.horizondev.habitbloom.screens.statistic

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
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
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DayOfWeek

class StatisticScreenModel(
    private val repository: HabitsRepository
) : StateScreenModel<StatisticUiState>(StatisticUiState()) {

    private val filteredHabitFlow = combine(
        state.map { it.selectedTimeUnit }.distinctUntilChanged(),
        repository.getListOfAllUserHabitRecordsFlow()
    ) { selectedTimeUnit, habitRecords ->
        handleHabitsList(
            habitRecords = habitRecords,
            selectedTimeUnit = selectedTimeUnit
        )
    }.onStart {
        mutableState.update { it.copy(isLoading = true) }
    }.onCompletion {
        mutableState.update { it.copy(isLoading = false) }
    }.catch {
        mutableState.update { it.copy(isLoading = false) }
    }.launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: StatisticUiEvent) {
        when (uiEvent) {
            is StatisticUiEvent.SelectTimeUnit -> {
                mutableState.update { it.copy(selectedTimeUnit = uiEvent.timeUnit) }
            }

            is StatisticUiEvent.OpenHabitDetails -> TODO()
        }
    }

    private fun handleHabitsList(
        habitRecords: List<UserHabitRecordFullInfo>,
        selectedTimeUnit: TimeUnit
    ) {
        val completedHabits = habitRecords.filter { it.isCompleted }
        val completeHabitsByTimeOfDay = handleGeneralHabitStatistic(
            completedHabits = completedHabits,
            selectedTimeUnit = selectedTimeUnit
        )
        val completedWeeklyHabits = handleWeeklyHabitStatistic(
            completedHabits = completedHabits
        )

        mutableState.update {
            it.copy(
                isLoading = false,
                completeHabitsByTimeOfDay = completeHabitsByTimeOfDay,
                completedHabitsThisWeek = completedWeeklyHabits,
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

    private fun handleWeeklyHabitStatistic(
        completedHabits: List<UserHabitRecordFullInfo>,
    ): Map<DayOfWeek, Int> {
        val currentDate = getCurrentDate()
        val startOfWeek = currentDate.calculateStartOfWeek()
        val endOfWeek = startOfWeek.plusDays(6)

        val completedHabitsFiltered = completedHabits
            .asSequence()
            .filter {
                it.date in startOfWeek..endOfWeek
            }.sortedBy {
                it.date
            }

        val weekDaysWithCompletedHabits = DayOfWeek.entries.associateWith { day ->
            val date = startOfWeek.plusDays(day.ordinal.toLong())
            completedHabitsFiltered.count { it.date == date }
        }

        return weekDaysWithCompletedHabits
    }
}