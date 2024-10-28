package com.horizondev.habitbloom.habits.presentation.createHabit

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePersonalHabitScreenModel(
    timeOfDay: TimeOfDay?
) : StateScreenModel<CreatePersonalHabitUiState>(
    CreatePersonalHabitUiState(
        timeOfDay = timeOfDay ?: TimeOfDay.Morning
    )
) {

    private val _uiIntent = MutableSharedFlow<CreatePersonalHabitUiIntent>()
    val uiIntent = _uiIntent.asSharedFlow()

    fun handleUiEvent(uiEvent: CreatePersonalHabitUiEvent) {
        when (uiEvent) {
            CreatePersonalHabitUiEvent.NavigateBack -> {
                screenModelScope.launch {
                    _uiIntent.emit(CreatePersonalHabitUiIntent.NavigateBack)
                }
            }

            is CreatePersonalHabitUiEvent.UpdateDescription -> {
                val input = uiEvent.input
                mutableState.update {
                    it.copy(
                        description = input,
                        isDescriptionInputError = input.length > HABIT_DESCRIPTION_MAX_LENGTH
                    )
                }
            }

            is CreatePersonalHabitUiEvent.UpdateTimeOfDay -> {
                mutableState.update { it.copy(timeOfDay = uiEvent.timeOfDay) }
            }

            is CreatePersonalHabitUiEvent.UpdateTitle -> {
                val input = uiEvent.input
                mutableState.update {
                    it.copy(
                        title = input,
                        isTitleInputError = input.length > HABIT_TITLE_MAX_LENGTH
                    )
                }
            }
        }
    }
}