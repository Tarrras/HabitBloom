package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.calendar.domain.models.CalendarMonthStatistics
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

class CalculateCalendarMonthlyStatisticsUseCase(
    private val habitStreaksProvider: CalendarHabitStreaksProvider
) {
    operator fun invoke(
        habitsByDate: Map<LocalDate, List<UserHabitRecordFullInfo>>,
        currentMonth: YearMonth,
        timeOfDayFilter: TimeOfDay?
    ): CalendarMonthStatistics {
        val recordsInMonth = habitsByDate
            .filter { (date, _) ->
                date.year == currentMonth.year && date.month == currentMonth.month
            }
            .values
            .flatten()

        val filteredRecordsInMonth = recordsInMonth
            .filter { habit -> timeOfDayFilter == null || habit.timeOfDay == timeOfDayFilter }

        val totalHabits = filteredRecordsInMonth.size
        val completedHabits = filteredRecordsInMonth.count { it.isCompleted }
        val completionRate = if (totalHabits > 0) {
            completedHabits.toFloat() / totalHabits
        } else {
            0f
        }
        val longestStreak = habitStreaksProvider
            .calculate(recordsInMonth)
            .values
            .maxOfOrNull { it.currentStreak } ?: 0

        return CalendarMonthStatistics(
            totalHabits = totalHabits,
            completedHabits = completedHabits,
            completionRate = completionRate,
            longestStreak = longestStreak
        )
    }
}
