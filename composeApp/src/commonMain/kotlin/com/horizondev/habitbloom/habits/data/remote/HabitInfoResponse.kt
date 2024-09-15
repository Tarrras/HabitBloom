package com.horizondev.habitbloom.habits.data.remote

import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.serialization.Serializable

@Serializable
data class HabitInfoResponse(
    val id: String? = null,
    val description: String,
    val iconUrl: String,
    val name: String,
    val shortInfo: String,
    val timeOfDay: TimeOfDayResponse
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
    shortInfo = shortInfo,
    timeOfDay = timeOfDay.toDomainModel()
)

fun TimeOfDayResponse.toDomainModel() = when (this) {
    TimeOfDayResponse.Morning -> TimeOfDay.Morning
    TimeOfDayResponse.Afternoon -> TimeOfDay.Afternoon
    TimeOfDayResponse.Evening -> TimeOfDay.Evening
}