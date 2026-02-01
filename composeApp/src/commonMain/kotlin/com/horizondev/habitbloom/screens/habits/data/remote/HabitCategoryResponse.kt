package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import kotlinx.serialization.Serializable

@Serializable
data class HabitCategoryResponse(
    val title: Map<String, String>,
    val description: Map<String, String>,
    val icon: String,
    val backgroundColorHexFirst: String,
    val backgroundColorHexSecond: String,
    val id: String? = null
)

fun HabitCategoryResponse.toDomainModel(locale: String): HabitCategoryData {
    return HabitCategoryData(
        id = id.orEmpty(),
        title = title.getOrElse(locale) { title.getValue("en") },
        description = description.getOrElse(locale) { description.getValue("en") },
        iconUrl = icon,
        backgroundColorHexFirst = backgroundColorHexFirst,
        backgroundColorHexSecond = backgroundColorHexSecond
    )
}