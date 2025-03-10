package com.horizondev.habitbloom.habits.presentation.home

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getTimeOfDay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * ViewModel for the Home screen.
 */
class HomeViewModel(
    private val repository: HabitsRepository
) : BloomViewModel<HomeScreenUiState, HomeScreenUiIntent>(
    HomeScreenUiState(
        selectedTimeOfDay = getTimeOfDay()
    )
) {
    init {
        // Initialize data flow
        observeHabits()
    }

    private fun observeHabits() {
        val selectedTimeOfDayFlow = state.map {
            it.selectedTimeOfDay
        }.distinctUntilChanged()

        combine(
            selectedTimeOfDayFlow,
            repository.getUserHabitsByDayFlow(getCurrentDate())
        ) { selectedTimeOfDay, userHabits ->
            Triple(
                first = userHabits.size,
                second = userHabits.count { it.isCompleted },
                third = userHabits.filter { it.timeOfDay == selectedTimeOfDay }
            )
        }.onStart {
            repository.initData()
        }.onEach { (userHabitsCount, completedHabitsCount, dailyHabits) ->
            updateState {
                it.copy(
                    userHabits = dailyHabits,
                    completedHabitsCount = completedHabitsCount,
                    habitsCount = userHabitsCount,
                    userCompletedAllHabitsForTimeOfDay = dailyHabits.isNotEmpty()
                            && dailyHabits.all { habit -> habit.isCompleted }
                )
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Handles UI events from the Home screen.
     */
    fun handleUiEvent(uiEvent: HomeScreenUiEvent) {
        when (uiEvent) {
            is HomeScreenUiEvent.SelectTimeOfDay -> {
                updateState { it.copy(selectedTimeOfDay = uiEvent.timeOfDay) }
            }

            is HomeScreenUiEvent.ChangeHabitCompletionStatus -> {
                launch {
                    repository.updateHabitCompletion(
                        isCompleted = uiEvent.isCompleted,
                        habitRecordId = uiEvent.id,
                        date = getCurrentDate()
                    )
                }
            }

            is HomeScreenUiEvent.OpenHabitDetails -> {
                emitUiIntent(HomeScreenUiIntent.OpenHabitDetails(uiEvent.userHabitId))
            }
        }
    }
} 