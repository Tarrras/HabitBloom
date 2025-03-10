package com.horizondev.habitbloom.profile.presentation

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.profile.domain.ProfileRepository

/**
 * ViewModel for the Profile screen.
 */
class ProfileViewModel(
    private val repository: ProfileRepository
) : BloomViewModel<ProfileUiState, ProfileUiIntent>(
    ProfileUiState(
        profile = null,
        isLoading = true
    )
) {
    init {
        loadProfile()
    }

    private fun loadProfile() {
        /*        repository.getProfileFlow()
                    .onEach { profile ->
                        updateState {
                            it.copy(
                                profile = profile,
                                isLoading = false
                            )
                        }
                    }
                    .launchIn(viewModelScope)*/
    }

    /**
     * Handles UI events from the Profile screen.
     */
    fun handleUiEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.UpdateUserName -> {
                launch {
                    updateState { it.copy(isLoading = true) }
                    //repository.updateUserName(event.name)
                }
            }

            is ProfileUiEvent.UpdateUserAvatar -> {
                launch {
                    updateState { it.copy(isLoading = true) }
                    //repository.updateUserAvatar(event.avatarUrl)
                }
            }

            is ProfileUiEvent.Logout -> {
                launch {
                    //repository.logout()
                    emitUiIntent(ProfileUiIntent.NavigateToLogin)
                }
            }
        }
    }
} 