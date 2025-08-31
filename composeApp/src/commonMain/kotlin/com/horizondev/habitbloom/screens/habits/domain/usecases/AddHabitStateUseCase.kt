package com.horizondev.habitbloom.screens.habits.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.AddHabitDraft
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCreationData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * UseCase for managing the state of habit creation flow.
 * Provides reactive state management using StateFlows and methods to update the draft.
 */
class AddHabitStateUseCase {

    private val _draft = MutableStateFlow(AddHabitDraft.createDefault())
    val draft: StateFlow<AddHabitDraft> = _draft.asStateFlow()

    /**
     * Updates the habit category.
     */
    fun updateHabitCategory(habitCategory: HabitCategoryData) {
        _draft.value = _draft.value.copy(habitCategory = habitCategory)
    }

    /**
     * Updates the time of day for the habit.
     */
    fun updateTimeOfDay(timeOfDay: TimeOfDay) {
        _draft.value = _draft.value.copy(timeOfDay = timeOfDay)
    }

    /**
     * Updates the habit information.
     */
    fun updateHabitInfo(habitInfo: HabitInfo) {
        _draft.value = _draft.value.copy(habitInfo = habitInfo)
    }

    /**
     * Updates the duration and date information.
     */
    fun updateDuration(
        startDate: LocalDate,
        endDate: LocalDate,
        selectedDays: List<DayOfWeek>,
        durationInDays: Int,
        reminderEnabled: Boolean,
        reminderTime: LocalTime
    ) {
        _draft.value = _draft.value.copy(
            startDate = startDate,
            endDate = endDate,
            selectedDays = selectedDays,
            durationInDays = durationInDays,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
    }

    /**
     * Updates only the selected days.
     */
    fun updateSelectedDays(selectedDays: List<DayOfWeek>) {
        _draft.value = _draft.value.copy(selectedDays = selectedDays)
    }

    /**
     * Updates only the date range.
     */
    fun updateDateRange(startDate: LocalDate, endDate: LocalDate) {
        _draft.value = _draft.value.copy(startDate = startDate, endDate = endDate)
    }

    /**
     * Updates only the reminder settings.
     */
    fun updateReminder(reminderEnabled: Boolean, reminderTime: LocalTime) {
        _draft.value = _draft.value.copy(
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
    }

    /**
     * Resets the draft to default state.
     */
    fun resetDraft() {
        _draft.value = AddHabitDraft.createDefault()
    }

    /**
     * Validates if the current draft has all required information to create a habit.
     */
    fun isDraftValid(): Boolean {
        val currentDraft = _draft.value
        return currentDraft.habitCategory != null &&
                currentDraft.habitInfo != null &&
                currentDraft.timeOfDay != null
    }

    /**
     * Converts the current draft to HabitCreationData if valid.
     * Returns null if the draft is not complete.
     */
    fun getHabitCreationData(): HabitCreationData? {
        val currentDraft = _draft.value
        return if (isDraftValid()) {
            HabitCreationData(
                habitCategory = currentDraft.habitCategory!!,
                habitInfo = currentDraft.habitInfo!!,
                timeOfDay = currentDraft.timeOfDay!!,
                startDate = currentDraft.startDate,
                endDate = currentDraft.endDate,
                selectedDays = currentDraft.selectedDays,
                reminderEnabled = currentDraft.reminderEnabled,
                reminderTime = currentDraft.reminderTime
            )
        } else {
            null
        }
    }

    /**
     * Gets the current draft state.
     */
    fun getCurrentDraft(): AddHabitDraft = _draft.value
}
