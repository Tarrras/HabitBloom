package com.horizondev.habitbloom.screens.habits.domain.models

/**
 * Centralized definition of time-of-day ranges to keep UI and logic in sync.
 * Hours are inclusive and in 24h format.
 */
object TimeOfDayRange {
    val MORNING = Range(startHour = 5, endHour = 11, label = "6:00 - 12:00")
    val AFTERNOON = Range(startHour = 12, endHour = 16, label = "12:00 - 18:00")
    val EVENING = Range(startHour = 17, endHour = 22, label = "18:00 - 22:00")

    data class Range(
        val startHour: Int,
        val endHour: Int,
        val label: String
    )
}

fun TimeOfDay.getRangeLabel() = when (this) {
    TimeOfDay.Morning -> TimeOfDayRange.MORNING
    TimeOfDay.Afternoon -> TimeOfDayRange.AFTERNOON
    TimeOfDay.Evening -> TimeOfDayRange.EVENING
}.label


