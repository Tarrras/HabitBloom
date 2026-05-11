package com.horizondev.habitbloom.screens.habits.data.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HabitsLocalDataSourceNativeTest {

    @Test
    fun insertUserHabit_returnsInsertedHabitIdAndCreatesRecordsWithThatId() = runBlocking {
        val driver = NativeSqliteDriver(
            schema = HabitBloomDatabase.Schema,
            name = "habit-bloom-local-data-source-${Random.nextLong()}.db"
        )
        val database = HabitBloomDatabase(driver)
        val localDataSource = HabitsLocalDataSource(
            database = database,
            userHabitsQueries = database.userHabitsEntityQueries,
            userHabitRecordsQueries = database.userHabitRecordsEntityQueries
        )

        try {
            val insertedId = localDataSource.insertUserHabit(
                UserHabit(
                    id = 0L,
                    habitId = "native-insert-test",
                    startDate = LocalDate(2026, 5, 11),
                    endDate = LocalDate(2026, 5, 13),
                    daysOfWeek = listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY
                    ),
                    timeOfDay = TimeOfDay.Morning
                )
            )

            val records = localDataSource.getUserHabitsByDate(LocalDate(2026, 5, 11))

            assertNotEquals(0L, insertedId)
            assertEquals(insertedId, records.single().userHabitId)
        } finally {
            driver.close()
        }
    }
}
