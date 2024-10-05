package com.horizondev.habitbloom.statistic

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.minusDays
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class StatisticScreenModel(
    private val repository: HabitsRepository
) : StateScreenModel<StatisticUiState>(StatisticUiState()) {

    private val habitsFlow = flow {
        emit(repository.getListOfAllUserHabitRecords())
    }

    private val filteredHabitFlow = combine(
        state.map { it.selectedTimeUnit }.distinctUntilChanged(),
        habitsFlow
    ) { selectedTimeUnit, habitResult ->
        habitResult.onSuccess { habitRecords ->
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

            val completedHabits = habitRecords.filter { it.isCompleted }
            val completedHabitsFiltered = habitRecords
                .asSequence()
                .filter { it.isCompleted && it.date in startDate..endDate }
                .groupBy { it.timeOfDay }
                .mapValues { (_, value) -> value.size }

            val result = TimeOfDay.entries
                .sortedBy { it.ordinal }
                .associateWith { completedHabitsFiltered[it] ?: 0 }

            mutableState.update {
                it.copy(
                    isLoading = false,
                    completeHabitsCountData = result,
                    userHasAnyCompleted = completedHabits.isNotEmpty()
                )
            }
        }.onFailure {
            mutableState.update { it.copy(isLoading = false) }
        }
    }.onStart {
        mutableState.update { it.copy(isLoading = true) }
    }.onCompletion {
        mutableState.update { it.copy(isLoading = false) }
    }.launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: StatisticUiEvent) {
        when (uiEvent) {
            is StatisticUiEvent.SelectTimeUnit -> {
                mutableState.update { it.copy(selectedTimeUnit = uiEvent.timeUnit) }
            }
        }
    }
}