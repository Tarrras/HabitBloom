package com.horizondev.habitbloom.habits.presentation.createHabit

import com.horizondev.habitbloom.habits.domain.models.TimeOfDay

data class CreatePersonalHabitUiState(
    val title: String = "",
    val isTitleInputError: Boolean = false,
    val description: String = "",
    val isDescriptionInputError: Boolean = false,
    val timeOfDay: TimeOfDay = TimeOfDay.Morning,
    val nextButtonEnabled: Boolean = false
)

sealed interface CreatePersonalHabitUiEvent {
    data class UpdateTitle(val input: String) : CreatePersonalHabitUiEvent
    data class UpdateDescription(val input: String) : CreatePersonalHabitUiEvent
    data class UpdateTimeOfDay(val timeOfDay: TimeOfDay) : CreatePersonalHabitUiEvent

    data object NavigateBack : CreatePersonalHabitUiEvent
}

sealed interface CreatePersonalHabitUiIntent {
    data object NavigateBack : CreatePersonalHabitUiIntent
}
