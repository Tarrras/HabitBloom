package com.horizondev.habitbloom.habits.domain.models

import kotlinx.datetime.DayOfWeek

data class NewUserHabitInfo(
    val timeOfDay: TimeOfDay? = null,
    val habitInfo: HabitInfo? = null,
    val days: List<DayOfWeek>? = null,
    val duration: Int? = null
)
