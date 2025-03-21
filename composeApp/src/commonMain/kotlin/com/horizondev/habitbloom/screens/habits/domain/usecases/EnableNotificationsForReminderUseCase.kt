package com.horizondev.habitbloom.screens.habits.domain.usecases

import com.horizondev.habitbloom.screens.profile.domain.ProfileRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Use case for enabling notifications when a user adds a habit with a reminder
 * or adds a reminder to an existing habit for the first time.
 */
class EnableNotificationsForReminderUseCase : KoinComponent {
    private val profileRepository: ProfileRepository by inject()

    /**
     * Enable notifications if they have not been explicitly disabled by the user.
     * This should be called when a user adds a habit with a reminder or adds a reminder to an existing habit.
     *
     * @return True if notifications were enabled or were already enabled, false if they were explicitly disabled by the user.
     */
    suspend fun execute(): Boolean {
        return profileRepository.enableNotificationsIfNotDetermined()
    }
} 