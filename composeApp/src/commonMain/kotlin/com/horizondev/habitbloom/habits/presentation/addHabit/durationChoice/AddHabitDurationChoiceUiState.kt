package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import androidx.compose.material3.SnackbarVisuals
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class AddHabitDurationChoiceUiState(
    val activeDays: List<DayOfWeek> = emptyList(),
    val startDate: String? = null,
    val weekStartOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK,
    val duration: Int = 1,
) {
    val nextButtonEnabled: Boolean = activeDays.isEmpty().not()
}

sealed interface AddHabitDurationChoiceUiIntent {
    data object NavigateBack : AddHabitDurationChoiceUiIntent

    data class ShowSnackBar(val visuals: BloomSnackbarVisuals): AddHabitDurationChoiceUiIntent
    data class NavigateToSummary(
        val selectedDays: List<DayOfWeek>,
        val selectedDuration: Int,
        val habitWeekStartOption: HabitWeekStartOption,
        val startDate: LocalDate
    ) : AddHabitDurationChoiceUiIntent
}

sealed interface AddHabitDurationChoiceUiEvent {
    data class UpdateDayState(
        val dayOfWeek: DayOfWeek,
        val isActive: Boolean
    ) : AddHabitDurationChoiceUiEvent

    data class SelectGroupOfDays(val group: GroupOfDays) : AddHabitDurationChoiceUiEvent
    data class SelectWeekStartOption(val option: HabitWeekStartOption) : AddHabitDurationChoiceUiEvent
    data class DurationChanged(val duration: Int) : AddHabitDurationChoiceUiEvent

    data object OnNext : AddHabitDurationChoiceUiEvent
    data object Cancel : AddHabitDurationChoiceUiEvent
}