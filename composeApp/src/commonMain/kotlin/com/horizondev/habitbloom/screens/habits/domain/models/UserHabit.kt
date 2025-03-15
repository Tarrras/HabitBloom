package com.horizondev.habitbloom.screens.habits.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class UserHabit(
    val id: Long,                 // Auto-generated ID
    val habitId: String,          // Reference to predefined habit ID
    val startDate: LocalDate,     // Start date of the habit
    val repeats: Int,             // Repeats (1 to 12)
    val daysOfWeek: List<DayOfWeek>, // Days of the week the habit should be performed
    val timeOfDay: TimeOfDay,     // Time of day
    val reminderEnabled: Boolean = false, // Whether reminders are enabled
    val reminderTime: LocalTime? = null   // Time for reminder notifications
)

/**
 * Convert a time string in "HH:MM" format to LocalTime or null if format is invalid
 */
fun String?.toLocalTimeOrNull(): LocalTime? {
    if (this == null || this.isBlank()) return null

    return try {
        val (hours, minutes) = this.split(":")
            .map { it.trim().toInt() }
            .take(2)

        LocalTime(hours, minutes)
    } catch (e: Exception) {
        null
    }
}

/**
 * Convert LocalTime to "HH:MM" string format or null
 */
fun LocalTime?.toTimeString(): String? {
    if (this == null) return null

    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}