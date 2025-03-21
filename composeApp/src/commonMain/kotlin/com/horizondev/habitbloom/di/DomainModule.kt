package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.usecases.EnableNotificationsForReminderUseCase
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val domainModule = module {
    singleOf(::HabitsRepository)
    single { Settings().makeObservable() }
    singleOf(::AuthRepository)
    singleOf(::ProfileRepository)
    singleOf(::ThemeUseCase)
    singleOf(::EnableNotificationsForReminderUseCase)
    single { PermissionsManager(permissionsController = get()) }
}