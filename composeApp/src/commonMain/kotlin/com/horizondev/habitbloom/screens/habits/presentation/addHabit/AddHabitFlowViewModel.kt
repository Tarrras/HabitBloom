package com.horizondev.habitbloom.screens.habits.presentation.addHabit

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.calculateEndOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * ViewModel for the Add Habit flow that maintains state across the different steps.
 */
class AddHabitFlowViewModel : BloomViewModel<AddHabitFlowState, AddHabitFlowUiIntent>(
    initialState = AddHabitFlowState()
) {

    /**
     * Single entry point for handling UI events.
     */
    fun handleUiEvent(event: AddHabitFlowUiEvent) {
        when (event) {
            is AddHabitFlowUiEvent.UpdateTimeOfDay -> {
                updateState { it.copy(timeOfDay = event.timeOfDay) }
            }

            is AddHabitFlowUiEvent.UpdateHabit -> {
                updateState { it.copy(habitInfo = event.habitInfo) }
            }

            is AddHabitFlowUiEvent.UpdateDuration -> {
                updateState {
                    it.copy(
                        startDate = event.startDate,
                        endDate = event.endDate,
                        selectedDays = event.selectedDays,
                        reminderEnabled = event.reminderEnabled,
                        reminderTime = event.reminderTime
                    )
                }
            }

            AddHabitFlowUiEvent.CancelFlow -> {
                emitUiIntent(AddHabitFlowUiIntent.ExitFlow)
            }

            is AddHabitFlowUiEvent.ShowSnackbar -> {
                emitUiIntent(AddHabitFlowUiIntent.ShowShackbar(visuals = event.visuals))
            }
        }
    }
}

/**
 * UI Events that can be triggered from the UI.
 */
sealed interface AddHabitFlowUiEvent {
    data class UpdateTimeOfDay(val timeOfDay: TimeOfDay) : AddHabitFlowUiEvent
    data class UpdateHabit(val habitInfo: HabitInfo) : AddHabitFlowUiEvent
    data class UpdateDuration(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val selectedDays: List<DayOfWeek>,
        val reminderEnabled: Boolean,
        val reminderTime: LocalTime
    ) : AddHabitFlowUiEvent

    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : AddHabitFlowUiEvent

    data object CancelFlow : AddHabitFlowUiEvent
}

/**
 * UI Intents emitted by the Add Habit Flow ViewModel.
 */
sealed interface AddHabitFlowUiIntent {
    data class ShowShackbar(val visuals: BloomSnackbarVisuals) : AddHabitFlowUiIntent
    data object ExitFlow : AddHabitFlowUiIntent
}

/**
 * Extended state with submission status.
 */
data class AddHabitFlowState(
    val timeOfDay: TimeOfDay? = null,
    val habitInfo: HabitInfo? = null,
    val startDate: LocalDate = getCurrentDate(),
    val endDate: LocalDate = getCurrentDate().calculateEndOfWeek(), // Default end date is the end of current week
    val isSubmitting: Boolean = false,
    val selectedDays: List<DayOfWeek> = DayOfWeek.entries,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime = LocalTime(8, 0) // Default reminder time set to 8:00 AM
) 