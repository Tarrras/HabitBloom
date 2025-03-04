package com.horizondev.habitbloom.habits.di

import com.horizondev.habitbloom.habits.domain.HabitsRepository
import org.koin.dsl.module

fun habitsDomainModule() = module {
    single { HabitsRepository(get(), get(), get(), get()) }
} 