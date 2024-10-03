package com.horizondev.habitbloom.habits.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class UserHabitRecordFullInfo(
    val id: Long,                 // Auto-generated ID
    val userHabitId: Long,        // Foreign key to UserHabit.id
    val date: LocalDate,          // The date the habit is scheduled
    val isCompleted: Boolean,      // Completion status
    val description: String,
    val iconUrl: String,
    val name: String,
    val shortInfo: String,
    val timeOfDay: TimeOfDay,
    val daysStreak: Int
)