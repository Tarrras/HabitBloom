package com.horizondev.habitbloom.core.time

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.common.settings.TimeFormatOption
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow

class TimeFormatUseCase(
    private val repository: ProfileRepository,
    private val localeManager: AppLocaleManager
) {
    val timeFormatFlow: Flow<TimeFormatOption> = repository.getTimeFormatStateFlow()

    fun getTimeFormat() = repository.getTimeFormatState()

    fun uses24HourTimeFormat(option: TimeFormatOption = getTimeFormat()): Boolean {
        return option.uses24HourFormat(localeManager.getLocale())
    }

    suspend fun updateTimeFormat(option: TimeFormatOption) {
        repository.updateTimeFormatState(option)
    }
}
