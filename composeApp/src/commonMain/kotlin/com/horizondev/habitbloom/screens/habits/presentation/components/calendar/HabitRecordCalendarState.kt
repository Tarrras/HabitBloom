package com.horizondev.habitbloom.screens.habits.presentation.components.calendar

import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import kotlinx.datetime.LocalDate

internal enum class HabitRecordVisualState {
    Completed,
    Missed,
    Future,
    None
}

internal fun resolveHabitRecordState(
    record: UserHabitRecord?,
    date: LocalDate,
    today: LocalDate
): HabitRecordVisualState {
    return when {
        record == null -> HabitRecordVisualState.None
        record.isCompleted -> HabitRecordVisualState.Completed
        date < today -> HabitRecordVisualState.Missed
        else -> HabitRecordVisualState.Future
    }
}
