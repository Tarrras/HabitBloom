package com.horizondev.habitbloom.screens.habits.data.database

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.screens.habits.domain.models.toLocalTimeOrNull
import database.UserHabitRecordsEntity
import database.UserHabitsEntity
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

fun UserHabitRecordsEntity.toDomainModel() = UserHabitRecord(
    id = id,
    userHabitId = userHabitId,
    date = LocalDate.parse(date),
    isCompleted = isCompleted == 1L
)

fun UserHabitsEntity.toDomainModel() = UserHabit(
    id = id,
    habitId = habitId,
    startDate = LocalDate.parse(startDate),
    repeats = repeats.toInt(),
    daysOfWeek = daysOfWeek.split(",").map { DayOfWeek.valueOf(it) },
    timeOfDay = TimeOfDay.entries[timeOfDay.toInt()],
    reminderEnabled = reminderEnabled == 1L,
    reminderTime = reminderTime?.toLocalTimeOrNull()
)