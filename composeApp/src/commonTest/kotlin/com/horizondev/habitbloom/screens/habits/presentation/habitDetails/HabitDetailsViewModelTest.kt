package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HabitDetailsViewModelTest {

    @Test
    fun endDateChanged_changesOnlyEndDateFields() {
        val originalStart = LocalDate(2026, 2, 1)
        val originalEnd = LocalDate(2026, 2, 20)
        val updatedEnd = LocalDate(2026, 2, 25)
        val initialState = HabitScreenDetailsUiState(
            habitInfo = createHabitInfo(startDate = originalStart, endDate = originalEnd),
            habitDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            startDate = originalStart,
            endDate = originalEnd,
            showEndDatePickerDialog = true,
            durationUpdateButtonEnabled = false
        )

        val updatedState = reduceEndDateChanged(initialState, updatedEnd)

        assertEquals(originalStart, updatedState.startDate)
        assertEquals(updatedEnd, updatedState.endDate)
        assertEquals(initialState.habitDays, updatedState.habitDays)
        assertFalse(updatedState.showEndDatePickerDialog)
        assertTrue(updatedState.durationUpdateButtonEnabled)
    }

    @Test
    fun durationEditModeChanged_resetsStagedChangesFromHabitInfo() {
        val originalStart = LocalDate(2026, 2, 1)
        val originalEnd = LocalDate(2026, 2, 20)
        val stagedStart = LocalDate(2026, 2, 2)
        val stagedEnd = LocalDate(2026, 2, 28)
        val habitInfo = createHabitInfo(
            startDate = originalStart,
            endDate = originalEnd,
            days = listOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
        )
        val initialState = HabitScreenDetailsUiState(
            habitInfo = habitInfo,
            habitDurationEditMode = false,
            habitDays = listOf(DayOfWeek.FRIDAY),
            startDate = stagedStart,
            endDate = stagedEnd
        )

        val updatedState = reduceDurationEditModeChanged(initialState)

        assertTrue(updatedState.habitDurationEditMode)
        assertEquals(habitInfo.startDate, updatedState.startDate)
        assertEquals(habitInfo.endDate, updatedState.endDate)
        assertEquals(habitInfo.days, updatedState.habitDays)
    }

    @Test
    fun updateValidation_buildsPayloadWithEndDateAndDays_andSuccessExitsEditMode() {
        val startDate = LocalDate(2026, 2, 1)
        val originalEndDate = LocalDate(2026, 2, 20)
        val updatedEndDate = LocalDate(2026, 2, 25)
        val habitInfo = createHabitInfo(
            startDate = startDate,
            endDate = originalEndDate,
            days = listOf(DayOfWeek.MONDAY)
        )
        val state = HabitScreenDetailsUiState(
            habitInfo = habitInfo,
            habitDurationEditMode = true,
            habitDays = DayOfWeek.entries.toList(),
            startDate = startDate,
            endDate = updatedEndDate
        )

        val validation = validateHabitDurationUpdate(state)
        assertTrue(validation is HabitDurationUpdateValidation.Ready)

        val payload = validation.payload
        assertEquals(habitInfo.userHabitId, payload.userHabitId)
        assertEquals(updatedEndDate, payload.endDate)
        assertEquals(state.habitDays, payload.days)

        val stateAfterSuccess = reduceDurationUpdatedSuccessfully(state)
        assertFalse(stateAfterSuccess.habitDurationEditMode)
    }

    @Test
    fun showReminderDialog_initializesDraftFromCommittedReminderState() {
        val committedTime = LocalTime(9, 15)
        val initialState = HabitScreenDetailsUiState(
            habitInfo = createHabitInfo(
                startDate = LocalDate(2026, 2, 1),
                endDate = LocalDate(2026, 2, 20),
                reminderEnabled = true,
                reminderTime = committedTime
            ),
            reminderEnabled = true,
            reminderTime = committedTime,
            reminderDraftEnabled = false,
            reminderDraftTime = LocalTime(7, 0)
        )

        val updatedState = reduceReminderDialogShown(initialState)

        assertTrue(updatedState.showReminderDialog)
        assertTrue(updatedState.reminderDraftEnabled)
        assertEquals(committedTime, updatedState.reminderDraftTime)
    }

    @Test
    fun dismissReminderDialog_discardsDraftEdits() {
        val committedTime = LocalTime(8, 0)
        val initialState = HabitScreenDetailsUiState(
            habitInfo = createHabitInfo(
                startDate = LocalDate(2026, 2, 1),
                endDate = LocalDate(2026, 2, 20),
                reminderEnabled = false,
                reminderTime = null
            ),
            showReminderDialog = true,
            showReminderTimePicker = true,
            reminderEnabled = false,
            reminderTime = committedTime,
            reminderDraftEnabled = true,
            reminderDraftTime = LocalTime(21, 45)
        )

        val updatedState = reduceReminderDialogDismissed(initialState)

        assertFalse(updatedState.showReminderDialog)
        assertFalse(updatedState.showReminderTimePicker)
        assertFalse(updatedState.reminderDraftEnabled)
        assertEquals(committedTime, updatedState.reminderDraftTime)
        assertFalse(updatedState.reminderEnabled)
        assertEquals(committedTime, updatedState.reminderTime)
    }

    @Test
    fun saveReminderSettings_commitsDraftValues() {
        val committedTime = LocalTime(8, 0)
        val draftTime = LocalTime(20, 30)
        val initialState = HabitScreenDetailsUiState(
            habitInfo = createHabitInfo(
                startDate = LocalDate(2026, 2, 1),
                endDate = LocalDate(2026, 2, 20),
                reminderEnabled = false,
                reminderTime = null
            ),
            showReminderDialog = true,
            showReminderTimePicker = true,
            reminderEnabled = false,
            reminderTime = committedTime,
            reminderDraftEnabled = true,
            reminderDraftTime = draftTime
        )

        val updatedState = reduceReminderSettingsSaved(initialState)

        assertFalse(updatedState.showReminderDialog)
        assertFalse(updatedState.showReminderTimePicker)
        assertTrue(updatedState.reminderEnabled)
        assertEquals(draftTime, updatedState.reminderTime)
        assertTrue(updatedState.reminderDraftEnabled)
        assertEquals(draftTime, updatedState.reminderDraftTime)
        val updatedHabitInfo = requireNotNull(updatedState.habitInfo)
        assertTrue(updatedHabitInfo.reminderEnabled)
        assertEquals(draftTime, updatedHabitInfo.reminderTime)
    }

    @Test
    fun showReminderTimePicker_fromDialog_closesDialogAndPreservesDraft() {
        val initialState = HabitScreenDetailsUiState(
            habitInfo = createHabitInfo(
                startDate = LocalDate(2026, 2, 1),
                endDate = LocalDate(2026, 2, 20),
                reminderEnabled = false,
                reminderTime = null
            ),
            showReminderDialog = true,
            reminderEnabled = false,
            reminderTime = LocalTime(8, 0),
            reminderDraftEnabled = true,
            reminderDraftTime = LocalTime(8, 0)
        )

        val updatedState = reduceReminderTimePickerShown(initialState)

        assertFalse(updatedState.showReminderDialog)
        assertTrue(updatedState.showReminderTimePicker)
        assertTrue(updatedState.returnToReminderDialogAfterTimePicker)
        assertTrue(updatedState.reminderDraftEnabled)
        assertEquals(LocalTime(8, 0), updatedState.reminderDraftTime)
        assertFalse(updatedState.reminderEnabled)
    }

    @Test
    fun dismissReminderTimePicker_reopensDialogWhenLaunchedFromDialog() {
        val initialState = HabitScreenDetailsUiState(
            showReminderDialog = false,
            showReminderTimePicker = true,
            returnToReminderDialogAfterTimePicker = true,
            reminderDraftEnabled = true,
            reminderDraftTime = LocalTime(9, 30)
        )

        val updatedState = reduceReminderTimePickerDismissed(initialState)

        assertTrue(updatedState.showReminderDialog)
        assertFalse(updatedState.showReminderTimePicker)
        assertFalse(updatedState.returnToReminderDialogAfterTimePicker)
        assertTrue(updatedState.reminderDraftEnabled)
        assertEquals(LocalTime(9, 30), updatedState.reminderDraftTime)
    }

    private fun createHabitInfo(
        startDate: LocalDate,
        endDate: LocalDate,
        days: List<DayOfWeek> = listOf(DayOfWeek.MONDAY),
        reminderEnabled: Boolean = false,
        reminderTime: LocalTime? = null
    ): UserHabitFullInfo {
        return UserHabitFullInfo(
            userHabitId = 100L,
            startDate = startDate,
            endDate = endDate,
            description = "desc",
            iconUrl = "icon",
            name = "habit",
            timeOfDay = TimeOfDay.Morning,
            daysStreak = 0,
            records = listOf(UserHabitRecord(1L, 100L, startDate, false)),
            days = days,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
    }
}
