package com.horizondev.habitbloom.screens.habits.di

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import org.koin.dsl.module

fun habitsDomainModule() = module {
    single { HabitsRepository(get(), get(), get(), get()) }
} 