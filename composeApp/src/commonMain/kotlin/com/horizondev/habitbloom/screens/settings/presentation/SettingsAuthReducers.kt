package com.horizondev.habitbloom.screens.settings.presentation

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession

fun reduceAuthSession(
    state: SettingsUiState,
    session: AuthSession,
    labels: SettingsAuthProfileLabels
): SettingsUiState {
    val user = session.user
    val profile = if (session.isAuthenticated && user != null) {
        SettingsAuthProfileUiState(
            isAuthenticated = true,
            title = user.displayName ?: user.email ?: labels.accountTitle,
            subtitle = user.email ?: labels.providerSubtitle(user.provider),
            provider = user.provider
        )
    } else {
        SettingsAuthProfileUiState(
            isAuthenticated = false,
            title = labels.guestTitle,
            subtitle = labels.guestSubtitle,
            provider = AuthProvider.Guest
        )
    }

    return state.copy(authProfile = profile)
}

data class SettingsAuthProfileLabels(
    val accountTitle: String,
    val guestTitle: String,
    val guestSubtitle: String,
    val googleSubtitle: String,
    val emailSubtitle: String,
    val appleSubtitle: String,
    val unknownSubtitle: String
) {
    fun providerSubtitle(provider: AuthProvider): String {
        return when (provider) {
            AuthProvider.Google -> googleSubtitle
            AuthProvider.Email -> emailSubtitle
            AuthProvider.Apple -> appleSubtitle
            AuthProvider.Guest -> guestTitle
            AuthProvider.Unknown -> unknownSubtitle
        }
    }
}
