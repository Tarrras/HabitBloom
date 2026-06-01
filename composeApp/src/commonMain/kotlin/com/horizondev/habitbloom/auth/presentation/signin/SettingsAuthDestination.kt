package com.horizondev.habitbloom.auth.presentation.signin

import com.horizondev.habitbloom.core.navigation.NavTarget
import kotlinx.serialization.Serializable

@Serializable
data class SettingsAuthDestination(val mode: String) : NavTarget {
    fun toAuthMode(): SettingsAuthMode {
        return when (mode) {
            SignUpMode -> SettingsAuthMode.SignUp
            else -> SettingsAuthMode.SignIn
        }
    }

    companion object {
        const val SignInMode = "sign_in"
        const val SignUpMode = "sign_up"

        fun from(mode: SettingsAuthMode): SettingsAuthDestination {
            return SettingsAuthDestination(
                when (mode) {
                    SettingsAuthMode.SignIn -> SignInMode
                    SettingsAuthMode.SignUp -> SignUpMode
                }
            )
        }
    }
}
