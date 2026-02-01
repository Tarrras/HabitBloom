package com.horizondev.habitbloom.screens.habits.domain.models

import kotlinx.serialization.Serializable

data class HabitInfo(
    val id: String,
    val description: String,
    val iconUrl: String,
    val name: String,
    val categoryId: String? = null,
    val isCustomHabit: Boolean = false
)

@Serializable
enum class TimeOfDay {
    Morning,
    Afternoon,
    Evening
}