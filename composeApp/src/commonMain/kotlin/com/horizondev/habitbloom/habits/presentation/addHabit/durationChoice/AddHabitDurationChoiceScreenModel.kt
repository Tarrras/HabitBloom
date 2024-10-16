package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import androidx.compose.material3.SnackbarDuration
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import com.horizondev.habitbloom.utils.formatToMmDdYy
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import com.horizondev.habitbloom.utils.mmDdYyToDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.the_habit_cannot_start_on_past_days
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.getString

class AddHabitDurationChoiceScreenModel : StateScreenModel<AddHabitDurationChoiceUiState>(AddHabitDurationChoiceUiState()) {

    val uiIntent = MutableSharedFlow<AddHabitDurationChoiceUiIntent>()

    val startDateFlow = combine(
        mutableState.map { it.activeDays },
        mutableState.map { it.weekStartOption }
    ) { activeDays, startOption ->
        val firstDay = getFirstDateAfterStartDateOrNextWeek(
            daysList = activeDays, startOption = startOption
        )

        mutableState.update { it.copy(startDate = firstDay?.formatToMmDdYy()) }
    }.launchIn(screenModelScope)

    fun handleUiEvent(uiEvent: AddHabitDurationChoiceUiEvent) = screenModelScope.launch {
        when (uiEvent) {
            is AddHabitDurationChoiceUiEvent.SelectGroupOfDays -> {
                val newList = when (uiEvent.group) {
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
                mutableState.update { it.copy(activeDays = newList) }
            }

            is AddHabitDurationChoiceUiEvent.UpdateDayState -> {
                val uiState = mutableState.value
                val dayToChange = uiEvent.dayOfWeek

                val currentActiveDays = uiState.activeDays

                val newList = currentActiveDays.toMutableList().apply {
                    if (contains(dayToChange)) {
                        remove(dayToChange)
                    } else add(dayToChange)
                }
                mutableState.update { it.copy(activeDays = newList) }
            }

            is AddHabitDurationChoiceUiEvent.DurationChanged -> {
                mutableState.update { it.copy(duration = uiEvent.duration) }
            }

            AddHabitDurationChoiceUiEvent.Cancel -> {
                uiIntent.emit(AddHabitDurationChoiceUiIntent.NavigateBack)
            }

            AddHabitDurationChoiceUiEvent.OnNext -> {
                val uiState = mutableState.value

                if (uiState.startDate == null) {
                    uiIntent.emit(
                        AddHabitDurationChoiceUiIntent.ShowSnackBar(
                            visuals = BloomSnackbarVisuals(
                                message = getString(Res.string.the_habit_cannot_start_on_past_days),
                                actionLabel = null,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        )
                    )
                } else uiIntent.emit(
                    AddHabitDurationChoiceUiIntent.NavigateToSummary(
                        selectedDays = uiState.activeDays,
                        selectedDuration = uiState.duration,
                        startDate = uiState.startDate.mmDdYyToDate(),
                        habitWeekStartOption = uiState.weekStartOption
                    )
                )
            }

            is AddHabitDurationChoiceUiEvent.SelectWeekStartOption -> {
                mutableState.update { it.copy(weekStartOption = uiEvent.option) }
            }
        }
    }
}