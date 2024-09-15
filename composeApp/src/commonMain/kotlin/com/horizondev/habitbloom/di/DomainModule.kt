package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.habits.domain.HabitsRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
    singleOf(::HabitsRepository)
}