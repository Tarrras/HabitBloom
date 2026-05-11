package com.horizondev.habitbloom.screens.habits.domain

import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitRecordMergeTest {

    @Test
    fun mergeHabitRecordsWithDetails_usesPreloadedMapsAndSkipsMissingDetails() {
        val date = LocalDate(2026, 5, 11)
        val records = listOf(
            UserHabitRecord(id = 1, userHabitId = 10, date = date, isCompleted = true),
            UserHabitRecord(id = 2, userHabitId = 20, date = date, isCompleted = false),
            UserHabitRecord(id = 3, userHabitId = 30, date = date, isCompleted = true)
        )
        val userHabitsById = mapOf(
            10L to UserHabit(
                id = 10,
                habitId = "remote-water",
                startDate = date,
                endDate = date,
                daysOfWeek = listOf(DayOfWeek.MONDAY),
                timeOfDay = TimeOfDay.Morning
            ),
            20L to UserHabit(
                id = 20,
                habitId = "missing-remote",
                startDate = date,
                endDate = date,
                daysOfWeek = listOf(DayOfWeek.MONDAY),
                timeOfDay = TimeOfDay.Evening
            )
        )
        val habitsByRemoteId = mapOf(
            "remote-water" to HabitInfo(
                id = "remote-water",
                description = "Drink enough water",
                iconUrl = "https://example.com/water.png",
                name = "Water"
            )
        )
        val streaksByUserHabitId = mapOf(10L to 4, 20L to 1)

        val result = mergeHabitRecordsWithDetails(
            habitRecords = records,
            userHabitsById = userHabitsById,
            habitsByRemoteId = habitsByRemoteId,
            streaksByUserHabitId = streaksByUserHabitId
        )

        assertEquals(1, result.size)
        assertEquals(1, result.single().id)
        assertEquals(10, result.single().userHabitId)
        assertEquals("Water", result.single().name)
        assertEquals("Drink enough water", result.single().description)
        assertEquals("https://example.com/water.png", result.single().iconUrl)
        assertEquals(TimeOfDay.Morning, result.single().timeOfDay)
        assertEquals(4, result.single().daysStreak)
    }
}
