package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.delete_custom_habit_success
import habitbloom.composeapp.generated.resources.habit_already_added
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the first step in Add Habit flow - selecting a habit.
 */
class AddHabitChoiceViewModel(
    private val repository: HabitsRepository,
    private val addHabitStateUseCase: AddHabitStateUseCase
) : BloomViewModel<AddHabitChoiceUiState, AddHabitChoiceUiIntent>(
    initialState = AddHabitChoiceUiState()
) {

    init {
        // Set up search debounce
        launch {
            state
                .map { it.searchInput }
                .distinctUntilChanged()
                .debounce(500)
                .collect { query ->
                    searchHabits(query)
                }
        }
    }

    /**
     * Single entry point for handling UI events.
     */
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
                emitUiIntent(AddHabitChoiceUiIntent.NavigateToCreateCustomHabit)
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
                searchHabits(state.value.searchInput)
            }
        }
    }

    /**
     * Checks if a habit is already added before proceeding with selection
     */
    private fun checkHabitAndProceed(habit: HabitInfo) {
        launch {
            runCatching {
                val isAlreadyAdded = repository.isHabitAlreadyAdded(habit.id)

                if (isAlreadyAdded) {
                    emitUiIntent(
                        AddHabitChoiceUiIntent.ShowSnackbar(
                            BloomSnackbarVisuals(
                                message = getString(Res.string.habit_already_added),
                                state = BloomSnackbarState.Error,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        )
                    )
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

    /**
     * Searches for habits based on the query.
     */
    private fun searchHabits(query: String) {
        launch {
            updateState { it.copy(isLoading = true) }

            runCatching {
                val categoryId = addHabitStateUseCase.getCurrentDraft().habitCategory?.id
                repository.getHabits(query, categoryId = categoryId)
            }.onSuccess { habitsResult ->
                habitsResult.onSuccess { habits ->
                    updateState { it.copy(habits = habits, isLoading = false) }
                }.onFailure {
                    Napier.e("Failed to search habits", it)
                    updateState { it.copy(isLoading = false) }
                }
            }.onFailure {
                Napier.e("Failed to search habits", it)
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Confirms the deletion of a custom habit.
     */
    private fun confirmDeleteHabit() {
        val habitToDelete = state.value.habitToDelete ?: return

        launch {
            updateState { it.copy(isLoading = true, showDeleteDialog = false) }

            try {
                val result = repository.deleteCustomHabit(habitToDelete.id)

                result.onSuccess {
                    // Refresh the list
                    searchHabits(query = state.value.searchInput)

                    // Show success message
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
                }

                result.onFailure { error ->
                    updateState { it.copy(isLoading = false) }
                    emitUiIntent(
                        AddHabitChoiceUiIntent.ShowSnackbar(
                            BloomSnackbarVisuals(
                                message = "Failed to delete habit: ${error.message}",
                                state = BloomSnackbarState.Error,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                Napier.e("Error deleting habit", e)
                updateState { it.copy(isLoading = false) }
                emitUiIntent(
                    AddHabitChoiceUiIntent.ShowSnackbar(
                        BloomSnackbarVisuals(
                            message = "Error deleting habit: ${e.message}",
                            state = BloomSnackbarState.Error,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    )
                )
            }
        }
    }
}

