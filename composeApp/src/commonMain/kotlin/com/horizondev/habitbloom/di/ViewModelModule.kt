package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.calendar.CalendarViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.success.AddHabitSuccessViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.summary.AddHabitSummaryViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayViewModel
import com.horizondev.habitbloom.habits.presentation.home.HomeViewModel
import com.horizondev.habitbloom.profile.presentation.ProfileViewModel
import com.horizondev.habitbloom.statistic.StatisticViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for ViewModels.
 */
val viewModelModule: Module = module {
    // Tab Screens ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::StatisticViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::ProfileViewModel)

    // Add Habit flow ViewModels
    viewModelOf(::AddHabitFlowViewModel)
    viewModelOf(::AddHabitTimeOfDayViewModel)
    viewModelOf(::AddHabitChoiceViewModel)
    viewModelOf(::AddHabitDurationViewModel)
    viewModelOf(::AddHabitSummaryViewModel)
    viewModelOf(::AddHabitSuccessViewModel)
}