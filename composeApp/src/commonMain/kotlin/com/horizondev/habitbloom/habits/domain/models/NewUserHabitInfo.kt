package com.horizondev.habitbloom.habits.domain.models

import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class NewUserHabitInfo(
    val timeOfDay: TimeOfDay? = null,
    val habitInfo: HabitInfo? = null,
    val days: List<DayOfWeek>? = null,
    val duration: Int? = null,
    val startDate: LocalDate? = null,
    val weekStartOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK
)
