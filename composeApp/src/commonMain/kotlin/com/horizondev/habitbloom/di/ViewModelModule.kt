package com.horizondev.habitbloom.di

import CreatePersonalHabitViewModel
import com.horizondev.habitbloom.calendar.CalendarViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.AddHabitFlowViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice.AddHabitDurationViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise.AddHabitChoiceViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.success.AddHabitSuccessViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary.AddHabitSummaryViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayViewModel
import com.horizondev.habitbloom.screens.habits.presentation.habitDetails.HabitDetailsViewModel
import com.horizondev.habitbloom.screens.habits.presentation.home.HomeViewModel
import com.horizondev.habitbloom.screens.profile.presentation.ProfileViewModel
import com.horizondev.habitbloom.screens.statistic.StatisticViewModel
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

    // Habit details
    viewModelOf(::HabitDetailsViewModel)

    // Add Habit flow ViewModels
    viewModelOf(::AddHabitFlowViewModel)
    viewModelOf(::AddHabitTimeOfDayViewModel)
    viewModelOf(::AddHabitChoiceViewModel)
    viewModelOf(::AddHabitDurationViewModel)
    viewModelOf(::AddHabitSummaryViewModel)
    viewModelOf(::AddHabitSuccessViewModel)
    viewModelOf(::CreatePersonalHabitViewModel)
}