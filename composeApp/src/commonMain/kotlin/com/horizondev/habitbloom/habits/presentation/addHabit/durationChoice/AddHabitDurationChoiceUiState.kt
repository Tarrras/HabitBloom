package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import kotlinx.datetime.DayOfWeek

data class AddHabitDurationChoiceUiState(
    val activeDays: List<DayOfWeek> = emptyList(),
    val duration: Int = 1,
) {
    val nextButtonEnabled: Boolean = activeDays.isEmpty().not()
}

sealed interface AddHabitDurationChoiceUiIntent {
    data object NavigateBack: AddHabitDurationChoiceUiIntent
    data class NavigateToSummary(
        val selectedDays: List<DayOfWeek>,
        val selectedDuration: Int
    ): AddHabitDurationChoiceUiIntent
}

sealed interface AddHabitDurationChoiceUiEvent {
    data class UpdateDayState(
        val dayOfWeek: DayOfWeek,
        val isActive: Boolean
    ) : AddHabitDurationChoiceUiEvent

    data class SelectGroupOfDays(val group: GroupOfDays) : AddHabitDurationChoiceUiEvent
    data class DurationChanged(val duration: Int) : AddHabitDurationChoiceUiEvent

    data object OnNext: AddHabitDurationChoiceUiEvent
    data object Cancel: AddHabitDurationChoiceUiEvent
}