package com.horizondev.habitbloom.screens.habits.presentation.createHabit.details

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL

data class CreatePersonalHabitUiState(
    val title: String = "",
    val isTitleInputError: Boolean = false,
    val description: String = "",
    val isDescriptionInputError: Boolean = false,
    val showCreateHabitDialog: Boolean = false,
    val isLoading: Boolean = false,
    val selectedImageUrl: String = DEFAULT_PHOTO_URL,
    val availableIcons: List<String> = emptyList(),
    val isLoadingIcons: Boolean = false
) {
    val nextButtonEnabled: Boolean = title.isNotEmpty()
            && isTitleInputError.not()
            && isDescriptionInputError.not()
            && selectedImageUrl.isNotEmpty()
}

sealed interface CreatePersonalHabitUiEvent {
    data class UpdateTitle(val input: String) : CreatePersonalHabitUiEvent
    data class UpdateDescription(val input: String) : CreatePersonalHabitUiEvent
    data class SelectIcon(val iconUrl: String) : CreatePersonalHabitUiEvent

    data object NavigateBack : CreatePersonalHabitUiEvent
    data object CreateHabit : CreatePersonalHabitUiEvent
    data object SubmitHabitCreation : CreatePersonalHabitUiEvent
    data object HideCreateHabitDialog : CreatePersonalHabitUiEvent
}

sealed interface CreatePersonalHabitUiIntent {
    data object NavigateBack : CreatePersonalHabitUiIntent
    data object OpenSuccessScreen : CreatePersonalHabitUiIntent

    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : CreatePersonalHabitUiIntent
}
