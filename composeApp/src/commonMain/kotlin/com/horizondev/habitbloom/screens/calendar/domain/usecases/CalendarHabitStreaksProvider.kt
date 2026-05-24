package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.HabitStreak
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo

interface CalendarHabitStreaksProvider {
    fun calculate(habitRecords: List<UserHabitRecordFullInfo>): Map<Long, HabitStreak>
}
