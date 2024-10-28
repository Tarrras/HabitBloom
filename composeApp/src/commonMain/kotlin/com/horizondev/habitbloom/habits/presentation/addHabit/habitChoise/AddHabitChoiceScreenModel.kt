package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddHabitChoiceScreenModel(
    private val repository: HabitsRepository,
    private val timeOfDay: TimeOfDay?
) : StateScreenModel<AddHabitChoiceUiState>(AddHabitChoiceUiState()) {

    val uiIntent = MutableSharedFlow<AddHabitChoiceUiIntent>()

    private val searchInput = state
        .onStart {
            mutableState.update { it.copy(isLoading = true) }
            repository.initData()
            mutableState.update { it.copy(isLoading = false) }
        }
        .map { it.searchInput }
        .debounce(500)
        .onEach {
            mutableState.update { it.copy(isLoading = true) }
            val habits = repository.getHabits(it, timeOfDay = timeOfDay ?: TimeOfDay.Morning)
            mutableState.update { state -> state.copy(habits = habits, isLoading = false) }
        }
        .launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: AddHabitChoiceUiEvent) {
        when (uiEvent) {
            is AddHabitChoiceUiEvent.PerformSearch -> mutableState.update {
                it.copy(searchInput = uiEvent.input)
            }

            is AddHabitChoiceUiEvent.SubmitHabit -> {
                screenModelScope.launch {
                    uiIntent.emit(AddHabitChoiceUiIntent.NavigateNext(uiEvent.info))
                }
            }

            AddHabitChoiceUiEvent.CreatePersonalHabit -> {
                timeOfDay?.let {
                    screenModelScope.launch {
                        uiIntent.emit(AddHabitChoiceUiIntent.NavigateToHabitCreation(timeOfDay))
                    }
                }
            }
        }
    }

}