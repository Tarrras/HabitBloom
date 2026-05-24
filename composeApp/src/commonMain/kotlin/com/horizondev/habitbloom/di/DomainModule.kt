package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.time.TimeFormatUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.CalculateCalendarHabitStreaksUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.CalculateCalendarMonthlyStatisticsUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.CalendarHabitStreaksProvider
import com.horizondev.habitbloom.screens.calendar.domain.usecases.FilterCalendarHabitsUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.ObserveCalendarHabitRecordsUseCase
import com.horizondev.habitbloom.screens.calendar.domain.usecases.UpdateCalendarHabitCompletionUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.CalculateHabitStreakUseCase
import com.horizondev.habitbloom.screens.habits.domain.usecases.EnableNotificationsForReminderUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.BuildStatisticSummaryUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.CalculateStatisticPeriodAggregateUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.CalculateStatisticTimeOfDayUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.GetStatisticPeriodRangeUseCase
import com.horizondev.habitbloom.screens.statistic.domain.usecases.ObserveStatisticHabitRecordsUseCase
import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val domainModule = module {
    singleOf(::ThemeUseCase)
    singleOf(::TimeFormatUseCase)
    singleOf(::CalculateHabitStreakUseCase)
    singleOf(::ObserveCalendarHabitRecordsUseCase)
    singleOf(::FilterCalendarHabitsUseCase)
    singleOf(::CalculateCalendarHabitStreaksUseCase)
    single<CalendarHabitStreaksProvider> { get<CalculateCalendarHabitStreaksUseCase>() }
    singleOf(::CalculateCalendarMonthlyStatisticsUseCase)
    singleOf(::UpdateCalendarHabitCompletionUseCase)
    singleOf(::ObserveStatisticHabitRecordsUseCase)
    singleOf(::GetStatisticPeriodRangeUseCase)
    singleOf(::CalculateStatisticTimeOfDayUseCase)
    singleOf(::CalculateStatisticPeriodAggregateUseCase)
    singleOf(::BuildStatisticSummaryUseCase)
    factoryOf(::EnableNotificationsForReminderUseCase)
    singleOf(::AddHabitStateUseCase)
    single { PermissionsManager(permissionsController = get()) }
}
