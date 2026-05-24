package com.horizondev.habitbloom.screens.statistic.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.statistic.domain.models.MonthlyStatistic
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticPeriodAggregate
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticPeriodRange
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticTimeUnit
import com.horizondev.habitbloom.screens.statistic.domain.models.WeeklyStatistic
import com.horizondev.habitbloom.screens.statistic.domain.models.YearlyStatistic
import com.horizondev.habitbloom.utils.plusDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class CalculateStatisticPeriodAggregateUseCase {
    operator fun invoke(
        habitRecords: List<UserHabitRecordFullInfo>,
        periodRange: StatisticPeriodRange,
        selectedTimeUnit: StatisticTimeUnit
    ): StatisticPeriodAggregate {
        return when (selectedTimeUnit) {
            StatisticTimeUnit.WEEK -> StatisticPeriodAggregate.Week(
                calculateWeeklyStatistic(
                    habitRecords = habitRecords,
                    periodRange = periodRange
                )
            )

            StatisticTimeUnit.MONTH -> StatisticPeriodAggregate.Month(
                calculateMonthlyStatistic(
                    habitRecords = habitRecords,
                    periodRange = periodRange
                )
            )

            StatisticTimeUnit.YEAR -> StatisticPeriodAggregate.Year(
                calculateYearlyStatistic(
                    habitRecords = habitRecords,
                    periodRange = periodRange
                )
            )
        }
    }

    private fun calculateWeeklyStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        periodRange: StatisticPeriodRange
    ): WeeklyStatistic {
        val recordsInWeek = habitRecords
            .asSequence()
            .filter { it.date in periodRange.startDate..periodRange.endDate }
            .toList()
        val completedRecordsInWeek = recordsInWeek.filter { it.isCompleted }

        return WeeklyStatistic(
            completedByDay = DayOfWeek.entries.associateWith { day ->
                val date = periodRange.startDate.plusDays(day.ordinal.toLong())
                completedRecordsInWeek.count { it.date == date }
            },
            scheduledByDay = DayOfWeek.entries.associateWith { day ->
                val date = periodRange.startDate.plusDays(day.ordinal.toLong())
                recordsInWeek.count { it.date == date }
            },
            periodRange = periodRange
        )
    }

    private fun calculateMonthlyStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        periodRange: StatisticPeriodRange
    ): MonthlyStatistic {
        val recordsInMonth = habitRecords.filter {
            it.date in periodRange.startDate..periodRange.endDate
        }
        val completedByDay = mutableMapOf<Int, Int>()
        val scheduledByDay = mutableMapOf<Int, Int>()

        for (day in periodRange.startDate.day..periodRange.endDate.day) {
            val date = LocalDate(periodRange.startDate.year, periodRange.startDate.month, day)
            val recordsForDay = recordsInMonth.filter { it.date == date }
            completedByDay[day] = recordsForDay.count { it.isCompleted }
            scheduledByDay[day] = recordsForDay.size
        }

        return MonthlyStatistic(
            completedByDay = completedByDay,
            scheduledByDay = scheduledByDay,
            periodRange = periodRange
        )
    }

    private fun calculateYearlyStatistic(
        habitRecords: List<UserHabitRecordFullInfo>,
        periodRange: StatisticPeriodRange
    ): YearlyStatistic {
        val recordsInYear = habitRecords
            .asSequence()
            .filter { it.date in periodRange.startDate..periodRange.endDate }
            .toList()

        return YearlyStatistic(
            completedByMonth = Month.entries.associateWith { month ->
                recordsInYear.count { it.date.month == month && it.isCompleted }
            },
            scheduledByMonth = Month.entries.associateWith { month ->
                recordsInYear.count { it.date.month == month }
            },
            year = periodRange.startDate.year
        )
    }
}
