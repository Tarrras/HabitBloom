package com.horizondev.habitbloom.auth.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthDeepLinkRepository {
    private val _event = MutableStateFlow<AuthDeepLinkEvent?>(null)
    val event: StateFlow<AuthDeepLinkEvent?> = _event.asStateFlow()

    private var nextEventId: Long = 0

    fun notifyPasswordRecoveryLinkOpened() {
        nextEventId += 1
        _event.value = AuthDeepLinkEvent.PasswordRecovery(nextEventId)
    }

    fun markHandled(event: AuthDeepLinkEvent) {
        if (_event.value == event) {
            _event.value = null
        }
    }
}

sealed interface AuthDeepLinkEvent {
    val id: Long

    data class PasswordRecovery(
        override val id: Long
    ) : AuthDeepLinkEvent
}

fun isPasswordRecoveryDeepLink(url: String): Boolean {
    val lowerUrl = url.lowercase()
    val path = lowerUrl.substringBefore("#").substringBefore("?").trimEnd('/')

    return "type=recovery" in lowerUrl || path.endsWith("/reset-password")
}
