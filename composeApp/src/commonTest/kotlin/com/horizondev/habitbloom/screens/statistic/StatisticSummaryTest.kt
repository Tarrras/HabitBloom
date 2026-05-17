package com.horizondev.habitbloom.screens.statistic

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class StatisticSummaryTest {

    @Test
    fun buildStatisticSummary_usesCompletedPeriodRecordsForHighlights() {
        val records = listOf(
            record(
                name = "Drink water",
                date = LocalDate(2026, 5, 1),
                completed = true,
                streak = 7
            ),
            record(
                name = "Drink water",
                date = LocalDate(2026, 5, 2),
                completed = true,
                streak = 5
            ),
            record(name = "Read", date = LocalDate(2026, 5, 2), completed = false, streak = 3),
            record(name = "Read", date = LocalDate(2026, 5, 3), completed = true, streak = 4),
        )

        val summary = buildStatisticSummary(
            periodHabitRecords = records,
            completedByTimeOfDay = mapOf(
                TimeOfDay.Morning to 2,
                TimeOfDay.Afternoon to 1,
                TimeOfDay.Evening to 0
            ),
            completedByPeriod = mapOf("Week 1" to 3),
            scheduledByPeriod = mapOf("Week 1" to 4)
        )

        assertEquals(3, summary.completedHabits)
        assertEquals(7, summary.longestStreak)
        assertEquals(75, summary.averageCompletionRate)
        assertEquals("Drink water", summary.bestHabitName)
        assertEquals(100, summary.bestHabitCompletionRate)
        assertEquals(
            mapOf(
                TimeOfDay.Morning to 67,
                TimeOfDay.Afternoon to 33,
                TimeOfDay.Evening to 0
            ),
            summary.timeOfDayCompletionRates
        )
    }

    @Test
    fun buildMonthlyChartData_createsOneBucketForEveryDayOfMonth() {
        val records = listOf(
            record(
                name = "Drink water",
                date = LocalDate(2026, 5, 1),
                completed = true,
                streak = 1
            ),
            record(name = "Read", date = LocalDate(2026, 5, 31), completed = false, streak = 0),
        )

        val chartData = buildMonthlyChartData(
            habitRecords = records,
            startOfMonth = LocalDate(2026, 5, 1),
            endOfMonth = LocalDate(2026, 5, 31),
            monthLabel = "May",
            monthShortLabel = "May"
        )

        assertEquals((1..31).map { it.toString() }, chartData.monthlyCategories)
        assertEquals(1, chartData.monthlyCompletedData["1"])
        assertEquals(0, chartData.monthlyCompletedData["31"])
        assertEquals(1, chartData.monthlyScheduledData["1"])
        assertEquals(1, chartData.monthlyScheduledData["31"])
        assertEquals("1 May", chartData.xAxisStartLabel)
        assertEquals("31 May", chartData.xAxisEndLabel)
    }

    private fun record(
        name: String,
        date: LocalDate,
        completed: Boolean,
        streak: Int,
        timeOfDay: TimeOfDay = TimeOfDay.Morning
    ): UserHabitRecordFullInfo = UserHabitRecordFullInfo(
        id = date.day.toLong(),
        userHabitId = name.hashCode().toLong(),
        date = date,
        isCompleted = completed,
        description = "",
        iconUrl = "",
        name = name,
        timeOfDay = timeOfDay,
        daysStreak = streak
    )
}
