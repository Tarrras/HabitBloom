package com.horizondev.habitbloom.habits.presentation.addHabit

import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
                        durationInDays = event.durationInDays,
                        startDate = event.startDate,
                        selectedDays = event.selectedDays,
                        weekStartOption = event.weekStartOption
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
        val durationInDays: Int,
        val startDate: LocalDate,
        val selectedDays: List<DayOfWeek>,
        val weekStartOption: HabitWeekStartOption
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
    val durationInDays: Int = 21,
    val startDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val isSubmitting: Boolean = false,
    val selectedDays: List<DayOfWeek> = DayOfWeek.entries,
    val weekStartOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK
) 