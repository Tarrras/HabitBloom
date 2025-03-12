package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.delete_custom_habit_success
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
    private val timeOfDay: TimeOfDay?
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
                emitUiIntent(AddHabitChoiceUiIntent.NavigateNext(event.habit))
            }

            AddHabitChoiceUiEvent.CreateCustomHabit -> {
                emitUiIntent(
                    AddHabitChoiceUiIntent.NavigateToCreateCustomHabit(
                        timeOfDay = timeOfDay ?: TimeOfDay.Morning
                    )
                )
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
     * Searches for habits based on the query.
     */
    private fun searchHabits(query: String) {
        launch {
            updateState { it.copy(isLoading = true) }

            runCatching {
                repository.getHabits(query, timeOfDay = timeOfDay ?: TimeOfDay.Morning)
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

