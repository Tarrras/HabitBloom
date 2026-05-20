package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.onboarding.data.OnboardingRepositoryImpl
import com.horizondev.habitbloom.screens.onboarding.domain.OnboardingRepository
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val dataModule = module {
    single<OnboardingRepository> {
        OnboardingRepositoryImpl(preferencesDataSource = get())
    }


    single {
        HabitsRepository(
            remoteDataSource = get(),
            localDataSource = get(),
            habitCatalogLocalDataSource = get(),
            storageService = get(),
            notificationManager = get(),
            permissionsManager = get(),
        )
    }

    single { Settings().makeObservable() }
    singleOf(::AuthRepository)
    
    single {
        ProfileRepository(
            settings = get(),
            permissionsManager = get(),
            habitsRepository = get(),
            onboardingRepository = get(),
            notificationScheduler = get()
        )
    }
} 
