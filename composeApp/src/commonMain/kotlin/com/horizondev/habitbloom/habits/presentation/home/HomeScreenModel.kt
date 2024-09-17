package com.horizondev.habitbloom.habits.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class HomeScreenModel(
    private val repository: HabitsRepository
) : StateScreenModel<HomeScreenUiState>(HomeScreenUiState()) {

    private val selectedTimeOfDay = state.map {
        it.selectedTimeOfDay
    }.distinctUntilChanged()

    private val habitsFlow = selectedTimeOfDay.flatMapLatest { state ->
        repository.getUserHabitsByDayFlow(getCurrentDate()).map { items ->
            items.filter { it.timeOfDay == state }
        }
    }.onEach { items ->
        mutableState.update { it.copy(userHabits = items) }
    }.launchIn(screenModelScope)
}