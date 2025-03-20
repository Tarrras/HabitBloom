package com.horizondev.habitbloom.screens.calendar

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.core.YearMonth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
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
    init {
        loadCalendarData()
    }

    private fun loadCalendarData() {
        repository.getListOfAllUserHabitRecordsFlow(
            untilDate = getCurrentDate().plus(DatePeriod(years = 1))
        )
            .distinctUntilChanged()
            .onEach { habitRecords ->
                processHabitRecords(habitRecords)
            }
            .catch {
                updateState { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun processHabitRecords(habitRecords: List<UserHabitRecordFullInfo>) {
        val habitsByDate = habitRecords.groupBy { it.date }
        val currentMonth = state.value.currentMonth
        val selectedDate = state.value.selectedDate
        val selectedTimeOfDayFilter = state.value.selectedTimeOfDayFilter

        // Filter habits for selected date
        val habitsForSelectedDate = habitsByDate[selectedDate] ?: emptyList()

        // Apply time of day filter if selected
        val filteredHabits = if (selectedTimeOfDayFilter != null) {
            habitsForSelectedDate.filter { it.timeOfDay == selectedTimeOfDayFilter }
        } else {
            habitsForSelectedDate
        }

        // Calculate monthly statistics
        val monthlyStats =
            calculateMonthlyStatistics(habitsByDate, currentMonth, selectedTimeOfDayFilter)

        // Calculate habit streaks
        val habitsWithStreaks = calculateHabitStreaks(habitRecords)

        updateState {
            it.copy(
                habitsByDate = habitsByDate,
                habitsForSelectedDate = filteredHabits,
                monthlyStats = monthlyStats,
                habitsWithStreaks = habitsWithStreaks,
                isLoading = false
            )
        }
    }

    private fun calculateMonthlyStatistics(
        habitsByDate: Map<LocalDate, List<UserHabitRecordFullInfo>>,
        currentMonth: YearMonth,
        timeOfDayFilter: TimeOfDay?
    ): MonthlyStatistics {
        // Filter habits that are within the current month
        val habitsInMonth = habitsByDate.filter { (date, _) ->
            date.year == currentMonth.year && date.month == currentMonth.month
        }

        // Count total and completed habits in the month, applying time of day filter if needed
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

        // Calculate completion rate
        val completionRate = if (totalHabits > 0) {
            completedHabits.toFloat() / totalHabits
        } else {
            0f
        }

        // Calculate longest streak in the month
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
        val today = getCurrentDate()
        val result = mutableMapOf<Long, HabitStreakInfo>()

        // Group records by habit ID
        val habitGroups = habitRecords.groupBy { it.userHabitId }

        habitGroups.forEach { (habitId, records) ->
            if (records.isEmpty()) return@forEach

            val habitName = records.firstOrNull()?.name ?: "Unknown Habit"

            // Sort records by date (newest first)
            val sortedRecords = records.sortedByDescending { it.date }

            // Calculate current streak
            var currentStreak = 0
            var previousDate: LocalDate? = null

            for (record in sortedRecords) {
                if (!record.isCompleted) continue

                // If this is the first completed habit we're examining
                if (previousDate == null) {
                    previousDate = record.date
                    currentStreak = 1
                    continue
                }

                // Check if this record is part of a consecutive streak
                val expectedDate = previousDate.minus(1, DateTimeUnit.DAY)
                if (record.date == expectedDate) {
                    currentStreak++
                    previousDate = record.date
                } else {
                    // The streak is broken
                    break
                }
            }

            // Calculate longest streak (historical)
            var longestStreak = 0
            var currentLongestStreak = 0
            var lastDate: LocalDate? = null

            for (record in records.sortedBy { it.date }) {
                if (!record.isCompleted) {
                    // Reset current streak if we find an uncompleted habit
                    currentLongestStreak = 0
                    lastDate = null
                    continue
                }

                if (lastDate == null) {
                    // First completed habit in a potential streak
                    currentLongestStreak = 1
                    lastDate = record.date
                } else {
                    val expectedDate = lastDate.plus(1, DateTimeUnit.DAY)
                    if (record.date == expectedDate) {
                        // Streak continues
                        currentLongestStreak++
                    } else {
                        // Streak breaks, start a new one
                        currentLongestStreak = 1
                    }

                    lastDate = record.date
                }

                // Update longest streak if current is higher
                if (currentLongestStreak > longestStreak) {
                    longestStreak = currentLongestStreak
                }
            }

            result[habitId] = HabitStreakInfo(
                habitId = habitId,
                habitName = habitName,
                currentStreak = currentStreak,
                longestStreak = longestStreak
            )
        }

        return result
    }

    /**
     * Handles UI events from the Calendar screen.
     */
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
                        habitsForSelectedDate = filteredHabits,
                        showBottomSheet = true
                    )
                }
            }

            is CalendarUiEvent.OpenHabitDetails -> {
                emitUiIntent(CalendarUiIntent.OpenHabitDetails(event.habitId))
            }

            is CalendarUiEvent.ChangeMonth -> {
                updateState {
                    it.copy(
                        currentMonth = event.yearMonth
                    )
                }

                // Recalculate monthly statistics when month changes
                val habitsByDate = state.value.habitsByDate
                val monthlyStats = calculateMonthlyStatistics(
                    habitsByDate,
                    event.yearMonth,
                    state.value.selectedTimeOfDayFilter
                )

                updateState {
                    it.copy(monthlyStats = monthlyStats)
                }
            }

            is CalendarUiEvent.FilterByTimeOfDay -> {
                val timeOfDay = event.timeOfDay
                val habitsByDate = state.value.habitsByDate
                val selectedDate = state.value.selectedDate
                val currentMonth = state.value.currentMonth

                // Update habits for selected date with filter
                val habitsForDate = habitsByDate[selectedDate] ?: emptyList()
                val filteredHabits = if (timeOfDay != null) {
                    habitsForDate.filter { it.timeOfDay == timeOfDay }
                } else {
                    habitsForDate
                }

                // Recalculate monthly statistics with new filter
                val monthlyStats = calculateMonthlyStatistics(habitsByDate, currentMonth, timeOfDay)

                updateState {
                    it.copy(
                        selectedTimeOfDayFilter = timeOfDay,
                        habitsForSelectedDate = filteredHabits,
                        monthlyStats = monthlyStats
                    )
                }
            }

            is CalendarUiEvent.ToggleHabitCompletion -> {
                /*launch {
                    repository.updateHabitCompletionStatus(
                        habitId = event.habitId,
                        date = event.date,
                        isCompleted = event.completed
                    )
                }*/
            }

            is CalendarUiEvent.CloseBottomSheet -> {
                updateState { it.copy(showBottomSheet = false) }
            }
        }
    }
} 