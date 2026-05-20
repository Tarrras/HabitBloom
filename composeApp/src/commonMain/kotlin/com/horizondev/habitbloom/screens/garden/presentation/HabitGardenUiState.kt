package com.horizondev.habitbloom.screens.garden.presentation

import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

data class HabitGardenUiState(
    val isLoading: Boolean = false,
    val habitFlowers: List<HabitFlower> = emptyList(),
    val selectedTimeOfDay: TimeOfDay = TimeOfDay.Morning,
    val errorMessage: String? = null,
    val themeOption: ThemeOption
)

sealed class HabitGardenUiEvent {
    data class SelectTimeOfDay(val timeOfDay: TimeOfDay) : HabitGardenUiEvent()
    data class OpenFlowerDetails(val habitId: Long) : HabitGardenUiEvent()
    data object RefreshGarden : HabitGardenUiEvent()
    data object BackPressed : HabitGardenUiEvent()
}


sealed class HabitGardenUiIntent {
    data class OpenFlowerDetails(val habitId: Long) : HabitGardenUiIntent()
    data object NavigateBack : HabitGardenUiIntent()
} 