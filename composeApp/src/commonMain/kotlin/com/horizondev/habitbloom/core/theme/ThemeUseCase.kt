package com.horizondev.habitbloom.core.theme

import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ThemeUseCase(
    private val repository: ProfileRepository
) {
    /**
     * Provides a flow of the current theme mode.
     */
    val themeModeFlow: Flow<ThemeOption> = repository.getThemeStateFlow()

    fun getThemeMode() = repository.getThemeState()

    /**
     * Updates the theme mode.
     */
    suspend fun updateThemeMode(mode: ThemeOption) {
        repository.updateThemeState(mode)
    }
} 