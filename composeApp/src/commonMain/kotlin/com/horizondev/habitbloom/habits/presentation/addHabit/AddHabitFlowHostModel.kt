package com.horizondev.habitbloom.habits.presentation.addHabit

import cafe.adriel.voyager.core.model.ScreenModel
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.NewUserHabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DayOfWeek

class AddHabitFlowHostModel : ScreenModel {
    private val flowPages = AddHabitFlowScreenStep.entries

    private val _flowPageState = MutableStateFlow(0)
    val flowPageState = _flowPageState.asStateFlow()

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
        duration: Int
    ) {
        _flowHabitInfoState.update { it.copy(days = days, duration = duration) }
    }
}