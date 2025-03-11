package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.models.GroupOfDays
import com.horizondev.habitbloom.utils.formatToMmDdYy
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import com.horizondev.habitbloom.utils.mmDdYyToDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.the_habit_cannot_start_on_past_days
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

/**
 * ViewModel for the duration choice step in the Add Habit flow.
 */
class AddHabitDurationViewModel : BloomViewModel<AddHabitDurationUiState, AddHabitDurationUiIntent>(
    initialState = AddHabitDurationUiState(
        activeDays = DayOfWeek.entries,
    )
) {

    init {
        // Setup derived state for start date
        combine(
            state.map { it.activeDays },
            state.map { it.weekStartOption }
        ) { activeDays, startOption ->
            val firstDay = getFirstDateAfterStartDateOrNextWeek(
                daysList = activeDays,
                startOption = startOption
            )

            if (firstDay != null) {
                updateState { it.copy(startDate = firstDay.formatToMmDdYy()) }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Single entry point for all UI events.
     */
    fun handleUiEvent(event: AddHabitDurationUiEvent) {
        when (event) {
            is AddHabitDurationUiEvent.SelectGroupOfDays -> {
                val newList = when (event.group) {
                    GroupOfDays.EVERY_DAY -> DayOfWeek.entries
                    GroupOfDays.WEEKENDS -> listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                    GroupOfDays.WORK_DAYS -> listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY
                    )
                }
                updateState { it.copy(activeDays = newList) }
            }

            is AddHabitDurationUiEvent.UpdateDayState -> {
                val currentState = state.value
                val dayToChange = event.dayOfWeek

                val currentActiveDays = currentState.activeDays

                val newList = currentActiveDays.toMutableList().apply {
                    if (contains(dayToChange)) {
                        remove(dayToChange)
                    } else add(dayToChange)
                }
                updateState { it.copy(activeDays = newList) }
            }

            is AddHabitDurationUiEvent.DurationChanged -> {
                updateState { it.copy(durationInDays = event.duration) }
            }

            AddHabitDurationUiEvent.Cancel -> {
                emitUiIntent(AddHabitDurationUiIntent.NavigateBack)
            }

            AddHabitDurationUiEvent.OnNext -> viewModelScope.launch {
                val currentState = state.value

                if (currentState.startDate == null) {
                    emitUiIntent(
                        AddHabitDurationUiIntent.ShowValidationError(
                            BloomSnackbarVisuals(
                                message = getString(Res.string.the_habit_cannot_start_on_past_days),
                                state = BloomSnackbarState.Error,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        )
                    )
                } else {
                    val calculatedStartedDate = currentState.startDate.mmDdYyToDate()
                    emitUiIntent(
                        AddHabitDurationUiIntent.NavigateNext(
                            selectedDays = currentState.activeDays,
                            durationInDays = currentState.durationInDays,
                            weekStartOption = currentState.weekStartOption,
                            startDate = calculatedStartedDate
                        )
                    )
                }
            }

            is AddHabitDurationUiEvent.SelectWeekStartOption -> {
                updateState { it.copy(weekStartOption = event.option) }
            }
        }
    }

    /**
     * Helper method to get string resources.
     */
    private suspend fun getString(resource: org.jetbrains.compose.resources.StringResource): String {
        return org.jetbrains.compose.resources.getString(resource)
    }
}