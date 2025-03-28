package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class HabitScreenDetailsUiState(
    val habitInfo: UserHabitFullInfo? = null,
    val habitDurationEditEnabled: Boolean = true,
    val habitDurationEditMode: Boolean = false,
    val habitDays: List<DayOfWeek> = emptyList(),
    val habitRepeats: Int = 0,
    val durationUpdateButtonEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val showClearHistoryDialog: Boolean = false,
    val showReminderDialog: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime = LocalTime(8, 0), // Default to 8:00 AM
    val progressUiState: UserHabitProgressUiState? = null
)

data class UserHabitProgressUiState(
    val currentStreak: Int,
    val bestStreak: Int,
    val totalDone: Int,
    val overallRate: Float
)

sealed interface HabitScreenDetailsUiEvent {
    data object BackPressed : HabitScreenDetailsUiEvent
    data object DurationEditModeChanged : HabitScreenDetailsUiEvent
    data object UpdateHabitDuration : HabitScreenDetailsUiEvent

    data object RequestDeleteHabit : HabitScreenDetailsUiEvent
    data object DeleteHabit : HabitScreenDetailsUiEvent
    data object DismissHabitDeletion : HabitScreenDetailsUiEvent

    data object RequestClearHistory : HabitScreenDetailsUiEvent
    data object ClearHistory : HabitScreenDetailsUiEvent
    data object DismissClearHistory : HabitScreenDetailsUiEvent

    // Reminder events
    data object ShowReminderDialog : HabitScreenDetailsUiEvent
    data object DismissReminderDialog : HabitScreenDetailsUiEvent
    data class ReminderTimeChanged(val time: LocalTime) : HabitScreenDetailsUiEvent
    data class ReminderEnabledChanged(val enabled: Boolean) : HabitScreenDetailsUiEvent
    data object SaveReminderSettings : HabitScreenDetailsUiEvent

    data class DurationChanged(val duration: Int) : HabitScreenDetailsUiEvent
    data class DayStateChanged(
        val dayOfWeek: DayOfWeek,
        val isActive: Boolean
    ) : HabitScreenDetailsUiEvent
}

sealed interface HabitScreenDetailsUiIntent {
    data object NavigateBack : HabitScreenDetailsUiIntent
    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : HabitScreenDetailsUiIntent
}
