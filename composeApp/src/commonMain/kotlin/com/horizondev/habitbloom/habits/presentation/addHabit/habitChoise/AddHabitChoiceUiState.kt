package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import com.horizondev.habitbloom.habits.domain.models.HabitInfo

data class AddHabitChoiceUiState(
    val habits: List<HabitInfo> = emptyList(),
    val searchInput: String = "",
    val isLoading: Boolean = true
)

sealed interface AddHabitChoiceUiEvent {
    data class PerformSearch(val input: String): AddHabitChoiceUiEvent
    data class SubmitHabit(val info: HabitInfo): AddHabitChoiceUiEvent
}

sealed interface AddHabitChoiceUiIntent {
    data class NavigateNext(val info: HabitInfo): AddHabitChoiceUiIntent
}
