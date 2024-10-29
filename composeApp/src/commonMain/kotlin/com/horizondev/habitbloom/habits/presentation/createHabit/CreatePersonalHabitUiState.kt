package com.horizondev.habitbloom.habits.presentation.createHabit

import com.horizondev.habitbloom.habits.domain.models.TimeOfDay

data class CreatePersonalHabitUiState(
    val timeOfDay: TimeOfDay = TimeOfDay.Morning,
    val title: String = "",
    val isTitleInputError: Boolean = false,
    val description: String = "",
    val isDescriptionInputError: Boolean = false,
    val showCreateHabitDialog: Boolean = false
) {
    val nextButtonEnabled: Boolean = title.isNotEmpty()
            && isTitleInputError.not()
            && description.isNotEmpty()
            && isDescriptionInputError.not()
}

sealed interface CreatePersonalHabitUiEvent {
    data class UpdateTitle(val input: String) : CreatePersonalHabitUiEvent
    data class UpdateDescription(val input: String) : CreatePersonalHabitUiEvent
    data class UpdateTimeOfDay(val timeOfDay: TimeOfDay) : CreatePersonalHabitUiEvent

    data object NavigateBack : CreatePersonalHabitUiEvent
    data object CreateHabit : CreatePersonalHabitUiEvent
    data object SubmitHabitCreation : CreatePersonalHabitUiEvent
    data object HideCreateHabitDialog : CreatePersonalHabitUiEvent
}

sealed interface CreatePersonalHabitUiIntent {
    data object NavigateBack : CreatePersonalHabitUiIntent
}
