package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail

data class HabitFlowerDetailUiState(
    val isLoading: Boolean = false,
    val habitFlowerDetail: HabitFlowerDetail? = null,
    val errorMessage: String? = null,
    val themeOption: ThemeOption,
    val use24HourFormat: Boolean = false
)

sealed class HabitFlowerDetailUiIntent {
    data object NavigateBack : HabitFlowerDetailUiIntent()
    data class NavigateToHabitDetails(val habitId: Long) : HabitFlowerDetailUiIntent()

    data class ShowSnackbar(val visuals: BloomSnackbarVisuals) : HabitFlowerDetailUiIntent()
}


sealed class HabitFlowerDetailUiEvent {
    data class NavigateToHabitDetails(val habitId: Long) : HabitFlowerDetailUiEvent()

    data object NavigateBack : HabitFlowerDetailUiEvent()
} 
