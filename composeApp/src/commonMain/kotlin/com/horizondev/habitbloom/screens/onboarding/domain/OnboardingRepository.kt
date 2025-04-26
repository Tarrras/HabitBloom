package com.horizondev.habitbloom.screens.onboarding.domain

/**
 * Repository for managing onboarding-related data and preferences.
 */
interface OnboardingRepository {
    /**
     * Checks whether the onboarding process has been completed.
     *
     * @return True if onboarding has been completed, false otherwise.
     */
    suspend fun isOnboardingCompleted(): Boolean

    /**
     * Sets the onboarding completion status.
     *
     * @param completed True to mark onboarding as completed, false otherwise.
     */
    suspend fun setOnboardingCompleted(completed: Boolean)
} 