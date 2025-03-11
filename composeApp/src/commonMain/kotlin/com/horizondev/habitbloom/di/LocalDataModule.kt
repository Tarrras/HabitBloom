package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.platform.DatabaseDriverFactory
import com.horizondev.habitbloom.screens.habits.data.database.HabitsLocalDataSource
import org.koin.dsl.module

val localDataModule = module {
    single<HabitBloomDatabase> {
        HabitBloomDatabase(
            driver = get<DatabaseDriverFactory>().createDriver(),
        )
    }

    single {
        val database = get<HabitBloomDatabase>()
        HabitsLocalDataSource(
            userHabitsQueries = database.userHabitsEntityQueries,
            userHabitRecordsQueries = database.userHabitRecordsEntityQueries,
            database = database
        )
    }
}