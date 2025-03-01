package com.horizondev.habitbloom.habits.presentation.createHabit.details

import androidx.compose.material3.SnackbarDuration
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.platform.ImagePicker
import com.horizondev.habitbloom.platform.ImagePickerResult
import com.horizondev.habitbloom.profile.domain.ProfileRepository
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.save_habit_error
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class CreatePersonalHabitScreenModel(
    private val habitRepository: HabitsRepository,
    private val profileRepository: ProfileRepository,
    private val imagePicker: ImagePicker,
    timeOfDay: TimeOfDay?
) : StateScreenModel<CreatePersonalHabitUiState>(
    CreatePersonalHabitUiState(
        timeOfDay = timeOfDay ?: TimeOfDay.Morning
    )
) {
    private val _uiIntent = MutableSharedFlow<CreatePersonalHabitUiIntent>()
    val uiIntent = _uiIntent.asSharedFlow()

    init {
        imagePicker.imagePickerResult
            .onEach { result ->
                mutableState.update { it.copy(imagePickerState = result) }
                when (result) {
                    is ImagePickerResult.Success -> {
                        mutableState.update { it.copy(selectedImageUrl = result.imageUrl) }
                    }

                    is ImagePickerResult.Error -> {
                        _uiIntent.emit(
                            CreatePersonalHabitUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = result.message,
                                    state = BloomSnackbarState.Error,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }

                    else -> Unit
                }
            }
            .launchIn(screenModelScope)
    }

    fun handleUiEvent(uiEvent: CreatePersonalHabitUiEvent) {
        when (uiEvent) {
            CreatePersonalHabitUiEvent.NavigateBack -> {
                screenModelScope.launch {
                    _uiIntent.emit(CreatePersonalHabitUiIntent.NavigateBack)
                }
            }

            is CreatePersonalHabitUiEvent.UpdateDescription -> {
                val input = uiEvent.input
                mutableState.update {
                    it.copy(
                        description = input,
                        isDescriptionInputError = input.length > HABIT_DESCRIPTION_MAX_LENGTH
                    )
                }
            }

            is CreatePersonalHabitUiEvent.UpdateTimeOfDay -> {
                mutableState.update { it.copy(timeOfDay = uiEvent.timeOfDay) }
            }

            is CreatePersonalHabitUiEvent.UpdateTitle -> {
                val input = uiEvent.input
                mutableState.update {
                    it.copy(
                        title = input,
                        isTitleInputError = input.length > HABIT_TITLE_MAX_LENGTH
                    )
                }
            }

            CreatePersonalHabitUiEvent.CreateHabit -> {
                mutableState.update { it.copy(showCreateHabitDialog = true) }
            }

            CreatePersonalHabitUiEvent.HideCreateHabitDialog -> {
                mutableState.update { it.copy(showCreateHabitDialog = false) }
            }

            CreatePersonalHabitUiEvent.SubmitHabitCreation -> {
                saveUserHabit()
            }

            CreatePersonalHabitUiEvent.PickImage -> {
                screenModelScope.launch {
                    imagePicker.pickImage()
                }
            }
        }
    }

    private fun saveUserHabit() = screenModelScope.launch {
        mutableState.update { it.copy(showCreateHabitDialog = false, isLoading = true) }

        val uiState = mutableState.value
        val userId = profileRepository.getUserInfo().getOrNull()?.id ?: return@launch
        habitRepository.createPersonalHabit(
            userId = userId,
            timeOfDay = uiState.timeOfDay,
            title = uiState.title,
            description = uiState.description,
            icon = uiState.selectedImageUrl ?: ""
        ).onSuccess {
            mutableState.update { it.copy(isLoading = false) }
            _uiIntent.emit(CreatePersonalHabitUiIntent.OpenSuccessScreen)
        }.onFailure {
            mutableState.update { it.copy(isLoading = false) }
            _uiIntent.emit(
                CreatePersonalHabitUiIntent.ShowSnackbar(
                    visuals = BloomSnackbarVisuals(
                        message = getString(Res.string.save_habit_error),
                        state = BloomSnackbarState.Error,
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                )
            )
        }
    }
}