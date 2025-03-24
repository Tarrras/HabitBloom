package com.horizondev.habitbloom.screens.habits.presentation.createHabit.details

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.platform.ImagePickerResult
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL

data class CreatePersonalHabitUiState(
    val timeOfDay: TimeOfDay = TimeOfDay.Morning,
    val title: String = "",
    val isTitleInputError: Boolean = false,
    val description: String = "",
    val isDescriptionInputError: Boolean = false,
    val showCreateHabitDialog: Boolean = false,
    val isLoading: Boolean = false,
    val selectedImageUrl: String = DEFAULT_PHOTO_URL,
    val imagePickerState: ImagePickerResult = ImagePickerResult.None
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
    data object PickImage : CreatePersonalHabitUiEvent
}

sealed interface CreatePersonalHabitUiIntent {
    data object NavigateBack : CreatePersonalHabitUiIntent
    data object OpenSuccessScreen : CreatePersonalHabitUiIntent

    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : CreatePersonalHabitUiIntent
}
