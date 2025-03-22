package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.habits.domain.models.GroupOfDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class AddHabitDurationUiState(
    val activeDays: List<DayOfWeek> = emptyList(),
    val startDate: LocalDate? = null,
    val formattedStartDate: String? = null,
    val weekStartOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK,
    val durationInDays: Int = 1,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime = LocalTime(8, 0), // Default reminder time set to 8:00 AM
) {
    val displayedStartDate: String? = formattedStartDate
    val nextButtonEnabled: Boolean = activeDays.isEmpty().not()
}

sealed interface AddHabitDurationUiIntent {
    data object NavigateBack : AddHabitDurationUiIntent

    data class ShowValidationError(val visuals: BloomSnackbarVisuals) : AddHabitDurationUiIntent

    data class NavigateNext(
        val selectedDays: List<DayOfWeek>,
        val durationInDays: Int,
        val weekStartOption: HabitWeekStartOption,
        val startDate: LocalDate,
        val reminderEnabled: Boolean,
        val reminderTime: LocalTime
    ) : AddHabitDurationUiIntent
}

sealed interface AddHabitDurationUiEvent {
    data class UpdateDayState(
        val dayOfWeek: DayOfWeek,
        val isActive: Boolean
    ) : AddHabitDurationUiEvent

    data class SelectGroupOfDays(val group: GroupOfDays) : AddHabitDurationUiEvent
    data class SelectWeekStartOption(val option: HabitWeekStartOption) : AddHabitDurationUiEvent
    data class DurationChanged(val duration: Int) : AddHabitDurationUiEvent
    data class ReminderEnabledChanged(val enabled: Boolean) : AddHabitDurationUiEvent
    data class ReminderTimeChanged(val time: LocalTime) : AddHabitDurationUiEvent

    data object OnNext : AddHabitDurationUiEvent
    data object Cancel : AddHabitDurationUiEvent
}