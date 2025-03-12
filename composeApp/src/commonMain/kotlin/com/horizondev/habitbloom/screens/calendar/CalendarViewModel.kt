package com.horizondev.habitbloom.screens.calendar

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel for the Calendar screen.
 */
class CalendarViewModel(
    private val repository: HabitsRepository
) : BloomViewModel<CalendarUiState, CalendarUiIntent>(
    CalendarUiState(
        selectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        habitsByDate = emptyMap(),
        isLoading = true
    )
) {
    init {
        loadCalendarData()
    }

    private fun loadCalendarData() {
        /*        repository.getHabitsCalendarDataFlow()
                    .onEach { calendarData ->
                        updateState {
                            it.copy(
                                habitsByDate = calendarData,
                                isLoading = false
                            )
                        }
                    }
                    .launchIn(viewModelScope)*/
    }

    /**
     * Handles UI events from the Calendar screen.
     */
    fun handleUiEvent(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.SelectDate -> {
                updateState { it.copy(selectedDate = event.date) }
                loadHabitsForDate(event.date)
            }

            is CalendarUiEvent.OpenHabitDetails -> {
                emitUiIntent(CalendarUiIntent.OpenHabitDetails(event.habitId))
            }
        }
    }

    private fun loadHabitsForDate(date: LocalDate) {
        // Load habits for the selected date if needed
        launch {
            // Here you would typically fetch habits for the specific date
            // For now we'll use what's already in the state
        }
    }
} 