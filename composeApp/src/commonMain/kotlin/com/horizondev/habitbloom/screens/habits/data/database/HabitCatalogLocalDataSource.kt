package com.horizondev.habitbloom.screens.habits.data.database

import app.cash.sqldelight.coroutines.asFlow
import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import database.HabitCatalogEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HabitCatalogLocalDataSource(
    private val database: HabitBloomDatabase,
    private val habitCatalogQueries: HabitCatalogEntityQueries
) {

    suspend fun replaceHabits(habits: List<HabitInfo>) {
        withContext(Dispatchers.IO) {
            database.transaction {
                habitCatalogQueries.deleteAllHabitCatalogItems()
                habits.forEach { habit ->
                    upsertHabitCatalogItem(habit)
                }
            }
        }
    }

    suspend fun upsertHabit(habit: HabitInfo) {
        withContext(Dispatchers.IO) {
            upsertHabitCatalogItem(habit)
        }
    }

    suspend fun getHabits(): List<HabitInfo> {
        return withContext(Dispatchers.IO) {
            habitCatalogQueries
                .selectAllHabitCatalogItems()
                .executeAsList()
                .map { it.toDomainModel() }
        }
    }

    fun observeHabits(): Flow<List<HabitInfo>> {
        return habitCatalogQueries
            .selectAllHabitCatalogItems()
            .asFlow()
            .mapToList()
            .map { rows -> rows.map { it.toDomainModel() } }
    }

    suspend fun deleteHabit(habitId: String) {
        withContext(Dispatchers.IO) {
            habitCatalogQueries.deleteHabitCatalogItemById(habitId)
        }
    }

    suspend fun deleteCustomHabits() {
        withContext(Dispatchers.IO) {
            habitCatalogQueries.deleteCustomHabitCatalogItems()
        }
    }

    private fun upsertHabitCatalogItem(habit: HabitInfo) {
        habitCatalogQueries.upsertHabitCatalogItem(
            id = habit.id,
            description = habit.description,
            iconUrl = habit.iconUrl,
            name = habit.name,
            categoryId = habit.categoryId,
            isCustomHabit = if (habit.isCustomHabit) 1L else 0L
        )
    }
}
