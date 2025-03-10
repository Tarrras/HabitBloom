package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.profile.domain.ProfileRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::HabitsRepository)
    singleOf(::AuthRepository)
    singleOf(::ProfileRepository)
}