package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import com.horizondev.habitbloom.habits.domain.models.HabitInfo

data class AddHabitChoiceUiState(
    val habits: List<HabitInfo> = emptyList(),
    val searchInput: String = ""
)

sealed interface AddHabitChoiceUiEvent {
    data class PerformSearch(val input: String): AddHabitChoiceUiEvent
}
