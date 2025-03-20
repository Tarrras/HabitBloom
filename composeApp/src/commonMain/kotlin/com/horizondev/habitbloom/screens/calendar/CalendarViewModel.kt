package com.horizondev.habitbloom.screens.calendar

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.core.YearMonth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

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
    // Track the current time of day filter to reapply it when data is refreshed
    private val selectedTimeOfDayFilter = MutableStateFlow<TimeOfDay?>(null)

    init {
        // Initialize with current date
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val currentMonth = YearMonth(now.year, now.monthNumber)

        updateState {
            it.copy(
                selectedDate = now,
                currentMonth = currentMonth
            )
        }

        // Observe filter changes to update the view
        viewModelScope.launch {
            selectedTimeOfDayFilter.collectLatest { timeOfDay ->
                updateState { it.copy(selectedTimeOfDayFilter = timeOfDay) }
                // Update filtered habits for selected date
                updateFilteredHabitsForSelectedDate()
            }
        }
        
        loadCalendarData()
    }

    /**
     * Loads calendar data including habits and their completion status.
     * Uses a reactive approach to automatically update when data changes.
     */
    private fun loadCalendarData() {
        // Set loading state
        updateState { it.copy(isLoading = true) }

        // Use flow-based repository method to get continuous updates
        repository.getListOfAllUserHabitRecordsFlow(
            untilDate = getCurrentDate().plus(DatePeriod(years = 1))
        )
            .distinctUntilChanged()
            .onEach { habitRecords ->
                processHabitRecords(habitRecords)
            }
            .catch { error ->
                // Log error or show error state
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

    /**
     * Updates the habits for the selected date based on the current filter
     */
    private fun updateFilteredHabitsForSelectedDate() {
        val selectedDate = state.value.selectedDate
        val habitsForDate = state.value.habitsByDate[selectedDate] ?: emptyList()
        val timeOfDayFilter = state.value.selectedTimeOfDayFilter

        // Apply time of day filter
        val filteredHabits = if (timeOfDayFilter != null) {
            habitsForDate.filter { it.timeOfDay == timeOfDayFilter }
        } else {
            habitsForDate
        }

        // Update monthly statistics with filter
        val monthlyStats = calculateMonthlyStatistics(
            state.value.habitsByDate,
            state.value.currentMonth,
            timeOfDayFilter
        )

        updateState {
            it.copy(
                habitsForSelectedDate = filteredHabits,
                monthlyStats = monthlyStats
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
                // Check if the date has habits before showing bottom sheet
                val habitsForDate = state.value.habitsByDate[event.date] ?: emptyList()

                // Apply the time of day filter
                val filteredHabits = if (state.value.selectedTimeOfDayFilter != null) {
                    habitsForDate.filter { it.timeOfDay == state.value.selectedTimeOfDayFilter }
                } else {
                    habitsForDate
                }

                updateState {
                    it.copy(
                        selectedDate = event.date,
                        habitsForSelectedDate = filteredHabits,
                        showBottomSheet = filteredHabits.isNotEmpty()
                    )
                }
            }

            is CalendarUiEvent.OpenHabitDetails -> {
                emitUiIntent(CalendarUiIntent.OpenHabitDetails(event.habitId))
            }

            is CalendarUiEvent.ChangeMonth -> {
                updateState {
                    it.copy(
                        currentMonth = YearMonth(event.yearMonth.year, event.yearMonth.month)
                    )
                }

                // Recalculate monthly statistics when month changes
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
                    // Update filter flow to trigger update
                    selectedTimeOfDayFilter.emit(event.timeOfDay)
                }
            }

            is CalendarUiEvent.ToggleHabitCompletion -> {
                viewModelScope.launch {
                    repository.updateHabitCompletion(
                        habitRecordId = event.habitId,
                        date = event.date,
                        isCompleted = event.completed
                    )
                    // No need to manually reload data as the Flow will update automatically
                }
            }

            is CalendarUiEvent.CloseBottomSheet -> {
                updateState { it.copy(showBottomSheet = false) }
            }

            is CalendarUiEvent.JumpToToday -> {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val currentMonth = YearMonth(today.year, today.monthNumber)

                // Update state to show today's date and current month
                updateState {
                    it.copy(
                        selectedDate = today,
                        currentMonth = currentMonth
                    )
                }

                // Check if today has habits and show bottom sheet if it does
                val habitsForToday = state.value.habitsByDate[today] ?: emptyList()
                val filteredHabits = if (state.value.selectedTimeOfDayFilter != null) {
                    habitsForToday.filter { it.timeOfDay == state.value.selectedTimeOfDayFilter }
                } else {
                    habitsForToday
                }

                if (filteredHabits.isNotEmpty()) {
                    updateState {
                        it.copy(
                            habitsForSelectedDate = filteredHabits,
                            showBottomSheet = true
                        )
                    }
                }

                // Update monthly statistics for the new month
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
                    // Set the celebrating habit ID
                    updateState { it.copy(celebratingHabitId = event.habitId) }

                    // Clear the celebration after a delay
                    delay(2000)
                    updateState { it.copy(celebratingHabitId = null) }
                }
            }
        }
    }
} 