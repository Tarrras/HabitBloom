package com.horizondev.habitbloom.screens.habits.presentation.home

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getTimeOfDay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.LocalDate

/**
 * ViewModel for the Home screen.
 */
class HomeViewModel(
    private val repository: HabitsRepository
) : BloomViewModel<HomeScreenUiState, HomeScreenUiIntent>(
    HomeScreenUiState(
        selectedTimeOfDay = getTimeOfDay(),
        selectedDate = getCurrentDate()
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

        val selectedDateFlow = state.map {
            it.selectedDate
        }.distinctUntilChanged()
        
        updateState { it.copy(isLoading = true) }

        combine(
            selectedTimeOfDayFlow,
            selectedDateFlow,
            repository.getUserHabitsByDayFlow(getCurrentDate())
        ) { selectedTimeOfDay, selectedDate, userHabits ->
            updateState { it.copy(isLoading = true) }

            // Get habits for the selected date
            val selectedDateHabits = repository.getUserHabitsByDayFlow(selectedDate)
                .map { habits -> habits }
                .onEach { habits ->
                    val filteredHabits = habits.filter { it.timeOfDay == selectedTimeOfDay }
                    updateState {
                        it.copy(
                            userHabits = filteredHabits,
                            habitsCount = habits.size,
                            completedHabitsCount = habits.count { it.isCompleted },
                            userCompletedAllHabitsForTimeOfDay = filteredHabits.isNotEmpty()
                                    && filteredHabits.all { habit -> habit.isCompleted },
                            isLoading = false
                        )
                    }
                }.launchIn(viewModelScope)
        }.onStart {
            repository.initData()
        }.launchIn(viewModelScope)
    }

    /**
     * Handles UI events from the Home screen.
     */
    fun handleUiEvent(event: HomeScreenUiEvent) {
        when (event) {
            is HomeScreenUiEvent.SelectTimeOfDay -> {
                updateState { it.copy(selectedTimeOfDay = event.timeOfDay) }
            }

            is HomeScreenUiEvent.SelectDate -> {
                updateState { it.copy(selectedDate = event.date) }
                // When date changes, reload habits for that date
                loadHabitsForDate(event.date)
            }

            is HomeScreenUiEvent.OpenHabitDetails -> {
                emitUiIntent(HomeScreenUiIntent.OpenHabitDetails(event.userHabitId))
            }

            is HomeScreenUiEvent.ChangeHabitCompletionStatus -> {
                changeHabitCompletionStatus(
                    habitRecordId = event.id,
                    isCompleted = event.isCompleted
                )
            }
        }
    }

    private fun loadHabitsForDate(date: LocalDate) {
        // Update observed habits with the new selected date
        repository.getUserHabitsByDayFlow(date)
            .onEach { habits ->
                val filteredHabits = habits.filter { it.timeOfDay == state.value.selectedTimeOfDay }
                updateState {
                    it.copy(
                        userHabits = filteredHabits,
                        habitsCount = habits.size,
                        completedHabitsCount = habits.count { it.isCompleted },
                        userCompletedAllHabitsForTimeOfDay = filteredHabits.isNotEmpty()
                                && filteredHabits.all { habit -> habit.isCompleted },
                        isLoading = false
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun changeHabitCompletionStatus(habitRecordId: Long, isCompleted: Boolean) {
        launch {
            repository.updateHabitCompletion(
                habitRecordId = habitRecordId,
                date = state.value.selectedDate, // Use selected date
                isCompleted = isCompleted
            )
        }
    }
} 