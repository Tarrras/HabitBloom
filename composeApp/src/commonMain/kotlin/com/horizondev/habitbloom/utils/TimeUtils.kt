package com.horizondev.habitbloom.utils

import kotlinx.datetime.LocalTime

/**
 * Formats a LocalTime into a readable string based on the specified format.
 *
 * @param time The time to format
 * @param use24HourFormat Whether to use 24-hour format (true) or 12-hour format with AM/PM (false)
 * @return A formatted time string
 */
fun formatTime(time: LocalTime, use24HourFormat: Boolean): String {
    return if (use24HourFormat) {
        "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
    } else {
        val hour = when {
            time.hour == 0 -> 12
            time.hour > 12 -> time.hour - 12
            else -> time.hour
        }
        val amPm = if (time.hour >= 12) "PM" else "AM"
        "${hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')} $amPm"
    }
}