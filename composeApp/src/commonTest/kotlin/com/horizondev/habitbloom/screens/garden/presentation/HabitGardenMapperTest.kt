package com.horizondev.habitbloom.screens.garden.presentation

import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HabitGardenMapperTest {

    @Test
    fun buildHabitFlowersForGarden_usesCurrentRecordsForVitalityAndLevel() {
        val d1 = LocalDate(2026, 5, 17)
        val d2 = LocalDate(2026, 5, 18)
        val d3 = LocalDate(2026, 5, 19)
        val records = listOf(
            fullRecord(id = 1, habitId = 10, date = d1, completed = true),
            fullRecord(id = 2, habitId = 10, date = d2, completed = true),
            fullRecord(id = 3, habitId = 10, date = d3, completed = false)
        )
        val habits = listOf(
            UserHabit(
                id = 10,
                habitId = "remote-10",
                startDate = d1,
                endDate = d3,
                daysOfWeek = listOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                ),
                timeOfDay = TimeOfDay.Morning
            )
        )

        val result = buildHabitFlowersForGarden(
            habitRecords = records,
            userHabits = habits,
            today = d3
        )

        assertEquals(1, result.size)
        assertEquals(10, result.single().habitId)
        assertEquals(FlowerGrowthStage.SEED, result.single().bloomingStage)
        assertEquals(1, result.single().health.consecutiveMissedDays)
        assertTrue(result.single().health.value < 1.0f)
    }

    private fun fullRecord(
        id: Long,
        habitId: Long,
        date: LocalDate,
        completed: Boolean
    ): UserHabitRecordFullInfo {
        return UserHabitRecordFullInfo(
            id = id,
            userHabitId = habitId,
            date = date,
            isCompleted = completed,
            description = "Description",
            iconUrl = "https://example.com/icon.png",
            name = "Habit",
            timeOfDay = TimeOfDay.Morning,
            daysStreak = 0
        )
    }
}
