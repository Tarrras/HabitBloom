package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.profile.domain.ProfileRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    single {
        HabitsRepository(
            remoteDataSource = get(),
            profileRemoteDataSource = get(),
            localDataSource = get(),
            storageService = get(),
            notificationManager = get(),
            permissionsManager = get()
        )
    }
    singleOf(::AuthRepository)
    singleOf(::ProfileRepository)

    single { PermissionsManager(permissionsController = get()) }
}