package com.horizondev.habitbloom.habits.domain.models

data class HabitInfo(
    val id: String,
    val description: String,
    val iconUrl: String,
    val name: String,
    val shortInfo: String,
    val timeOfDay: TimeOfDay,
    val isCustomHabit: Boolean = false
)


enum class TimeOfDay {
    Morning,
    Afternoon,
    Evening
}