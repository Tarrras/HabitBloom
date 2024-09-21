package com.horizondev.habitbloom.habits.presentation.addHabit

import cafe.adriel.voyager.core.model.ScreenModel
import com.horizondev.habitbloom.habits.domain.models.NewUserHabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddHabitFlowHostModel : ScreenModel {
    private val flowPages = AddHabitFlowScreen.entries

    private val _flowPageState = MutableStateFlow(0)
    val flowPageState = _flowPageState.asStateFlow()

    fun updatedFlowPage(page: AddHabitFlowScreen) {
        val index = flowPages.indexOf(page)
        _flowPageState.update { index }
    }

    private val _flowHabitInfoState = MutableStateFlow(NewUserHabitInfo())
    val flowHabitInfoState = _flowHabitInfoState.asStateFlow()

    fun updateTimeOfDaySelection(timeOfDay: TimeOfDay) {
        _flowHabitInfoState.update { it.copy(timeOfDay = timeOfDay) }
    }
}