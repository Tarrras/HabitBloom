package com.horizondev.habitbloom.utils

import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

private val customFormat = LocalDate.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED); char(' '); dayOfMonth(); chars(", "); year()
}


fun getCurrentDate(): LocalDate {
    // Obtain the current system time zone
    val timeZone = TimeZone.currentSystemDefault()

    // Get the current date in the system's default time zone
    return Clock.System.todayIn(timeZone)
}

fun getTimeOfDay(): TimeOfDay {
    val currentMoment = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val currentDateTime = currentMoment.toLocalDateTime(timeZone)
    val currentHour = currentDateTime.hour

    return when (currentHour) {
        in 5..11 -> TimeOfDay.Morning
        in 12..16 -> TimeOfDay.Afternoon
        in 17..20 -> TimeOfDay.Evening
        else -> TimeOfDay.Evening
    }
}

fun LocalDate.calculateStartOfWeek(): LocalDate {
    val daysSinceMonday = this.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    return this.minusDays(daysSinceMonday.toLong())
}

fun getFirstDateFromDaysList(daysList: List<DayOfWeek>): LocalDate? {
    if (daysList.isEmpty()) return null

    // Get today's date and day of the week
    val today = getCurrentDate()
    val daysSinceMonday = today.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    val startOfWeek = today.minusDays(daysSinceMonday.toLong())

    // Map each DayOfWeek to its LocalDate in the current week
    val datesInCurrentWeek = daysList.map { dayOfWeek ->
        val daysDifference = dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
        startOfWeek.plusDays(daysDifference.toLong())
    }

    // Return the earliest date from the list
    return datesInCurrentWeek.minOrNull()
}

fun getFirstDateAfterTodayOrNextWeek(
    daysList: List<DayOfWeek>,
    startOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK
): LocalDate? {
    if (daysList.isEmpty()) return null
    val sortedDays = daysList.sortedBy { it.ordinal }

    // Get today's date and day of the week
    val today = getCurrentDate()
    val daysSinceMonday = today.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    val startOfWeek = today.minusDays(daysSinceMonday.toLong())

    // Map each DayOfWeek to its LocalDate in the current week
    val datesInCurrentWeek = sortedDays.map { dayOfWeek ->
        val daysDifference = dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
        startOfWeek.plusDays(daysDifference.toLong())
    }

    val firstDayFromToday = datesInCurrentWeek.firstOrNull { it >= today }
    return when (startOption) {
        HabitWeekStartOption.THIS_WEEK -> firstDayFromToday
        HabitWeekStartOption.NEXT_WEEK -> {
            val minDay = datesInCurrentWeek.minOrNull()
            minDay?.plusDays(7)
        }
    }
}

fun LocalDate.formatToMmDdYy(): String {
    return this.format(customFormat)
}

fun String.mmDdYyToDate(): LocalDate {
    return LocalDate.parse(this, customFormat)
}

fun LocalDate.minusDays(days: Long): LocalDate {
    return this.minus(days, DateTimeUnit.DAY)
}

fun LocalDate.plusDays(days: Long): LocalDate {
    return this.plus(days, DateTimeUnit.DAY)
}