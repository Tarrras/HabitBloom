package com.horizondev.habitbloom.screens.habits.data.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.garden.data.FlowerHealthDataSource
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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

    @Test
    fun deleteAllUserData_clearsUserHabitsRecordsAndCustomCatalogOnly() = runBlocking {
        val driver = NativeSqliteDriver(
            schema = HabitBloomDatabase.Schema,
            name = "habit-bloom-delete-all-user-data-${Random.nextLong()}.db"
        )
        val database = HabitBloomDatabase(driver)
        val localDataSource = HabitsLocalDataSource(
            database = database,
            userHabitsQueries = database.userHabitsEntityQueries,
            userHabitRecordsQueries = database.userHabitRecordsEntityQueries
        )
        val catalogDataSource = HabitCatalogLocalDataSource(
            database = database,
            habitCatalogQueries = database.habitCatalogEntityQueries
        )

        try {
            val insertedUserHabitId = localDataSource.insertUserHabit(
                UserHabit(
                    id = 0L,
                    habitId = "local_custom_123",
                    startDate = LocalDate(2026, 5, 11),
                    endDate = LocalDate(2026, 5, 13),
                    daysOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                    timeOfDay = TimeOfDay.Morning
                )
            )
            database.flowerHealthEntityQueries.insertOrReplaceFlowerHealth(
                userHabitId = insertedUserHabitId,
                healthValue = 0.5,
                consecutiveMissedDays = 2,
                lastUpdatedDate = "2026-05-11"
            )
            catalogDataSource.upsertHabit(
                HabitInfo(
                    id = "official_1",
                    description = "Official habit",
                    iconUrl = "official-icon",
                    name = "Official",
                    isCustomHabit = false
                )
            )
            catalogDataSource.upsertHabit(
                HabitInfo(
                    id = "local_custom_123",
                    description = "Custom habit",
                    iconUrl = "custom-icon",
                    name = "Custom",
                    isCustomHabit = true
                )
            )

            localDataSource.deleteAllUserHabitData()
            catalogDataSource.deleteCustomHabits()
            FlowerHealthDataSource(
                database = database,
                localDataSource = localDataSource
            ).deleteAllFlowerHealth()

            assertTrue(localDataSource.getAllUserHabits().isEmpty())
            assertTrue(
                database.userHabitRecordsEntityQueries
                    .selectAllUserHabitRecords()
                    .executeAsList()
                    .isEmpty()
            )
            assertTrue(
                database.flowerHealthEntityQueries
                    .selectFlowerHealthByUserHabitId(insertedUserHabitId)
                    .executeAsOneOrNull() == null
            )
            assertEquals(
                listOf("official_1"),
                catalogDataSource.getHabits().map { it.id }
            )
        } finally {
            driver.close()
        }
    }
}
