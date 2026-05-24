package com.horizondev.habitbloom.screens.statistic.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.statistic.domain.models.StatisticSummaryStats
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

class BuildStatisticSummaryUseCase {
    operator fun invoke(
        periodHabitRecords: List<UserHabitRecordFullInfo>,
        completedByTimeOfDay: Map<TimeOfDay, Int>,
        today: LocalDate = getCurrentDate()
    ): StatisticSummaryStats {
        val completedRecords = periodHabitRecords.filter { it.isCompleted }
        val currentAndPastRecords = periodHabitRecords.filter { it.date <= today }
        val totalCompleted = currentAndPastRecords.count { it.isCompleted }
        val totalScheduled = currentAndPastRecords.size
        val averageCompletionRate = if (totalScheduled == 0) {
            0
        } else {
            (totalCompleted.toFloat() / totalScheduled * 100).roundToInt()
        }

        val bestHabit = completedRecords
            .groupBy { it.name }
            .maxByOrNull { (_, records) -> records.size }

        val bestHabitCompletionRate = bestHabit
            ?.let { (_, records) ->
                val scheduledForHabit =
                    currentAndPastRecords.count { it.name == records.first().name }
                if (scheduledForHabit == 0) {
                    0
                } else {
                    (records.size.toFloat() / scheduledForHabit * 100).roundToInt()
                }
            }
            ?: 0

        val timeOfDayCompletionRates = TimeOfDay.entries.associateWith { timeOfDay ->
            if (totalCompleted == 0) {
                0
            } else {
                ((completedByTimeOfDay[timeOfDay] ?: 0).toFloat() / totalCompleted * 100)
                    .roundToInt()
            }
        }

        return StatisticSummaryStats(
            completedHabits = totalCompleted,
            longestStreak = periodHabitRecords.maxOfOrNull { it.daysStreak } ?: 0,
            averageCompletionRate = averageCompletionRate.coerceIn(0, 100),
            bestHabitName = bestHabit?.key.orEmpty(),
            bestHabitCompletionRate = bestHabitCompletionRate.coerceIn(0, 100),
            timeOfDayCompletionRates = timeOfDayCompletionRates
        )
    }
}
