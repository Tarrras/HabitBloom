package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.delete_custom_habit_success
import habitbloom.composeapp.generated.resources.habit_already_added
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString

class AddHabitChoiceViewModel(
    private val repository: HabitsRepository,
    private val addHabitStateUseCase: AddHabitStateUseCase
) : BloomViewModel<AddHabitChoiceUiState, AddHabitChoiceUiIntent>(
    initialState = AddHabitChoiceUiState()
) {

    private val currentCategory: HabitCategoryData?
        get() = addHabitStateUseCase.getCurrentDraft().habitCategory

    private var isInitialLoad = true

    init {
        requestHabits(forceRefresh = true)

        launch {
            state
                .map { it.searchInput }
                .distinctUntilChanged()
                .drop(1)
                .debounce(300) // Reduced from 500ms for better UX
                .collectLatest {
                    loadHabits(forceRefresh = false)
                }
        }
    }

    fun handleScreenResumed() {
        if (!isInitialLoad) {
            // Only refresh if this is not the initial load
            handleUiEvent(AddHabitChoiceUiEvent.RefreshPage)
        }
    }

    fun handleUiEvent(event: AddHabitChoiceUiEvent) {
        when (event) {
            is AddHabitChoiceUiEvent.UpdateSearchInput -> {
                updateState { it.copy(searchInput = event.input) }
            }

            is AddHabitChoiceUiEvent.SelectHabit -> {
                // Update the UseCase with the selected habit
                addHabitStateUseCase.updateHabitInfo(event.habit)
                checkHabitAndProceed(event.habit)
            }

            AddHabitChoiceUiEvent.CreateCustomHabit -> {
                emitUiIntent(AddHabitChoiceUiIntent.NavigateToCreateCustomHabit(currentCategory?.id))
            }

            is AddHabitChoiceUiEvent.DeleteHabit -> {
                updateState {
                    it.copy(
                        showDeleteDialog = true,
                        habitToDelete = event.habit
                    )
                }
            }

            AddHabitChoiceUiEvent.CancelDeleteHabit -> {
                updateState {
                    it.copy(
                        showDeleteDialog = false,
                        habitToDelete = null
                    )
                }
            }

            AddHabitChoiceUiEvent.ConfirmDeleteHabit -> {
                confirmDeleteHabit()
            }

            AddHabitChoiceUiEvent.NavigateBack -> {
                emitUiIntent(AddHabitChoiceUiIntent.NavigateBack)
            }

            AddHabitChoiceUiEvent.RefreshPage -> {
                requestHabits(forceRefresh = true)
            }
        }
    }

    private fun checkHabitAndProceed(habit: HabitInfo) {
        launch {
            runCatching {
                repository.isHabitAlreadyAdded(habit.id)
            }.onSuccess { isAlreadyAdded ->
                if (isAlreadyAdded) {
                    showErrorSnackbar(getString(Res.string.habit_already_added))
                } else {
                    emitUiIntent(AddHabitChoiceUiIntent.NavigateNext(habit))
                }
            }.onFailure {
                Napier.e("Error checking if habit exists", it)
                // Proceed anyway in case of error
                emitUiIntent(AddHabitChoiceUiIntent.NavigateNext(habit))
            }
        }
    }

    private fun requestHabits(forceRefresh: Boolean) {
        launch {
            loadHabits(forceRefresh = forceRefresh)
        }
    }

    private suspend fun loadHabits(forceRefresh: Boolean) {
        val selectedCategory = currentCategory
        val query = state.value.searchInput.trim()

        updateState {
            it.copy(isLoading = true, currentCategory = selectedCategory)
        }

        runCatching {
            repository.getHabits(
                searchInput = query,
                categoryId = selectedCategory?.id,
                forceRefresh = forceRefresh
            )
        }.onSuccess { result ->
            result.fold(
                onSuccess = { habits ->
                    isInitialLoad = false
                    updateState {
                        it.copy(
                            habits = habits,
                            isLoading = false,
                            currentCategory = selectedCategory
                        )
                    }
                },
                onFailure = {
                    Napier.e("Failed to load habits", it)
                    updateState { state -> state.copy(isLoading = false) }
                }
            )
        }.onFailure {
            Napier.e("Failed to load habits", it)
            updateState { state -> state.copy(isLoading = false) }
        }
    }


    private fun showErrorSnackbar(message: String) {
        emitUiIntent(
            AddHabitChoiceUiIntent.ShowSnackbar(
                BloomSnackbarVisuals(
                    message = message,
                    state = BloomSnackbarState.Error,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            )
        )
    }


    private fun confirmDeleteHabit() {
        val habitToDelete = state.value.habitToDelete ?: return

        launch {
            updateState { it.copy(isLoading = true, showDeleteDialog = false) }

            runCatching {
                repository.deleteCustomHabit(habitToDelete.id)
            }.onSuccess { result ->
                result.fold(
                    onSuccess = {
                        loadHabits(forceRefresh = false)
                        emitUiIntent(
                            AddHabitChoiceUiIntent.ShowSnackbar(
                                BloomSnackbarVisuals(
                                    message = getString(Res.string.delete_custom_habit_success),
                                    state = BloomSnackbarState.Success,
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                            )
                        )
                    },
                    onFailure = { error ->
                        updateState { state -> state.copy(isLoading = false) }
                        showErrorSnackbar("Failed to delete habit: ${error.message}")
                    }
                )
            }.onFailure { exception ->
                Napier.e("Error deleting habit", exception)
                updateState { state -> state.copy(isLoading = false) }
                showErrorSnackbar("Error deleting habit: ${exception.message}")
            }
        }
    }
}
