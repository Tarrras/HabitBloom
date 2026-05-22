package com.horizondev.habitbloom.screens.calendar

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.plus

/**
 * ViewModel for the Calendar screen.
 */
class CalendarViewModel(
    private val repository: HabitsRepository
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
        val currentMonth = YearMonth(now.year, now.month.number)

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

        repository.getListOfAllUserHabitRecordsFlow(
            untilDate = getCurrentDate().plus(DatePeriod(years = 1))
        )
            .distinctUntilChanged()
            .onEach { habitRecords ->
                processHabitRecords(habitRecords)
            }
            .catch { error ->
                updateState { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun processHabitRecords(habitRecords: List<UserHabitRecordFullInfo>) {
        val habitsByDate = habitRecords.groupBy { it.date }
        val currentMonth = state.value.currentMonth
        val selectedDate = state.value.selectedDate
        val selectedTimeOfDayFilter = state.value.selectedTimeOfDayFilter

        val habitsForSelectedDate = habitsByDate[selectedDate] ?: emptyList()

        val filteredHabits = if (selectedTimeOfDayFilter != null) {
            habitsForSelectedDate.filter { it.timeOfDay == selectedTimeOfDayFilter }
        } else {
            habitsForSelectedDate
        }

        val monthlyStats =
            calculateMonthlyStatistics(habitsByDate, currentMonth, selectedTimeOfDayFilter)

        val habitsWithStreaks = calculateHabitStreaks(habitRecords)

        updateState {
            it.copy(
                habitsByDate = habitsByDate,
                habitsForSelectedDate = filteredHabits.sortedBy { habit -> habit.timeOfDay.ordinal },
                monthlyStats = monthlyStats,
                habitsWithStreaks = habitsWithStreaks,
                isLoading = false
            )
        }
    }

    private fun updateFilteredHabitsForSelectedDate() {
        val selectedDate = state.value.selectedDate
        val habitsForDate = state.value.habitsByDate[selectedDate] ?: emptyList()
        val timeOfDayFilter = state.value.selectedTimeOfDayFilter

        val filteredHabits = if (timeOfDayFilter != null) {
            habitsForDate.filter { it.timeOfDay == timeOfDayFilter }
        } else {
            habitsForDate
        }

        val monthlyStats = calculateMonthlyStatistics(
            state.value.habitsByDate,
            state.value.currentMonth,
            timeOfDayFilter
        )

        updateState {
            it.copy(
                habitsForSelectedDate = filteredHabits.sortedBy { habit -> habit.timeOfDay.ordinal },
                monthlyStats = monthlyStats
            )
        }
    }

    private fun calculateMonthlyStatistics(
        habitsByDate: Map<LocalDate, List<UserHabitRecordFullInfo>>,
        currentMonth: YearMonth,
        timeOfDayFilter: TimeOfDay?
    ): MonthlyStatistics {
        val habitsInMonth = habitsByDate.filter { (date, _) ->
            date.year == currentMonth.year && date.month == currentMonth.month
        }

        var totalHabits = 0
        var completedHabits = 0

        habitsInMonth.forEach { (_, habits) ->
            val filteredHabits = if (timeOfDayFilter != null) {
                habits.filter { it.timeOfDay == timeOfDayFilter }
            } else {
                habits
            }

            totalHabits += filteredHabits.size
            completedHabits += filteredHabits.count { it.isCompleted }
        }

        val completionRate = if (totalHabits > 0) {
            completedHabits.toFloat() / totalHabits
        } else {
            0f
        }

        val streaks = calculateHabitStreaks(habitsInMonth.values.flatten())
        val longestStreak = streaks.values.maxOfOrNull { it.currentStreak } ?: 0

        return MonthlyStatistics(
            totalHabits = totalHabits,
            completedHabits = completedHabits,
            completionRate = completionRate,
            longestStreak = longestStreak
        )
    }

    private fun calculateHabitStreaks(habitRecords: List<UserHabitRecordFullInfo>): Map<Long, HabitStreakInfo> {
        return runCatching {
            val result = mutableMapOf<Long, HabitStreakInfo>()

            val habitGroups = habitRecords.groupBy { it.userHabitId }

            habitGroups.forEach { (userHabitId, records) ->
                if (records.isEmpty()) return@forEach

                result[userHabitId] = repository.calculateHabitStreak(
                    userHabitId = userHabitId,
                    habitRecords = habitRecords
                )
            }

            result
        }.getOrNull()?.toMap() ?: emptyMap()
    }


    fun handleUiEvent(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.SelectDate -> {
                val habitsForDate = state.value.habitsByDate[event.date] ?: emptyList()

                val filteredHabits = if (state.value.selectedTimeOfDayFilter != null) {
                    habitsForDate.filter { it.timeOfDay == state.value.selectedTimeOfDayFilter }
                } else {
                    habitsForDate
                }

                updateState {
                    it.copy(
                        selectedDate = event.date,
                        habitsForSelectedDate = filteredHabits.sortedBy { habit -> habit.timeOfDay.ordinal },
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
                    habitsByDate,
                    currentMonth,
                    selectedTimeOfDayFilter
                )

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
                    val today = getCurrentDate()

                    if (event.date == today) {
                        repository.updateHabitCompletionByRecordId(
                            habitRecordId = event.habitRecordId,
                            date = event.date,
                            isCompleted = event.completed
                        )
                    } else {
                    }
                }
            }

            is CalendarUiEvent.CloseBottomSheet -> {
                updateState { it.copy(showBottomSheet = false) }
            }

            is CalendarUiEvent.JumpToToday -> {
                val today = getCurrentDate()
                val currentMonth = YearMonth(today.year, today.monthNumber)

                updateState {
                    it.copy(
                        selectedDate = today,
                        currentMonth = currentMonth
                    )
                }

                val habitsForToday = state.value.habitsByDate[today] ?: emptyList()
                val filteredHabits = if (state.value.selectedTimeOfDayFilter != null) {
                    habitsForToday.filter { it.timeOfDay == state.value.selectedTimeOfDayFilter }
                } else {
                    habitsForToday
                }

                if (filteredHabits.isNotEmpty()) {
                    updateState {
                        it.copy(
                            habitsForSelectedDate = filteredHabits.sortedBy { habit -> habit.timeOfDay.ordinal },
                            showBottomSheet = true
                        )
                    }
                }

                val monthlyStats = calculateMonthlyStatistics(
                    state.value.habitsByDate,
                    currentMonth,
                    state.value.selectedTimeOfDayFilter
                )

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