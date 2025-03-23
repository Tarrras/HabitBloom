package com.horizondev.habitbloom.screens.habits.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class UserHabitFullInfo(
    val userHabitId: Long,
    val startDate: LocalDate,
    val description: String,
    val iconUrl: String,
    val name: String,
    val timeOfDay: TimeOfDay,
    val daysStreak: Int,
    val records: List<UserHabitRecord>,
    val repeats: Int,
    val completedRepeats: Int,
    val days: List<DayOfWeek>,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime? = null
)