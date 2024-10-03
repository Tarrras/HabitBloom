package com.horizondev.habitbloom.habits.data.database

import com.horizondev.habitbloom.habits.domain.models.UserHabitRecord
import database.UserHabitRecordsEntity
import kotlinx.datetime.LocalDate

fun UserHabitRecordsEntity.toDomainModel() = UserHabitRecord(
    id = id,
    userHabitId = userHabitId,
    date = LocalDate.parse(date),
    isCompleted = isCompleted == 1L
)