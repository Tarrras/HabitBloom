package com.horizondev.habitbloom.screens.habits.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.HabitStreak
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalculateHabitStreakUseCase {
    operator fun invoke(
        userHabitId: Long,
        habitRecords: List<UserHabitRecordFullInfo>
    ): HabitStreak {
        val today = getCurrentDate()
        val records = habitRecords.filter { it.userHabitId == userHabitId }

        val habitName = records.firstOrNull()?.name ?: "Unknown Habit"

        val sortedRecords = records
            .sortedByDescending { it.date }
            .filter { it.date <= today }

        var currentStreak = 0
        var previousDate: LocalDate? = null

        for (record in sortedRecords) {
            if (!record.isCompleted) continue

            if (previousDate == null) {
                previousDate = record.date
                currentStreak = 1
                continue
            }

            val expectedDate = previousDate.minus(1, DateTimeUnit.DAY)
            if (record.date == expectedDate) {
                currentStreak++
                previousDate = record.date
            } else {
                break
            }
        }

        var longestStreak = 0
        var currentLongestStreak = 0
        var lastDate: LocalDate? = null

        for (record in records.sortedBy { it.date }) {
            if (!record.isCompleted) {
                currentLongestStreak = 0
                lastDate = null
                continue
            }

            if (lastDate == null) {
                currentLongestStreak = 1
                lastDate = record.date
            } else {
                val expectedDate = lastDate.plus(1, DateTimeUnit.DAY)
                if (record.date == expectedDate) {
                    currentLongestStreak++
                } else {
                    currentLongestStreak = 1
                }

                lastDate = record.date
            }

            if (currentLongestStreak > longestStreak) {
                longestStreak = currentLongestStreak
            }
        }

        return HabitStreak(
            userHabitId = userHabitId,
            habitName = habitName,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }
}
