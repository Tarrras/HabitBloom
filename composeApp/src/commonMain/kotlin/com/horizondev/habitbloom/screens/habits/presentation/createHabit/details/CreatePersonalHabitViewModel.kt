
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.platform.ImagePicker
import com.horizondev.habitbloom.platform.ImagePickerResult
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.details.CreatePersonalHabitUiEvent
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.details.CreatePersonalHabitUiIntent
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.details.CreatePersonalHabitUiState
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.save_habit_error
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the Create Personal Habit screen.
 */
class CreatePersonalHabitViewModel(
    private val habitRepository: HabitsRepository,
    private val profileRepository: ProfileRepository,
    private val imagePicker: ImagePicker,
    timeOfDay: TimeOfDay?
) : BloomViewModel<CreatePersonalHabitUiState, CreatePersonalHabitUiIntent>(
    initialState = CreatePersonalHabitUiState(
        timeOfDay = timeOfDay ?: TimeOfDay.Morning
    )
) {
    init {
        // Observe image picker results
        imagePicker.imagePickerResult
            .onEach { result ->
                updateState {
                    it.copy(imagePickerState = result)
                }

                when (result) {
                    is ImagePickerResult.Success -> {
                        Napier.d("Image picked: ${result.imageUrl}")
                        updateState {
                            it.copy(selectedImageUrl = result.imageUrl)
                        }
                    }
                    is ImagePickerResult.Error -> {
                        emitUiIntent(
                            CreatePersonalHabitUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = "Failed to pick image: ${result.message}",
                                    state = BloomSnackbarState.Error,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }

                    else -> { /* Do nothing for other states */
                    }
                }
            }.launchIn(viewModelScope)
    }

    /**
     * Handle UI events from the view.
     */
    fun handleUiEvent(uiEvent: CreatePersonalHabitUiEvent) {
        when (uiEvent) {
            CreatePersonalHabitUiEvent.NavigateBack -> {
                emitUiIntent(CreatePersonalHabitUiIntent.NavigateBack)
            }

            is CreatePersonalHabitUiEvent.UpdateDescription -> {
                val input = uiEvent.input
                updateState {
                    it.copy(
                        description = input,
                        isDescriptionInputError = input.length > HABIT_DESCRIPTION_MAX_LENGTH
                    )
                }
            }

            is CreatePersonalHabitUiEvent.UpdateTimeOfDay -> {
                updateState { it.copy(timeOfDay = uiEvent.timeOfDay) }
            }

            is CreatePersonalHabitUiEvent.UpdateTitle -> {
                val input = uiEvent.input
                updateState {
                    it.copy(
                        title = input,
                        isTitleInputError = input.length > HABIT_TITLE_MAX_LENGTH
                    )
                }
            }

            CreatePersonalHabitUiEvent.CreateHabit -> {
                updateState { it.copy(showCreateHabitDialog = true) }
            }

            CreatePersonalHabitUiEvent.HideCreateHabitDialog -> {
                updateState { it.copy(showCreateHabitDialog = false) }
            }

            CreatePersonalHabitUiEvent.SubmitHabitCreation -> {
                saveUserHabit()
            }

            CreatePersonalHabitUiEvent.PickImage -> {
                launch {
                    imagePicker.pickImage()
                }
            }
        }
    }

    /**
     * Save the user habit to the repository.
     */
    private fun saveUserHabit() {
        launch {
            updateState { it.copy(showCreateHabitDialog = false, isLoading = true) }

            val uiState = state.value
            val userId = profileRepository.getUserInfo().getOrNull()?.id ?: return@launch

            Napier.d("Creating habit with image: ${uiState.selectedImageUrl ?: "No image"}")

            habitRepository.createPersonalHabit(
                userId = userId,
                timeOfDay = uiState.timeOfDay,
                title = uiState.title,
                description = uiState.description,
                icon = uiState.selectedImageUrl ?: ""
            ).onSuccess {
                updateState { it.copy(isLoading = false) }
                emitUiIntent(CreatePersonalHabitUiIntent.OpenSuccessScreen)
            }.onFailure {
                Napier.e("Failed to save habit", it)
                updateState { it.copy(isLoading = false) }
                emitUiIntent(
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
}