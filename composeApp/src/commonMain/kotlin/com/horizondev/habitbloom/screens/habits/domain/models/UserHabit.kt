package com.horizondev.habitbloom.screens.habits.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class UserHabit(
    val id: Long,                 // Auto-generated ID
    val habitId: String,          // Reference to predefined habit ID
    val startDate: LocalDate,     // Start date of the habit
    val endDate: LocalDate, // End date of the habit (inclusive)
    val daysOfWeek: List<DayOfWeek>, // Days of the week the habit should be performed
    val timeOfDay: TimeOfDay,     // Time of day
    val reminderEnabled: Boolean = false, // Whether reminders are enabled
    val reminderTime: LocalTime? = null   // Time for reminder notifications
)

/**
 * Convert a time string in "HH:MM" format to LocalTime or null if format is invalid
 */
fun String?.toLocalTimeOrNull(): LocalTime? {
    if (this.isNullOrBlank()) return null

    return runCatching {
        LocalTime.parse(this, format = LocalTime.Formats.ISO)
    }.getOrNull()
}

/**
 * Convert LocalTime to "HH:MM" string format or null
 */
fun LocalTime?.toTimeString(): String? {
    if (this == null) return null

    return this.toString()
}