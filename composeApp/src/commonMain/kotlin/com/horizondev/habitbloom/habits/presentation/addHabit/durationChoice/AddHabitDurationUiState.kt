package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class AddHabitDurationUiState(
    val activeDays: List<DayOfWeek> = emptyList(),
    val startDate: String? = null,
    val weekStartOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK,
    val durationInDays: Int = 1,
) {
    val displayedStartDate: String? = startDate
    val nextButtonEnabled: Boolean = activeDays.isEmpty().not()
}

sealed interface AddHabitDurationUiIntent {
    data object NavigateBack : AddHabitDurationUiIntent

    data class ShowValidationError(val visuals: BloomSnackbarVisuals) : AddHabitDurationUiIntent

    data class NavigateNext(
        val selectedDays: List<DayOfWeek>,
        val durationInDays: Int,
        val weekStartOption: HabitWeekStartOption,
        val startDate: LocalDate
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

    data object OnNext : AddHabitDurationUiEvent
    data object Cancel : AddHabitDurationUiEvent
}