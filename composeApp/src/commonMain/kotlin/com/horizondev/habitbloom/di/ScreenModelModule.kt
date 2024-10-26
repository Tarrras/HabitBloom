package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.app.AppScreenModel
import com.horizondev.habitbloom.calendar.CalendarScreenModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceScreenModel
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceScreenModel
import com.horizondev.habitbloom.habits.presentation.addHabit.summary.AddHabitSummaryScreenModel
import com.horizondev.habitbloom.habits.presentation.habitDetails.HabitDetailsScreenModel
import com.horizondev.habitbloom.habits.presentation.home.HomeScreenModel
import com.horizondev.habitbloom.profile.presentation.ProfileScreenModel
import com.horizondev.habitbloom.statistic.StatisticScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun screenModelModule() = module {
    singleOf(::AppScreenModel)

    factoryOf(::HomeScreenModel)
    factoryOf(::ProfileScreenModel)
    factoryOf(::StatisticScreenModel)
    factoryOf(::CalendarScreenModel)
    factoryOf(::AddHabitFlowHostModel)
    factoryOf(::AddHabitChoiceScreenModel)
    factoryOf(::AddHabitDurationChoiceScreenModel)
    factoryOf(::AddHabitSummaryScreenModel)
    factoryOf(::HabitDetailsScreenModel)
}