package com.horizondev.habitbloom.screens.habits.domain.models

data class HabitCategoryData(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val backgroundColorHexFirst: String,
    val backgroundColorHexSecond: String
)
