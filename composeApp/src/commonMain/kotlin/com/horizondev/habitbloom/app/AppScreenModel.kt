package com.horizondev.habitbloom.app

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.auth.domain.AuthRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class AppScreenModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    init {
        initUser()
    }

    private fun initUser() = screenModelScope.launch {
        authRepository.initUser().onFailure {
            Napier.e(throwable = it, message = "initUser error")
        }
    }
}