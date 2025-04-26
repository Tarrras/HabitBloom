package com.horizondev.habitbloom.screens.onboarding

/**
 * UI state for the onboarding flow.
 */
data class OnboardingUiState(
    val isLoading: Boolean = false
)

/**
 * Events that can be triggered from the onboarding UI.
 */
sealed interface OnboardingUiEvent {
    /**
     * Event triggered when the user completes the onboarding flow.
     */
    data object FinishOnboarding : OnboardingUiEvent
}

/**
 * One-time events that the ViewModel emits to the UI.
 */
sealed interface OnboardingUiIntent {
    /**
     * Intent to navigate to the main app screen.
     */
    data object NavigateToMainScreen : OnboardingUiIntent
} 