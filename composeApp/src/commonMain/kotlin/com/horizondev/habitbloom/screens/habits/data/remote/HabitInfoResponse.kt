package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
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
        categoryId = categoryId,
        isCustomHabit = false
    )
}
