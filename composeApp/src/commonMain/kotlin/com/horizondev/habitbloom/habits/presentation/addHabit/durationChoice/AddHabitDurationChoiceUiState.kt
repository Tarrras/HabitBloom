package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import kotlinx.datetime.DayOfWeek

data class AddHabitDurationChoiceUiState(
    val activeDays: List<DayOfWeek> = emptyList()
)

sealed interface AddHabitDurationChoiceUiEvent {
    data class UpdateDayState(
        val dayOfWeek: DayOfWeek,
        val isActive: Boolean
    ) : AddHabitDurationChoiceUiEvent

    data class SelectGroupOfDays(val group: GroupOfDays) : AddHabitDurationChoiceUiEvent
}