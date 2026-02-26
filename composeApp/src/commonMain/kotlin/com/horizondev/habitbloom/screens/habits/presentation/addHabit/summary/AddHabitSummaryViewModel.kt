package com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.ActiveHabitAlreadyExistsException
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.EnableNotificationsForReminderUseCase
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.habit_already_added
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.getString

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


    private fun submitHabit() {
        val creationData = addHabitStateUseCase.getHabitCreationData() ?: return
        val currentState = state.value

        launch {
            updateState { it.copy(isLoading = true) }

            try {
                val result = repository.addUserHabit(
                    habitInfo = creationData.habitInfo,
                    startDate = creationData.startDate,
                    endDate = creationData.endDate,
                    selectedDays = creationData.selectedDays,
                    reminderEnabled = currentState.reminderEnabled,
                    reminderTime = currentState.reminderTime,
                    timeOfDay = creationData.timeOfDay
                )

                val habitId = result.getOrElse { error ->
                    Napier.e("Failed to add habit", error)
                    updateState { it.copy(isLoading = false) }

                    emitUiIntent(
                        AddHabitSummaryUiIntent.ShowSnackBar(
                            BloomSnackbarVisuals(
                                message = resolveErrorMessage(error),
                                state = BloomSnackbarState.Error,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        )
                    )
                    return@launch
                }

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

                // Cache the habit name for the success screen
                addHabitStateUseCase.setLastAddedHabitName(creationData.habitInfo.name)

                // Reset the draft after successful creation
                addHabitStateUseCase.resetDraft()

                // Navigate to the success screen
                emitUiIntent(AddHabitSummaryUiIntent.NavigateToSuccess)
            } catch (error: Exception) {
                Napier.e("Error adding habit", error)
                updateState { it.copy(isLoading = false) }

                emitUiIntent(
                    AddHabitSummaryUiIntent.ShowSnackBar(
                        BloomSnackbarVisuals(
                            message = resolveErrorMessage(error),
                            state = BloomSnackbarState.Error,
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        )
                    )
                )
            }
        }
    }

    private suspend fun resolveErrorMessage(error: Throwable): String {
        return when (error) {
            is ActiveHabitAlreadyExistsException -> getString(Res.string.habit_already_added)
            else -> "Failed to add habit: ${error.message}"
        }
    }
}
