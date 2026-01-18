package com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.EnableNotificationsForReminderUseCase

import io.github.aakira.napier.Napier

/**
 * ViewModel for the summary step in the Add Habit flow.
 */
class AddHabitSummaryViewModel(
    private val repository: HabitsRepository,
    private val addHabitStateUseCase: AddHabitStateUseCase,
    private val enableNotificationsUseCase: EnableNotificationsForReminderUseCase
) : BloomViewModel<AddHabitSummaryUiState, AddHabitSummaryUiIntent>(
    initialState = AddHabitSummaryUiState()
) {

    init {
        // Observe the UseCase state and update our UI state
        launch {
            addHabitStateUseCase.draft.collect { draft ->
                updateState {
                    it.copy(
                        timeOfDay = draft.timeOfDay ?: TimeOfDay.Morning,
                        habitInfo = draft.habitInfo,
                        habitCategory = draft.habitCategory,
                        days = draft.selectedDays,
                        startDate = draft.startDate,
                        endDate = draft.endDate,
                        durationInDays = draft.durationInDays,
                        reminderEnabled = draft.reminderEnabled,
                        reminderTime = draft.reminderTime
                    )
                }
            }
        }
    }

    /**
     * Single entry point for handling UI events.
     */
    fun handleUiEvent(event: AddHabitSummaryUiEvent) {
        when (event) {
            AddHabitSummaryUiEvent.Confirm -> {
                submitHabit()
            }

            AddHabitSummaryUiEvent.BackPressed -> {
                emitUiIntent(AddHabitSummaryUiIntent.NavigateBack)
            }
        }
    }

    /**
     * Submits the habit to the repository.
     */
    private fun submitHabit() {
        val creationData = addHabitStateUseCase.getHabitCreationData() ?: return
        val currentState = state.value

        launch {
            updateState { it.copy(isLoading = true) }

            runCatching {
                val result = repository.addUserHabit(
                    habitInfo = creationData.habitInfo,
                    startDate = creationData.startDate,
                    endDate = creationData.endDate,
                    selectedDays = creationData.selectedDays,
                    reminderEnabled = currentState.reminderEnabled,
                    reminderTime = currentState.reminderTime
                )

                result.fold(
                    onSuccess = { habitId ->
                        updateState { it.copy(isLoading = false) }

                        // Schedule reminder if enabled
                        if (currentState.reminderEnabled && currentState.reminderTime != null) {
                            // Enable notifications if this is the first reminder
                            enableNotificationsUseCase.execute()

                            repository.scheduleReminderForHabit(
                                habitId = habitId,
                                reminderTime = currentState.reminderTime
                            )
                        }

                        // Reset the draft after successful creation
                        addHabitStateUseCase.resetDraft()

                        // Navigate to the success screen
                        emitUiIntent(AddHabitSummaryUiIntent.NavigateToSuccess)
                    },
                    onFailure = { error ->
                        Napier.e("Failed to add habit", error)
                        updateState { it.copy(isLoading = false) }

                        emitUiIntent(
                            AddHabitSummaryUiIntent.ShowSnackBar(
                                BloomSnackbarVisuals(
                                    message = "Failed to add habit: ${error.message}",
                                    state = BloomSnackbarState.Error,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }
                )
            }.onFailure {
                Napier.e("Error adding habit", it)
                updateState { it.copy(isLoading = false) }

                emitUiIntent(
                    AddHabitSummaryUiIntent.ShowSnackBar(
                        BloomSnackbarVisuals(
                            message = "Failed to add habit: ${it.message}",
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