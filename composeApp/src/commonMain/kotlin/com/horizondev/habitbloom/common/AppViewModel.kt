package com.horizondev.habitbloom.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.screens.onboarding.domain.OnboardingRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class representing the UI state of the application.
 */
data class AppUiState(
    val isLoading: Boolean = true,
    val isOnboardingNeeded: Boolean? = null
)

class AppViewModel(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {
    private val TAG = "AppViewModel"

    // UI state
    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

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

        // Check if onboarding is needed
        runCatching {
            val isOnboardingCompleted = onboardingRepository.isOnboardingCompleted()
            _state.update {
                it.copy(
                    isOnboardingNeeded = !isOnboardingCompleted,
                    isLoading = false
                )
            }
        }.onFailure {
            Napier.e("Failed to check onboarding status", it, tag = TAG)
            // Default to not showing onboarding if there's an error
            _state.update {
                it.copy(
                    isOnboardingNeeded = false,
                    isLoading = false
                )
            }
        }
    }
} 