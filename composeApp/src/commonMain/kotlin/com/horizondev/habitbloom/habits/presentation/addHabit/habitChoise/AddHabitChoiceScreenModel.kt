package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class AddHabitChoiceScreenModel(
    private val repository: HabitsRepository,
    private val timeOfDay: TimeOfDay?
) : StateScreenModel<AddHabitChoiceUiState>(AddHabitChoiceUiState()) {

    private val searchInput = state
        .onStart { repository.initData() }
        .map { it.searchInput }
        .debounce(500)
        .onEach {
            val habits = repository.getHabits(it, timeOfDay = timeOfDay ?: TimeOfDay.Morning)
            mutableState.update { state -> state.copy(habits = habits) }
        }
        .launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: AddHabitChoiceUiEvent) {
        when (uiEvent) {
            is AddHabitChoiceUiEvent.PerformSearch -> mutableState.update {
                it.copy(searchInput = uiEvent.input)
            }
        }
    }

}