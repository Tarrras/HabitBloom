package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.serialization.Serializable

@Serializable
data class HabitLocalizationResponse(
    val name: String,
    val description: String
)

@Serializable
data class OfficialHabitInfoResponse(
    val id: String?,
    val iconUrl: String,
    val timeOfDay: TimeOfDayResponse,
    val categoryId: String? = null,
    val localizations: Map<String, HabitLocalizationResponse>
)

fun OfficialHabitInfoResponse.toDomainModel(
    locale: String
): HabitInfo? {
    val localization = localizations[locale] ?: localizations["en"]

    if (localization == null) return null

    return HabitInfo(
        id = id.orEmpty(),
        description = localization.description,
        iconUrl = iconUrl,
        name = localization.name,
        timeOfDay = timeOfDay.toDomainModel(),
        categoryId = categoryId,
        isCustomHabit = false
    )
}

@Serializable
data class HabitInfoResponse(
    val id: String? = null,
    val description: String,
    val iconUrl: String,
    val name: String,
    val shortInfo: String,
    val timeOfDay: TimeOfDayResponse,
    val categoryId: String? = null,
    val userId: String? = null
)


enum class TimeOfDayResponse {
    Morning,
    Afternoon,
    Evening
}

fun HabitInfoResponse.toDomainModel() = HabitInfo(
    id = id.orEmpty(),
    description = description,
    iconUrl = iconUrl,
    name = name,
    timeOfDay = timeOfDay.toDomainModel(),
    categoryId = categoryId,
    isCustomHabit = userId != null || id?.startsWith("user_") == true
)

fun TimeOfDayResponse.toDomainModel() = when (this) {
    TimeOfDayResponse.Morning -> TimeOfDay.Morning
    TimeOfDayResponse.Afternoon -> TimeOfDay.Afternoon
    TimeOfDayResponse.Evening -> TimeOfDay.Evening
}

fun TimeOfDay.toNetworkModel() = when (this) {
    TimeOfDay.Morning -> TimeOfDayResponse.Morning
    TimeOfDay.Afternoon -> TimeOfDayResponse.Afternoon
    TimeOfDay.Evening -> TimeOfDayResponse.Evening
}