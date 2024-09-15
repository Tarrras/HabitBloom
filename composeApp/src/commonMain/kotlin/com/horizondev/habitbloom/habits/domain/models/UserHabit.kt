package com.horizondev.habitbloom.habits.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class UserHabit(
    val id: Long,                 // Auto-generated ID
    val habitId: String,          // Reference to predefined habit ID
    val startDate: LocalDate,     // Start date of the habit
    val duration: Int,            // Duration in days (1 to 90)
    val daysOfWeek: List<DayOfWeek>, // Days of the week the habit should be performed
    val timeOfDay: TimeOfDay      // Time of day
)