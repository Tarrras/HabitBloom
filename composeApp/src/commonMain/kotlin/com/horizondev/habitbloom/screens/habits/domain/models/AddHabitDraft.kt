package com.horizondev.habitbloom.screens.habits.domain.models

import com.horizondev.habitbloom.utils.calculateEndOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Data class representing a draft habit being created during the add habit flow.
 * This contains all the information gathered across the different steps.
 */
data class AddHabitDraft(
    val habitCategory: HabitCategoryData? = null,
    val timeOfDay: TimeOfDay? = null,
    val habitInfo: HabitInfo? = null,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val selectedDays: List<DayOfWeek>,
    val durationInDays: Int,
    val reminderEnabled: Boolean,
    val reminderTime: LocalTime
) {
    companion object {
        fun createDefault(): AddHabitDraft {
            return AddHabitDraft(
                habitCategory = null,
                timeOfDay = null,
                habitInfo = null,
                startDate = getCurrentDate(),
                endDate = getCurrentDate().calculateEndOfWeek(),
                selectedDays = DayOfWeek.entries,
                durationInDays = 0,
                reminderEnabled = false,
                reminderTime = LocalTime(8, 0)
            )
        }
    }
}

/**
 * Data class representing the complete habit creation data needed to create a new habit.
 * Updated to include habit category information.
 */
data class HabitCreationData(
    val habitCategory: HabitCategoryData,
    val habitInfo: HabitInfo,
    val timeOfDay: TimeOfDay,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val selectedDays: List<DayOfWeek>,
    val reminderEnabled: Boolean,
    val reminderTime: LocalTime
)
