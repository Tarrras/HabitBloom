package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay

data class AddHabitChoiceUiState(
    val habits: List<HabitInfo> = emptyList(),
    val searchInput: String = "",
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val habitToDelete: HabitInfo? = null
)

sealed interface AddHabitChoiceUiEvent {
    data class UpdateSearchInput(val input: String) : AddHabitChoiceUiEvent
    data class SelectHabit(val habit: HabitInfo) : AddHabitChoiceUiEvent
    data class DeleteHabit(val habit: HabitInfo) : AddHabitChoiceUiEvent
    data object ConfirmDeleteHabit : AddHabitChoiceUiEvent
    data object CancelDeleteHabit : AddHabitChoiceUiEvent

    data object CreateCustomHabit : AddHabitChoiceUiEvent
    data object NavigateBack : AddHabitChoiceUiEvent
}

sealed interface AddHabitChoiceUiIntent {
    data class NavigateNext(val info: HabitInfo): AddHabitChoiceUiIntent
    data class NavigateToCreateCustomHabit(val timeOfDay: TimeOfDay) : AddHabitChoiceUiIntent
    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : AddHabitChoiceUiIntent

    data object NavigateBack : AddHabitChoiceUiIntent
}
