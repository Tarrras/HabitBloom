package com.horizondev.habitbloom.habits.presentation.addHabit

import androidx.compose.material3.SnackbarVisuals
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.NewUserHabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class AddHabitFlowHostModel : ScreenModel {
    private val flowPages = AddHabitFlowScreenStep.entries

    private val _flowPageState = MutableStateFlow(0)
    val flowPageState = _flowPageState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<SnackbarVisuals>()
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    fun showSnackBar(visuals: BloomSnackbarVisuals) {
        screenModelScope.launch { _snackBarFlow.emit(visuals) }
    }

    fun updatedFlowPage(page: AddHabitFlowScreenStep) {
        val index = flowPages.indexOf(page)
        _flowPageState.update { index }
    }

    private val _flowHabitInfoState = MutableStateFlow(NewUserHabitInfo())
    val flowHabitInfoState = _flowHabitInfoState.asStateFlow()

    fun getNewHabitInfo() = _flowHabitInfoState.value

    fun updateTimeOfDaySelection(timeOfDay: TimeOfDay) {
        _flowHabitInfoState.update { it.copy(timeOfDay = timeOfDay) }
    }

    fun updateSelectedHabit(habitInfo: HabitInfo) {
        _flowHabitInfoState.update { it.copy(habitInfo = habitInfo) }
    }

    fun updateDaysAndDuration(
        days: List<DayOfWeek>,
        duration: Int,
        weekStartOption: HabitWeekStartOption,
        startDate: LocalDate
    ) {
        _flowHabitInfoState.update {
            it.copy(
                days = days,
                duration = duration,
                startDate = startDate,
                weekStartOption = weekStartOption
            )
        }
    }
}