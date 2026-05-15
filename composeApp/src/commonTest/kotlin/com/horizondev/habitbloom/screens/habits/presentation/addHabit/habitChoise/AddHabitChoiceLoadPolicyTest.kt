package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddHabitChoiceLoadPolicyTest {

    @Test
    fun localFirstLoad_doesNotForceRemoteRefresh() {
        assertFalse(HabitCatalogLoadMode.LocalFirst.forceRefresh)
    }

    @Test
    fun remoteRefreshLoad_forcesRemoteRefresh() {
        assertTrue(HabitCatalogLoadMode.RemoteRefresh.forceRefresh)
    }
}
