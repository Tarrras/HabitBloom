package com.horizondev.habitbloom.screens.onboarding

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.onboarding.domain.OnboardingRepository
import io.github.aakira.napier.Napier

/**
 * ViewModel for the onboarding flow.
 */
class OnboardingViewModel(
    private val repository: OnboardingRepository
) : BloomViewModel<OnboardingUiState, OnboardingUiIntent>(
    initialState = OnboardingUiState()
) {
    private val TAG = "OnboardingViewModel"

    /**
     * Handles UI events from the onboarding screens.
     */
    fun handleUiEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.FinishOnboarding -> {
                completeOnboarding()
            }
        }
    }

    /**
     * Marks onboarding as completed and navigates to the main app.
     */
    private fun completeOnboarding() {
        launch {
            runCatching {
                repository.setOnboardingCompleted(true)
            }.onSuccess {
                Napier.d("Onboarding completed successfully", tag = TAG)
                emitUiIntent(OnboardingUiIntent.NavigateToMainScreen)
            }.onFailure { error ->
                Napier.e("Failed to complete onboarding", error, tag = TAG)
                // Still navigate to main screen even if saving preference fails
                emitUiIntent(OnboardingUiIntent.NavigateToMainScreen)
            }
        }
    }
} 