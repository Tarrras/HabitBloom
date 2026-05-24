package com.horizondev.habitbloom.screens.statistic.domain.usecases

import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticPeriodRange
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticTimeUnit
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getEndOfMonth
import com.horizondev.habitbloom.utils.minusDays
import com.horizondev.habitbloom.utils.minusMonths
import com.horizondev.habitbloom.utils.plusDays
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class GetStatisticPeriodRangeUseCase {
    operator fun invoke(
        selectedTimeUnit: StatisticTimeUnit,
        periodOffset: Int
    ): StatisticPeriodRange {
        val currentDate = getCurrentDate()
        val (startDate, endDate) = when (selectedTimeUnit) {
            StatisticTimeUnit.WEEK -> {
                val startOfCurrentWeek = currentDate.calculateStartOfWeek()
                val start = startOfCurrentWeek.minusDays((-periodOffset * 7).toLong())
                start to start.plusDays(6)
            }

            StatisticTimeUnit.MONTH -> {
                val targetMonth = currentDate.minusMonths(-periodOffset.toLong())
                val start = LocalDate(targetMonth.year, targetMonth.month, 1)
                start to start.getEndOfMonth()
            }

            StatisticTimeUnit.YEAR -> {
                val targetYear = currentDate.year + periodOffset
                LocalDate(targetYear, Month.JANUARY, 1) to
                        LocalDate(targetYear, Month.DECEMBER, 31)
            }
        }

        return StatisticPeriodRange(startDate = startDate, endDate = endDate)
    }
}
