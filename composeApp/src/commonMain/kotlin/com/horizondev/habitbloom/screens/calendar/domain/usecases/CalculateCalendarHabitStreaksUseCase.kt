package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.HabitStreak
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.habits.domain.usecases.CalculateHabitStreakUseCase

class CalculateCalendarHabitStreaksUseCase(
    private val calculateHabitStreak: CalculateHabitStreakUseCase
) : CalendarHabitStreaksProvider {
    override fun calculate(
        habitRecords: List<UserHabitRecordFullInfo>
    ): Map<Long, HabitStreak> {
        return runCatching {
            habitRecords
                .groupBy { it.userHabitId }
                .keys
                .associateWith { userHabitId ->
                    calculateHabitStreak(
                        userHabitId = userHabitId,
                        habitRecords = habitRecords
                    )
                }
        }.getOrDefault(emptyMap())
    }
}
