package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.utils.formatToMmDdYyWithLocaleSuspend
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.duration_too_long
import habitbloom.composeapp.generated.resources.notification_permission_denied
import habitbloom.composeapp.generated.resources.notifications_required
import habitbloom.composeapp.generated.resources.the_habit_cannot_start_on_past_days
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * ViewModel for the duration choice step in the Add Habit flow.
 * Now focused on date range selection rather than explicit duration.
 */
class AddHabitDurationViewModel(
    private val permissionsManager: PermissionsManager
) : BloomViewModel<AddHabitDurationUiState, AddHabitDurationUiIntent>(
    initialState = AddHabitDurationUiState(
        activeDays = DayOfWeek.entries,
    )
) {

    init {
        // Setup derived state for start date
        combine(
            state.map { it.activeDays },
            state.map { it.weekStartOption }
        ) { activeDays, startOption ->
            val firstDay = getFirstDateAfterStartDateOrNextWeek(
                daysList = activeDays,
                startOption = startOption
            )
            val firstDateFormatted = firstDay?.formatToMmDdYyWithLocaleSuspend()

            if (firstDay != null) {
                // Default end date is now 7 days ahead (for a weekly habit)
                val endDate = calculateEndDate(firstDay, 7)
                updateState {
                    it.copy(
                        startDate = firstDay,
                        formattedStartDate = firstDateFormatted,
                        endDate = endDate,
                        // Calculate duration in days for backward compatibility
                        durationInDays = 1
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Single entry point for all UI events.
     */
    fun handleUiEvent(event: AddHabitDurationUiEvent) {
        when (event) {
            is AddHabitDurationUiEvent.SelectGroupOfDays -> {
                val newList = event.group
                updateState { it.copy(activeDays = newList) }
            }

            is AddHabitDurationUiEvent.UpdateDayState -> {
                val currentState = state.value
                val dayToChange = event.dayOfWeek

                val currentActiveDays = currentState.activeDays

                val newList = currentActiveDays.toMutableList().apply {
                    if (contains(dayToChange)) {
                        remove(dayToChange)
                    } else add(dayToChange)
                }

                updateState {
                    it.copy(
                        activeDays = newList,
                    )
                }
            }

            is AddHabitDurationUiEvent.SelectPresetDateRange -> {
                val currentState = state.value
                val startDate = currentState.startDate ?: return

                // Calculate new end date based on preset days
                val daysAhead = event.daysAhead.coerceAtMost(currentState.maxHabitDurationDays)
                val endDate = startDate.plus(daysAhead, DateTimeUnit.DAY)

                // Update state with new date range and calculated duration
                updateState {
                    val calculatedDuration = calculateDurationValue(startDate, endDate)
                    it.copy(
                        endDate = endDate,
                        durationInDays = calculatedDuration
                    )
                }
            }

            is AddHabitDurationUiEvent.ReminderEnabledChanged -> {
                viewModelScope.launch {
                    if (event.enabled) {
                        // Request notification permission when enabling reminders
                        val permissionGranted = permissionsManager.requestNotificationPermission()

                        if (permissionGranted) {
                            updateState { it.copy(reminderEnabled = true) }
                        } else {
                            // Show error if permission denied
                            emitUiIntent(
                                AddHabitDurationUiIntent.ShowValidationError(
                                    BloomSnackbarVisuals(
                                        message = getString(Res.string.notification_permission_denied),
                                        state = BloomSnackbarState.Error,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                )
                            )
                            // Ensure switch remains off
                            updateState { it.copy(reminderEnabled = false) }
                        }
                    } else {
                        // Simply disable reminders if turning off
                        updateState { it.copy(reminderEnabled = false) }
                    }
                }
            }

            is AddHabitDurationUiEvent.ReminderTimeChanged -> {
                updateState { it.copy(reminderTime = event.time) }
            }

            AddHabitDurationUiEvent.Cancel -> {
                emitUiIntent(AddHabitDurationUiIntent.NavigateBack)
            }

            AddHabitDurationUiEvent.OnNext -> viewModelScope.launch {
                val currentState = state.value

                if (currentState.startDate == null) {
                    emitUiIntent(
                        AddHabitDurationUiIntent.ShowValidationError(
                            BloomSnackbarVisuals(
                                message = getString(Res.string.the_habit_cannot_start_on_past_days),
                                state = BloomSnackbarState.Error,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        )
                    )
                    return@launch
                }

                if (currentState.endDate == null) {
                    // This shouldn't happen with our UI, but just in case
                    return@launch
                }

                val daysBetween = calculateDaysBetween(currentState.startDate, currentState.endDate)
                if (daysBetween > currentState.maxHabitDurationDays) {
                    emitUiIntent(
                        AddHabitDurationUiIntent.ShowValidationError(
                            BloomSnackbarVisuals(
                                message = getString(Res.string.duration_too_long),
                                state = BloomSnackbarState.Warning,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        )
                    )
                    return@launch
                }

                // Check if reminders are enabled but permissions are not granted
                if (currentState.reminderEnabled && !permissionsManager.hasNotificationPermission()) {
                    emitUiIntent(
                        AddHabitDurationUiIntent.ShowValidationError(
                            BloomSnackbarVisuals(
                                message = getString(Res.string.notifications_required),
                                state = BloomSnackbarState.Warning,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        )
                    )
                    // Disable reminders since permissions were not granted
                    updateState { it.copy(reminderEnabled = false) }
                    return@launch
                }

                val calculatedStartedDate = currentState.startDate
                val calculatedDuration = calculateDurationValue(
                    currentState.startDate,
                    currentState.endDate
                )

                emitUiIntent(
                    AddHabitDurationUiIntent.NavigateNext(
                        selectedDays = currentState.activeDays,
                        durationInDays = calculatedDuration,
                        weekStartOption = currentState.weekStartOption,
                        startDate = calculatedStartedDate,
                        reminderEnabled = currentState.reminderEnabled,
                        reminderTime = currentState.reminderTime
                    )
                )
            }

            is AddHabitDurationUiEvent.SelectWeekStartOption -> {
                updateState { it.copy(weekStartOption = event.option) }
            }

            is AddHabitDurationUiEvent.StartDateChanged -> {
                launch {
                    val formattedDate = event.date.formatToMmDdYyWithLocaleSuspend()

                    // When start date changes, adjust end date to maintain similar duration
                    val currentState = state.value
                    val oldEndDate = currentState.endDate
                    val oldStartDate = currentState.startDate

                    val newEndDate = if (oldEndDate != null && oldStartDate != null) {
                        // Maintain the same duration between dates
                        val daysBetween = calculateDaysBetween(oldStartDate, oldEndDate)
                        event.date.plus(daysBetween, DateTimeUnit.DAY)
                    } else {
                        // Default to 7 days if we don't have previous dates
                        event.date.plus(7, DateTimeUnit.DAY)
                    }

                    val calculatedDuration = calculateDurationValue(event.date, newEndDate)
                    
                    updateState {
                        it.copy(
                            startDate = event.date,
                            formattedStartDate = formattedDate,
                            endDate = newEndDate,
                            durationInDays = calculatedDuration
                        )
                    }
                }
            }

            is AddHabitDurationUiEvent.DateRangeChanged -> {
                viewModelScope.launch {
                    val startDate = event.startDate
                    val endDate = event.endDate ?: return@launch

                    // Enforce maximum duration
                    val daysBetween = calculateDaysBetween(startDate, endDate)
                    val maxDays = state.value.maxHabitDurationDays

                    val effectiveEndDate = if (daysBetween > maxDays) {
                        // Limit to max duration
                        startDate.plus(maxDays, DateTimeUnit.DAY)
                    } else {
                        endDate
                    }
                    
                    val formattedDate = startDate.formatToMmDdYyWithLocaleSuspend()
                    val calculatedDuration = calculateDurationValue(startDate, effectiveEndDate)
                    
                    updateState {
                        it.copy(
                            startDate = startDate,
                            endDate = effectiveEndDate,
                            formattedStartDate = formattedDate,
                            durationInDays = calculatedDuration
                        )
                    }

                    // Show warning if the date range was limited
                    if (effectiveEndDate != endDate) {
                        emitUiIntent(
                            AddHabitDurationUiIntent.ShowValidationError(
                                BloomSnackbarVisuals(
                                    message = getString(Res.string.duration_too_long),
                                    state = BloomSnackbarState.Warning,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }
                }
            }

            is AddHabitDurationUiEvent.SetDatePickerVisibility -> {
                updateState { it.copy(isDatePickerVisible = event.isVisible) }
            }
        }
    }

    /**
     * Calculate the equivalent duration value (in repeats) from a date range
     * This is for backward compatibility with the rest of the codebase
     */
    private fun calculateDurationValue(startDate: LocalDate, endDate: LocalDate): Int {
        val daysBetween = calculateDaysBetween(startDate, endDate)
        // Each repeat is approximately one week (7 days)
        // We calculate repeats by dividing by 7 and rounding up
        return ((daysBetween + 6) / 7).coerceIn(1, 12)
    }

    /**
     * Calculate the end date based on the start date and number of days
     */
    private fun calculateEndDate(startDate: LocalDate, daysAhead: Int): LocalDate {
        return startDate.plus(daysAhead, DateTimeUnit.DAY)
    }

    /**
     * Calculate days between two dates (inclusive)
     */
    private fun calculateDaysBetween(startDate: LocalDate, endDate: LocalDate): Int {
        if (startDate > endDate) return 0

        var current = startDate
        var days = 0

        while (current <= endDate) {
            days++
            current = current.plus(1, DateTimeUnit.DAY)
        }

        return days
    }

    /**
     * Helper method to get string resources.
     */
    private suspend fun getString(resource: org.jetbrains.compose.resources.StringResource): String {
        return org.jetbrains.compose.resources.getString(resource)
    }
}