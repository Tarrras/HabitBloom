package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * UI state for the Add Habit Duration screen.
 * The focus is now on selecting a date range rather than specifying duration in days.
 */
data class AddHabitDurationUiState(
    val activeDays: List<DayOfWeek> = emptyList(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val durationInDays: Int = 1, // Display how many days the habit will take
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime = LocalTime(8, 0), // Default reminder time set to 8:00 AM
    val maxHabitDurationDays: Int = 90, // Maximum allowed habit duration in days
    val isDatePickerVisible: Boolean = false, // Control dialog visibility from ViewModel
    val timeOfDay: TimeOfDay = TimeOfDay.Morning,
) {
    val nextButtonEnabled: Boolean = activeDays.isNotEmpty() && startDate != null && endDate != null
}

sealed interface AddHabitDurationUiIntent {
    data object NavigateBack : AddHabitDurationUiIntent

    data class ShowValidationError(val visuals: BloomSnackbarVisuals) : AddHabitDurationUiIntent

    data class NavigateNext(
        val selectedDays: List<DayOfWeek>,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val durationInDays: Int,
        val reminderEnabled: Boolean,
        val reminderTime: LocalTime
    ) : AddHabitDurationUiIntent
}

/**
 * UI events for the Add Habit Duration screen.
 */
sealed interface AddHabitDurationUiEvent {
    data class UpdateDayState(
        val dayOfWeek: DayOfWeek,
        val isActive: Boolean
    ) : AddHabitDurationUiEvent

    data class SelectGroupOfDays(val group: List<DayOfWeek>) : AddHabitDurationUiEvent
    data class ReminderEnabledChanged(val enabled: Boolean) : AddHabitDurationUiEvent
    data class ReminderTimeChanged(val time: LocalTime) : AddHabitDurationUiEvent
    data class DateRangeChanged(val startDate: LocalDate, val endDate: LocalDate?) :
        AddHabitDurationUiEvent
    
    // Preset date range selection events
    data class SelectPresetDateRange(val daysAhead: Int) : AddHabitDurationUiEvent

    // Date picker visibility events
    data class SetDatePickerVisibility(val isVisible: Boolean) : AddHabitDurationUiEvent

    // Time of day selection
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay) : AddHabitDurationUiEvent

    data object OnNext : AddHabitDurationUiEvent
    data object Cancel : AddHabitDurationUiEvent
}
