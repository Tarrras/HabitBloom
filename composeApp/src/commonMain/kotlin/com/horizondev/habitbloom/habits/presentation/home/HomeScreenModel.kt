package com.horizondev.habitbloom.habits.presentation.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getTimeOfDay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class HomeScreenModel(
    private val repository: HabitsRepository
) : StateScreenModel<HomeScreenUiState>(
    HomeScreenUiState(
        selectedTimeOfDay = getTimeOfDay()
    )
) {

    private val selectedTimeOfDayFlow = state.map {
        it.selectedTimeOfDay
    }.distinctUntilChanged()

    private val habitsFlow = combine(
        selectedTimeOfDayFlow,
        repository.getUserHabitsByDayFlow(getCurrentDate())
    ) { selectedTimeOfDay, userHabits ->
        Triple(
            first = userHabits.size,
            second = userHabits.count { it.isCompleted },
            third = userHabits.filter { it.timeOfDay == selectedTimeOfDay }
        )
    }.onStart {
        repository.initData()
    }.onEach { (userHabitsCount, completedHabitsCount, dailyHabits) ->
        mutableState.update {
            it.copy(
                userHabits = dailyHabits,
                completedHabitsCount = completedHabitsCount,
                habitsCount = userHabitsCount
            )
        }
    }.launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: HomeScreenUiEvent) {
        when (uiEvent) {
            is HomeScreenUiEvent.SelectTimeOfDay -> {
                mutableState.update { it.copy(selectedTimeOfDay = uiEvent.timeOfDay) }
            }

            is HomeScreenUiEvent.ChangeHabitCompletionStatus -> {}
        }
    }
}