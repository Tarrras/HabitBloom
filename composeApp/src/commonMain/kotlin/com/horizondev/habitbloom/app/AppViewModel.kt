package com.horizondev.habitbloom.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.auth.domain.AuthRepository
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    fun initUser() = viewModelScope.launch {
        runCatching { repository.initUser() }
    }
}