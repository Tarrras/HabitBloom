package com.horizondev.habitbloom.habits.domain.models

data class NewUserHabitInfo(
    val timeOfDay: TimeOfDay? = null,
    val habitInfo: HabitInfo? = null
)
