package com.horizondev.habitbloom.screens.statistic.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month

data class WeeklyStatistic(
    val completedByDay: Map<DayOfWeek, Int>,
    val scheduledByDay: Map<DayOfWeek, Int>,
    val periodRange: StatisticPeriodRange
)

data class MonthlyStatistic(
    val completedByDay: Map<Int, Int>,
    val scheduledByDay: Map<Int, Int>,
    val periodRange: StatisticPeriodRange
)

data class YearlyStatistic(
    val completedByMonth: Map<Month, Int>,
    val scheduledByMonth: Map<Month, Int>,
    val year: Int
)

sealed interface StatisticPeriodAggregate {
    data class Week(val statistic: WeeklyStatistic) : StatisticPeriodAggregate
    data class Month(val statistic: MonthlyStatistic) : StatisticPeriodAggregate
    data class Year(val statistic: YearlyStatistic) : StatisticPeriodAggregate
}
