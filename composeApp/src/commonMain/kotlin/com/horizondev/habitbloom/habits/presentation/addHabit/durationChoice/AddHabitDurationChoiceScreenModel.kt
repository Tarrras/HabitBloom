package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DayOfWeek

class AddHabitDurationChoiceScreenModel(

) : StateScreenModel<AddHabitDurationChoiceUiState>(AddHabitDurationChoiceUiState()) {

    fun handleUiEvent(uiEvent: AddHabitDurationChoiceUiEvent) {
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
        }
    }
}