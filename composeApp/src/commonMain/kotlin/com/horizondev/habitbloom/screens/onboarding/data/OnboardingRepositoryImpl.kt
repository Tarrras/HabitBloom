package com.horizondev.habitbloom.screens.onboarding.data

import com.horizondev.habitbloom.screens.onboarding.domain.OnboardingRepository
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Implementation of [OnboardingRepository] that stores onboarding state in preferences.
 */
class OnboardingRepositoryImpl(
    private val preferencesDataSource: ObservableSettings
) : OnboardingRepository {

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    override suspend fun isOnboardingCompleted(): Boolean = withContext(Dispatchers.IO) {
        preferencesDataSource.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) = withContext(Dispatchers.IO) {
        preferencesDataSource.putBoolean(KEY_ONBOARDING_COMPLETED, completed)
    }
} 