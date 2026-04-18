package com.horizondev.habitbloom.screens.habits.domain

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HabitsRepositoryReminderUpdateTest {

    @Test
    fun applyHabitReminderUpdate_persistsOnlyAfterSuccessfulScheduling() = runBlocking {
        val events = mutableListOf<String>()

        val result = applyHabitReminderUpdate(
            enabled = true,
            reminderTime = LocalTime(9, 30),
            persistReminder = { enabled, time ->
                events += "persist:$enabled:$time"
            },
            scheduleReminder = { time ->
                events += "schedule:$time"
                Result.success(true)
            },
            cancelReminder = {
                events += "cancel"
            }
        )

        assertTrue(result)
        assertEquals(
            listOf(
                "schedule:09:30",
                "persist:true:09:30"
            ),
            events
        )
    }

    @Test
    fun applyHabitReminderUpdate_doesNotPersistWhenSchedulingFails() = runBlocking {
        val events = mutableListOf<String>()

        assertFailsWith<IllegalStateException> {
            applyHabitReminderUpdate(
                enabled = true,
                reminderTime = LocalTime(9, 30),
                persistReminder = { enabled, time ->
                    events += "persist:$enabled:$time"
                },
                scheduleReminder = { time ->
                    events += "schedule:$time"
                    Result.failure(IllegalStateException("permission denied"))
                },
                cancelReminder = {
                    events += "cancel"
                }
            )
        }

        assertEquals(listOf("schedule:09:30"), events)
    }

    @Test
    fun applyHabitReminderUpdate_cancelsBeforePersistingDisabledReminder() = runBlocking {
        val events = mutableListOf<String>()

        val result = applyHabitReminderUpdate(
            enabled = false,
            reminderTime = null,
            persistReminder = { enabled, time ->
                events += "persist:$enabled:$time"
            },
            scheduleReminder = { time ->
                events += "schedule:$time"
                Result.success(true)
            },
            cancelReminder = {
                events += "cancel"
            }
        )

        assertTrue(result)
        assertEquals(
            listOf(
                "cancel",
                "persist:false:null"
            ),
            events
        )
        assertFalse(events.any { it.startsWith("schedule:") })
    }
}
