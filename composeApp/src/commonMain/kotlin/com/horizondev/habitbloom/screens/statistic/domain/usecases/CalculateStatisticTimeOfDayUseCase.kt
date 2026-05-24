package com.horizondev.habitbloom.screens.statistic.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticPeriodRange

class CalculateStatisticTimeOfDayUseCase {
    operator fun invoke(
        completedHabits: List<UserHabitRecordFullInfo>,
        periodRange: StatisticPeriodRange
    ): Map<TimeOfDay, Int> {
        val completedByTimeOfDay = completedHabits
            .asSequence()
            .filter { it.date in periodRange.startDate..periodRange.endDate }
            .groupBy { it.timeOfDay }
            .mapValues { (_, value) -> value.size }

        return TimeOfDay.entries
            .sortedBy { it.ordinal }
            .associateWith { completedByTimeOfDay[it] ?: 0 }
    }
}
