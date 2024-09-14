package com.horizondev.habitbloom.habits.domain

import com.horizondev.habitbloom.habits.data.HabitsRemoteDataSource
import com.horizondev.habitbloom.habits.data.toDomainModel
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class HabitsRepository(
    private val remoteDataSource: HabitsRemoteDataSource
) {
    suspend fun getAllHabits(): Result<List<HabitInfo>> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getHabits()
                .mapCatching { data -> data.map { habit -> habit.toDomainModel() } }
        }
    }
}