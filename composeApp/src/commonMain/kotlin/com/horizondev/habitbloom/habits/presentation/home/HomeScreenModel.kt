package com.horizondev.habitbloom.habits.presentation.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getTimeOfDay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val repository: HabitsRepository
) : StateScreenModel<HomeScreenUiState>(
    HomeScreenUiState(
        selectedTimeOfDay = getTimeOfDay()
    )
) {

    private val _uiIntent = MutableSharedFlow<HomeScreenUiIntent>()
    val uiIntent = _uiIntent.asSharedFlow()

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
                habitsCount = userHabitsCount,
                userCompletedAllHabitsForTimeOfDay = dailyHabits.isNotEmpty()
                        && dailyHabits.all { habit -> habit.isCompleted }
            )
        }
    }.launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: HomeScreenUiEvent) {
        when (uiEvent) {
            is HomeScreenUiEvent.SelectTimeOfDay -> {
                mutableState.update { it.copy(selectedTimeOfDay = uiEvent.timeOfDay) }
            }

            is HomeScreenUiEvent.ChangeHabitCompletionStatus -> {
                screenModelScope.launch {
                    repository.updateHabitCompletion(
                        isCompleted = uiEvent.isCompleted,
                        habitRecordId = uiEvent.id,
                        date = getCurrentDate()
                    )
                }
            }

            is HomeScreenUiEvent.OpenHabitDetails -> {
                screenModelScope.launch {
                    _uiIntent.emit(HomeScreenUiIntent.OpenHabitDetails(uiEvent.userHabitId))
                }
            }
        }
    }
}