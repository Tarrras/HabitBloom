package com.horizondev.habitbloom.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class AppViewModel(
    private val authRepository: AuthRepository,
    private val habitsRepository: HabitsRepository
) : ViewModel() {
    private val TAG = "AppViewModel"

    /**
     * Initializes the app and performs necessary startup operations.
     */
    fun initApp() = viewModelScope.launch {
        // Initialize user authentication
        runCatching {
            authRepository.initUser()
        }.onFailure {
            Napier.e("Failed to initialize user", it, tag = TAG)
        }

        /*// Update flower health for habits that were missed while the app wasn't used
        runCatching {
            habitsRepository.updateFlowerHealthForMissedDays()
        }.onFailure {
            Napier.e("Failed to update flower health for missed days", it, tag = TAG)
        }*/
    }
}