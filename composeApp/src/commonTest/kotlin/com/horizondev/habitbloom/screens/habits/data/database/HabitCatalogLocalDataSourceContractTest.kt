package com.horizondev.habitbloom.screens.habits.data.database

import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import kotlinx.coroutines.flow.Flow
import kotlin.test.Test
import kotlin.test.assertNotNull

class HabitCatalogLocalDataSourceContractTest {

    @Test
    fun exposesReactiveHabitCatalogFlow() {
        val observer: (HabitCatalogLocalDataSource) -> Flow<List<HabitInfo>> = {
            it.observeHabits()
        }

        assertNotNull(observer)
    }
}
