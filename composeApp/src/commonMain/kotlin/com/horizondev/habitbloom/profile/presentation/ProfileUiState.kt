package com.horizondev.habitbloom.profile.presentation


/**
 * Represents the UI state for the Profile screen.
 */
data class ProfileUiState(
    val profile: String?,
    val isLoading: Boolean
)

/**
 * UI events that can be triggered from the Profile screen.
 */
sealed class ProfileUiEvent {
    /**
     * Event to update the user's name.
     */
    data class UpdateUserName(val name: String) : ProfileUiEvent()

    /**
     * Event to update the user's avatar.
     */
    data class UpdateUserAvatar(val avatarUrl: String) : ProfileUiEvent()

    /**
     * Event to log out.
     */
    data object Logout : ProfileUiEvent()
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class ProfileUiIntent {
    /**
     * Intent to navigate to the login screen.
     */
    data object NavigateToLogin : ProfileUiIntent()
} 