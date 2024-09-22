package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceScreenModel
import com.horizondev.habitbloom.habits.presentation.home.HomeScreenModel
import com.horizondev.habitbloom.profile.presentation.ProfileScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

fun screenModelModule() = module {
    factoryOf(::HomeScreenModel)
    factoryOf(::ProfileScreenModel)
    factoryOf(::AddHabitFlowHostModel)
    factoryOf(::AddHabitChoiceScreenModel)
}