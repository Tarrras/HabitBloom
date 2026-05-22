package com.horizondev.habitbloom.utils

import com.horizondev.habitbloom.common.locale.AppLocale
import com.horizondev.habitbloom.common.settings.uses24HourTimeFormat
import kotlinx.datetime.LocalTime

fun formatTimeForLocale(time: LocalTime, locale: AppLocale): String {
    return formatTime(time, use24HourFormat = locale.uses24HourTimeFormat())
}

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
