package com.horizondev.habitbloom.di

import CreatePersonalHabitViewModel
import com.horizondev.habitbloom.common.AppViewModel
import com.horizondev.habitbloom.screens.calendar.CalendarViewModel
import com.horizondev.habitbloom.screens.garden.presentation.HabitGardenViewModel
import com.horizondev.habitbloom.screens.garden.presentation.flowerdetail.HabitFlowerDetailViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.AddHabitFlowViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice.AddHabitDurationViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise.AddHabitChoiceViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.success.AddHabitSuccessViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary.AddHabitSummaryViewModel
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayViewModel
import com.horizondev.habitbloom.screens.habits.presentation.habitDetails.HabitDetailsViewModel
import com.horizondev.habitbloom.screens.habits.presentation.home.HomeViewModel
import com.horizondev.habitbloom.screens.onboarding.OnboardingViewModel
import com.horizondev.habitbloom.screens.settings.presentation.SettingsViewModel
import com.horizondev.habitbloom.screens.statistic.StatisticViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for ViewModels.
 */
val viewModelModule: Module = module {
    viewModelOf(::AppViewModel)

    // Tab Screens ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::StatisticViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::SettingsViewModel)

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

    // Garden flow ViewModels
    viewModelOf(::HabitGardenViewModel)
    viewModelOf(::HabitFlowerDetailViewModel)

    viewModelOf(::OnboardingViewModel)
}