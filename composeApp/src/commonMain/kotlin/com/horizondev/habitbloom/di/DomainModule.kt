package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.EnableNotificationsForReminderUseCase
import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val domainModule = module {
    singleOf(::ThemeUseCase)
    singleOf(::EnableNotificationsForReminderUseCase)
    singleOf(::AddHabitStateUseCase)
    single { PermissionsManager(permissionsController = get()) }
}