package com.horizondev.habitbloom.screens.habits.domain

import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CustomHabitLocalPolicyTest {

    @Test
    fun buildLocalCustomHabitInfo_marksHabitAsLocalCustom() {
        val habit = buildLocalCustomHabitInfo(
            id = "local_custom_123",
            title = "  Drink water  ",
            description = "  Two glasses before lunch  ",
            categoryId = "health",
            icon = "local-icon"
        )

        assertEquals("local_custom_123", habit.id)
        assertEquals("Drink water", habit.name)
        assertEquals("Two glasses before lunch", habit.description)
        assertEquals("health", habit.categoryId)
        assertEquals("local-icon", habit.iconUrl)
        assertTrue(habit.isCustomHabit)
    }

    @Test
    fun mergeRemoteCatalogWithLocalCustomHabits_keepsOnlyLocalCustomHabitsFromCache() {
        val remoteOfficialHabit = habit(
            id = "remote_official",
            name = "Remote official",
            isCustomHabit = false
        )
        val cachedCustomHabit = habit(
            id = "local_custom_123",
            name = "Local custom",
            isCustomHabit = true
        )
        val staleOfficialHabit = habit(
            id = "stale_official",
            name = "Stale official",
            isCustomHabit = false
        )

        val merged = mergeRemoteCatalogWithLocalCustomHabits(
            remoteHabits = listOf(remoteOfficialHabit),
            cachedHabits = listOf(cachedCustomHabit, staleOfficialHabit)
        )

        assertEquals(listOf(remoteOfficialHabit, cachedCustomHabit), merged)
        assertFalse(merged.any { it.id == staleOfficialHabit.id })
    }

    private fun habit(
        id: String,
        name: String,
        isCustomHabit: Boolean
    ): HabitInfo {
        return HabitInfo(
            id = id,
            description = "$name description",
            iconUrl = "https://example.com/$id.png",
            name = name,
            isCustomHabit = isCustomHabit
        )
    }
}
