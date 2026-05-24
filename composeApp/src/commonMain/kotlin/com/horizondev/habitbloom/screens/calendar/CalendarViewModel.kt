package com.horizondev.habitbloom.screens.calendar

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.calendar.domain.models.CalendarMonthStatistics
import com.horizondev.habitbloom.screens.calendar.domain.usecases.CalculateCalendarMonthlyStatisticsUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.CalendarHabitStreaksProvider
import com.horizondev.habitbloom.screens.calendar.domain.usecases.FilterCalendarHabitsUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.ObserveCalendarHabitRecordsUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.UpdateCalendarHabitCompletionUseCase
import com.horizondev.habitbloom.screens.habits.domain.models.HabitStreak
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.YearMonth

/**
 * ViewModel for the Calendar screen.
 */
class CalendarViewModel(
    private val observeCalendarHabitRecords: ObserveCalendarHabitRecordsUseCase,
    private val filterCalendarHabits: FilterCalendarHabitsUseCase,
    private val calculateMonthlyStatistics: CalculateCalendarMonthlyStatisticsUseCase,
    private val habitStreaksProvider: CalendarHabitStreaksProvider,
    private val updateCalendarHabitCompletion: UpdateCalendarHabitCompletionUseCase
) : BloomViewModel<CalendarUiState, CalendarUiIntent>(
    CalendarUiState(
        selectedDate = getCurrentDate(),
        currentMonth = YearMonth(getCurrentDate().year, getCurrentDate().month),
        isLoading = true
    )
) {
    private val selectedTimeOfDayFilter = MutableStateFlow<TimeOfDay?>(null)

    init {
        val now = getCurrentDate()
        val currentMonth = YearMonth(now.year, now.month)

        updateState {
            it.copy(
                selectedDate = now,
                currentMonth = currentMonth
            )
        }

        viewModelScope.launch {
            selectedTimeOfDayFilter.collectLatest { timeOfDay ->
                updateState { it.copy(selectedTimeOfDayFilter = timeOfDay) }
                updateFilteredHabitsForSelectedDate()
            }
        }

        loadCalendarData()
    }

    private fun loadCalendarData() {
        updateState { it.copy(isLoading = true) }

        observeCalendarHabitRecords()
            .distinctUntilChanged()
            .onEach { habitRecords ->
                val habitsByDate = habitRecords.groupBy { it.date }
                updateState {
                    it.copy(
                        habitsByDate = habitsByDate,
                        habitsForSelectedDate = filterCalendarHabits(
                            habitsByDate = habitsByDate,
                            selectedDate = state.value.selectedDate,
                            timeOfDayFilter = state.value.selectedTimeOfDayFilter
                        ),
                        monthlyStats = calculateMonthlyStatistics(
                            habitsByDate = habitsByDate,
                            currentMonth = state.value.currentMonth,
                            timeOfDayFilter = state.value.selectedTimeOfDayFilter
                        ).toUiState(),
                        habitsWithStreaks = habitStreaksProvider.calculate(habitRecords)
                            .toUiState(),
                        isLoading = false
                    )
                }
            }
            .catch { error ->
                updateState { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun updateFilteredHabitsForSelectedDate() {
        val timeOfDayFilter = state.value.selectedTimeOfDayFilter

        val monthlyStats = calculateMonthlyStatistics(
            habitsByDate = state.value.habitsByDate,
            currentMonth = state.value.currentMonth,
            timeOfDayFilter = timeOfDayFilter
        ).toUiState()

        updateState {
            it.copy(
                habitsForSelectedDate = filterCalendarHabits(
                    habitsByDate = state.value.habitsByDate,
                    selectedDate = state.value.selectedDate,
                    timeOfDayFilter = timeOfDayFilter
                ),
                monthlyStats = monthlyStats
            )
        }
    }


    fun handleUiEvent(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.SelectDate -> {
                val filteredHabits = filterCalendarHabits(
                    habitsByDate = state.value.habitsByDate,
                    selectedDate = event.date,
                    timeOfDayFilter = state.value.selectedTimeOfDayFilter
                )

                updateState {
                    it.copy(
                        selectedDate = event.date,
                        habitsForSelectedDate = filteredHabits,
                        showBottomSheet = filteredHabits.isNotEmpty()
                    )
                }
            }

            is CalendarUiEvent.ChangeMonth -> {
                updateState {
                    it.copy(
                        currentMonth = YearMonth(event.yearMonth.year, event.yearMonth.month)
                    )
                }

                val habitsByDate = state.value.habitsByDate
                val currentMonth = YearMonth(event.yearMonth.year, event.yearMonth.month)
                val selectedTimeOfDayFilter = state.value.selectedTimeOfDayFilter

                val monthlyStats = calculateMonthlyStatistics(
                    habitsByDate = habitsByDate,
                    currentMonth = currentMonth,
                    timeOfDayFilter = selectedTimeOfDayFilter
                ).toUiState()

                updateState {
                    it.copy(monthlyStats = monthlyStats)
                }
            }

            is CalendarUiEvent.FilterByTimeOfDay -> {
                viewModelScope.launch {
                    selectedTimeOfDayFilter.emit(event.timeOfDay)
                }
            }

            is CalendarUiEvent.ToggleHabitCompletion -> {
                viewModelScope.launch {
                    updateCalendarHabitCompletion(
                        habitRecordId = event.habitRecordId,
                        date = event.date,
                        completed = event.completed
                    )
                }
            }

            is CalendarUiEvent.CloseBottomSheet -> {
                updateState { it.copy(showBottomSheet = false) }
            }

            is CalendarUiEvent.JumpToToday -> {
                val today = getCurrentDate()
                val currentMonth = YearMonth(today.year, today.month)

                updateState {
                    it.copy(
                        selectedDate = today,
                        currentMonth = currentMonth
                    )
                }

                val filteredHabits = filterCalendarHabits(
                    habitsByDate = state.value.habitsByDate,
                    selectedDate = today,
                    timeOfDayFilter = state.value.selectedTimeOfDayFilter
                )

                if (filteredHabits.isNotEmpty()) {
                    updateState {
                        it.copy(
                            habitsForSelectedDate = filteredHabits,
                            showBottomSheet = true
                        )
                    }
                }

                val monthlyStats = calculateMonthlyStatistics(
                    habitsByDate = state.value.habitsByDate,
                    currentMonth = currentMonth,
                    timeOfDayFilter = state.value.selectedTimeOfDayFilter
                ).toUiState()

                updateState {
                    it.copy(monthlyStats = monthlyStats)
                }
            }

            is CalendarUiEvent.ShowStreakCelebration -> {
                viewModelScope.launch {
                    updateState { it.copy(celebratingHabitId = event.habitId) }

                    delay(2000)
                    updateState { it.copy(celebratingHabitId = null) }
                }
            }
        }
    }
}

private fun CalendarMonthStatistics.toUiState(): MonthlyStatistics {
    return MonthlyStatistics(
        totalHabits = totalHabits,
        completedHabits = completedHabits,
        completionRate = completionRate,
        longestStreak = longestStreak
    )
}

private fun Map<Long, HabitStreak>.toUiState(): Map<Long, HabitStreakInfo> {
    return mapValues { (_, streak) ->
        HabitStreakInfo(
            userHabitId = streak.userHabitId,
            habitName = streak.habitName,
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak
        )
    }
}
