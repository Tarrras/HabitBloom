package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.success.AddHabitSuccessViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.summary.AddHabitSummaryViewModel
import com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module that provides all ViewModels for the app.
 */
fun viewModelModule() = module {
    // Add Habit flow ViewModels
    viewModelOf(::AddHabitFlowViewModel)
    viewModelOf(::AddHabitTimeOfDayViewModel)
    viewModelOf(::AddHabitChoiceViewModel)
    viewModelOf(::AddHabitDurationViewModel)
    viewModelOf(::AddHabitSummaryViewModel)
    viewModelOf(::AddHabitSuccessViewModel)
}