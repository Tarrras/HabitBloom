package com.horizondev.habitbloom.screens.habits.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateHabitStreakUseCaseTest {

    @Test
    fun calculatesCurrentAndLongestStreakForHabitRecordsUpToToday() {
        val result = CalculateHabitStreakUseCase().invoke(
            userHabitId = 10,
            habitRecords = listOf(
                record(userHabitId = 10, date = LocalDate(2026, 5, 18), completed = true),
                record(userHabitId = 10, date = LocalDate(2026, 5, 19), completed = true),
                record(userHabitId = 10, date = LocalDate(2026, 5, 20), completed = false),
                record(userHabitId = 10, date = LocalDate(2026, 5, 21), completed = true),
                record(userHabitId = 10, date = LocalDate(2026, 5, 22), completed = true),
                record(userHabitId = 20, date = LocalDate(2026, 5, 22), completed = true)
            )
        )

        assertEquals(10, result.userHabitId)
        assertEquals("Habit 10", result.habitName)
        assertEquals(2, result.currentStreak)
        assertEquals(2, result.longestStreak)
    }

    @Test
    fun ignoresFutureRecordsForCurrentStreak() {
        val result = CalculateHabitStreakUseCase().invoke(
            userHabitId = 10,
            habitRecords = listOf(
                record(userHabitId = 10, date = LocalDate(2026, 5, 22), completed = true),
                record(userHabitId = 10, date = LocalDate(2026, 5, 23), completed = true),
                record(userHabitId = 10, date = LocalDate(2026, 5, 24), completed = true),
                record(userHabitId = 10, date = LocalDate(2026, 5, 25), completed = true)
            )
        )

        assertEquals(3, result.currentStreak)
        assertEquals(4, result.longestStreak)
    }

    private fun record(
        userHabitId: Long,
        date: LocalDate,
        completed: Boolean
    ): UserHabitRecordFullInfo = UserHabitRecordFullInfo(
        id = date.day.toLong(),
        userHabitId = userHabitId,
        date = date,
        isCompleted = completed,
        description = "",
        iconUrl = "",
        name = "Habit $userHabitId",
        timeOfDay = TimeOfDay.Morning,
        daysStreak = 0
    )
}
