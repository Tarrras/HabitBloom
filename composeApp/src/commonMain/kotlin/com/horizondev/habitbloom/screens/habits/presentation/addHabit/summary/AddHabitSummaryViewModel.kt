package com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.AddHabitFlowState
import io.github.aakira.napier.Napier

/**
 * ViewModel for the summary step in the Add Habit flow.
 */
class AddHabitSummaryViewModel(
    private val repository: HabitsRepository,
    private val addHabitState: AddHabitFlowState
) : BloomViewModel<AddHabitSummaryUiState, AddHabitSummaryUiIntent>(
    initialState = AddHabitSummaryUiState(
        timeOfDay = addHabitState.timeOfDay ?: TimeOfDay.Morning,
        habitInfo = addHabitState.habitInfo ?: throw IllegalStateException("Habit info is null"),
        days = addHabitState.selectedDays,
        duration = addHabitState.durationInDays,
        startDate = addHabitState.startDate,
        weekStartOption = addHabitState.weekStartOption,
        reminderEnabled = addHabitState.reminderEnabled,
        reminderTime = addHabitState.reminderTime
    )
) {

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
        val info = addHabitState.habitInfo ?: return
        val currentState = state.value

        launch {
            updateState { it.copy(isLoading = true) }

            runCatching {
                val result = repository.addUserHabit(
                    habitInfo = info,
                    durationInDays = addHabitState.durationInDays,
                    startDate = addHabitState.startDate,
                    reminderEnabled = currentState.reminderEnabled,
                    reminderTime = currentState.reminderTime
                )

                result.fold(
                    onSuccess = { habitId ->
                        updateState { it.copy(isLoading = false) }

                        // Schedule reminder if enabled
                        if (currentState.reminderEnabled && currentState.reminderTime != null) {
                            repository.scheduleReminder(
                                habitId = habitId,
                                habitName = info.name,
                                description = info.description,
                                time = currentState.reminderTime,
                                activeDays = currentState.days
                            )
                        }

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