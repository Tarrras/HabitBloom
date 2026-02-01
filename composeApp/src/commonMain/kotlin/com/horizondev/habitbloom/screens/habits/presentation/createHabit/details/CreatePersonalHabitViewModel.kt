import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.details.CreatePersonalHabitUiEvent
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.details.CreatePersonalHabitUiIntent
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.details.CreatePersonalHabitUiState
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.save_habit_error
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the Create Personal Habit screen.
 */
class CreatePersonalHabitViewModel(
    private val habitRepository: HabitsRepository,
    private val profileRepository: ProfileRepository,
    private val categoryId: String
) : BloomViewModel<CreatePersonalHabitUiState, CreatePersonalHabitUiIntent>(
    initialState = CreatePersonalHabitUiState()
) {
    init {
        // Load available icons from Firebase
        loadHabitIcons()
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

            is CreatePersonalHabitUiEvent.SelectIcon -> {
                updateState { it.copy(selectedImageUrl = uiEvent.iconUrl) }
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
                title = uiState.title,
                description = uiState.description,
                categoryId = categoryId,
                icon = uiState.selectedImageUrl
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

    /**
     * Load available habit icons from Firebase.
     */
    private fun loadHabitIcons() {
        launch {
            updateState { it.copy(isLoadingIcons = true) }

            habitRepository.getHabitIcons()
                .onSuccess { icons ->
                    updateState {
                        it.copy(
                            availableIcons = icons,
                            isLoadingIcons = false,
                            selectedImageUrl = icons.firstOrNull() ?: DEFAULT_PHOTO_URL
                        )
                    }
                }
                .onFailure { error ->
                    Napier.e("Failed to load habit icons", error)
                    updateState { it.copy(isLoadingIcons = false) }
                    emitUiIntent(
                        CreatePersonalHabitUiIntent.ShowSnackbar(
                            visuals = BloomSnackbarVisuals(
                                message = "Failed to load icons",
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