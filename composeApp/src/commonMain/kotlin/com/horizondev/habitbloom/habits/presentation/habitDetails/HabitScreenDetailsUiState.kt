package com.horizondev.habitbloom.habits.presentation.habitDetails

import com.horizondev.habitbloom.habits.domain.models.UserHabitFullInfo

data class HabitScreenDetailsUiState(
    val habitInfo: UserHabitFullInfo? = null,
    val isLoading: Boolean = false
)

sealed interface HabitScreenDetailsUiEvent {
    data object BackPressed : HabitScreenDetailsUiEvent
}

sealed interface HabitScreenDetailsUiIntent {
    data object NavigateBack : HabitScreenDetailsUiIntent
}
