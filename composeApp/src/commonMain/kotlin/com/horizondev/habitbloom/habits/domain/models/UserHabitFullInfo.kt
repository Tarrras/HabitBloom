package com.horizondev.habitbloom.habits.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Immutable
data class UserHabitFullInfo(
    val userHabitId: Long,
    val startDate: LocalDate,
    val description: String,
    val iconUrl: String,
    val name: String,
    val shortInfo: String,
    val timeOfDay: TimeOfDay,
    val daysStreak: Int,
    val records: List<UserHabitRecord>,
    val repeats: Int,
    val days: List<DayOfWeek>
)