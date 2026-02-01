package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo


data class AddHabitChoiceUiState(
    val habits: List<HabitInfo> = emptyList(),
    val searchInput: String = "",
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val habitToDelete: HabitInfo? = null,
    val currentCategory: HabitCategoryData? = null
)

sealed interface AddHabitChoiceUiEvent {
    data class UpdateSearchInput(val input: String) : AddHabitChoiceUiEvent
    data class SelectHabit(val habit: HabitInfo) : AddHabitChoiceUiEvent
    data class DeleteHabit(val habit: HabitInfo) : AddHabitChoiceUiEvent

    data object ConfirmDeleteHabit : AddHabitChoiceUiEvent
    data object CancelDeleteHabit : AddHabitChoiceUiEvent
    data object CreateCustomHabit : AddHabitChoiceUiEvent
    data object NavigateBack : AddHabitChoiceUiEvent
    data object RefreshPage : AddHabitChoiceUiEvent
}

sealed interface AddHabitChoiceUiIntent {
    data class NavigateNext(val info: HabitInfo): AddHabitChoiceUiIntent
    data class NavigateToCreateCustomHabit(val categoryId: String?) : AddHabitChoiceUiIntent
    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : AddHabitChoiceUiIntent

    data object NavigateBack : AddHabitChoiceUiIntent
}
