package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import androidx.compose.material3.SnackbarDuration
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.delete_custom_habit_success
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

class AddHabitChoiceScreenModel(
    private val repository: HabitsRepository,
    private val timeOfDay: TimeOfDay?,
) : StateScreenModel<AddHabitChoiceUiState>(AddHabitChoiceUiState()) {

    val uiIntent = MutableSharedFlow<AddHabitChoiceUiIntent>()

    private val searchInput = state
        .onStart {
            mutableState.update { it.copy(isLoading = true) }
            repository.initData()
            mutableState.update { it.copy(isLoading = false) }
        }
        .map { it.searchInput }
        .debounce(500)
        .onEach {
            mutableState.update { it.copy(isLoading = true) }
            val habits = repository.getHabits(it, timeOfDay = timeOfDay ?: TimeOfDay.Morning)
            mutableState.update { state -> state.copy(habits = habits, isLoading = false) }
        }
        .launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: AddHabitChoiceUiEvent) {
        when (uiEvent) {
            is AddHabitChoiceUiEvent.PerformSearch -> mutableState.update {
                it.copy(searchInput = uiEvent.input)
            }

            is AddHabitChoiceUiEvent.SubmitHabit -> {
                screenModelScope.launch {
                    uiIntent.emit(AddHabitChoiceUiIntent.NavigateNext(uiEvent.info))
                }
            }

            AddHabitChoiceUiEvent.CreatePersonalHabit -> {
                timeOfDay?.let {
                    screenModelScope.launch {
                        uiIntent.emit(AddHabitChoiceUiIntent.NavigateToHabitCreation(timeOfDay))
                    }
                }
            }

            is AddHabitChoiceUiEvent.DeleteHabit -> {
                mutableState.update {
                    it.copy(
                        showDeleteDialog = true,
                        habitToDelete = uiEvent.info
                    )
                }
            }

            AddHabitChoiceUiEvent.CancelDeleteHabit -> {
                mutableState.update {
                    it.copy(
                        showDeleteDialog = false,
                        habitToDelete = null
                    )
                }
            }

            AddHabitChoiceUiEvent.ConfirmDeleteHabit -> {
                val habitToDelete = mutableState.value.habitToDelete ?: return

                screenModelScope.launch {
                    mutableState.update { it.copy(isLoading = true, showDeleteDialog = false) }

                    runCatching {
                        // Extract habit ID
                        val habitId = habitToDelete.id

                        // Call repository to delete the custom habit
                        repository.deleteCustomHabit(habitId)
                    }.fold(
                        onSuccess = { result ->
                            result.fold(
                                onSuccess = {
                                    // Refresh habits list
                                    val currentSearch = mutableState.value.searchInput
                                    val refreshedHabits = repository.getHabits(
                                        currentSearch,
                                        timeOfDay = timeOfDay ?: TimeOfDay.Morning
                                    )

                                    mutableState.update {
                                        it.copy(
                                            habits = refreshedHabits,
                                            isLoading = false,
                                            habitToDelete = null
                                        )
                                    }

                                    uiIntent.emit(
                                        AddHabitChoiceUiIntent.ShowSnackbar(
                                            visuals = BloomSnackbarVisuals(
                                                message = runBlocking { getString(Res.string.delete_custom_habit_success) },
                                                state = BloomSnackbarState.Success,
                                                duration = SnackbarDuration.Short,
                                                withDismissAction = true
                                            )
                                        )
                                    )
                                },
                                onFailure = { error ->
                                    handleError(error, "Failed to delete custom habit")
                                }
                            )
                        },
                        onFailure = { error ->
                            handleError(error, "Error deleting custom habit")
                        }
                    )
                }
            }
        }
    }

    private suspend fun handleError(error: Throwable, errorPrefix: String) {
        Napier.e(errorPrefix, error)
        mutableState.update { it.copy(isLoading = false) }
        uiIntent.emit(
            AddHabitChoiceUiIntent.ShowSnackbar(
                visuals = BloomSnackbarVisuals(
                    message = "$errorPrefix: ${error.message}",
                    state = BloomSnackbarState.Error,
                    duration = SnackbarDuration.Short,
                    withDismissAction = true
                )
            )
        )
    }
}