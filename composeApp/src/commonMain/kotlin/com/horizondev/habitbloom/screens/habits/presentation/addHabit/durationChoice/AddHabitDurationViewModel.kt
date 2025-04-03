package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.utils.calculateEndOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import com.horizondev.habitbloom.utils.plusDays
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
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the duration choice step in the Add Habit flow.
 * Now focused on date range selection rather than explicit duration.
 */
class AddHabitDurationViewModel(
    private val permissionsManager: PermissionsManager
) : BloomViewModel<AddHabitDurationUiState, AddHabitDurationUiIntent>(
    initialState = AddHabitDurationUiState(
        activeDays = DayOfWeek.entries,
        startDate = getCurrentDate(),
        endDate = getCurrentDate().calculateEndOfWeek()
    )
) {

    init {
        // Setup derived state for start date
        combine(
            state.map { it.activeDays },
            state.map { it.startDate to it.endDate }
        ) { activeDays, currentDates ->
            val (currentStart, currentEnd) = currentDates

            // Only calculate if we have active days
            if (activeDays.isEmpty()) return@combine

            // Get the first available day based on selection
            val newFirstDay = getFirstDateAfterStartDateOrNextWeek(
                daysList = activeDays
            ) ?: return@combine

            // Determine if this is an initial setup or an update due to day selection change
            when {
                currentStart == null || currentEnd == null -> {
                    // Initial setup - default to end of week
                    val newEndDate = newFirstDay.calculateEndOfWeek()

                    updateState {
                        it.copy(
                            startDate = newFirstDay,
                            endDate = newEndDate,
                            durationInDays = calculateDurationValue(newFirstDay, newEndDate)
                        )
                    }
                }

                currentEnd != null &&
                        currentStart != newFirstDay -> {
                    // Start date changed due to day selection - keep the same end date
                    // This handles the case when a user deselects a day

                    updateState {
                        it.copy(
                            startDate = newFirstDay,
                            durationInDays = calculateDurationValue(newFirstDay, currentEnd)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Single entry point for all UI events.
     */
    fun handleUiEvent(event: AddHabitDurationUiEvent) {
        when (event) {
            is AddHabitDurationUiEvent.SelectGroupOfDays -> selectGroupOfDays(event.group)
            is AddHabitDurationUiEvent.UpdateDayState -> updateDayState(event.dayOfWeek)
            is AddHabitDurationUiEvent.SelectPresetDateRange -> selectPresetDateRange(event.daysAhead)
            is AddHabitDurationUiEvent.ReminderEnabledChanged -> handleReminderEnabledChanged(event.enabled)
            is AddHabitDurationUiEvent.ReminderTimeChanged -> handleReminderTimeChanged(event.time)
            is AddHabitDurationUiEvent.DateRangeChanged -> handleDateRangeChanged(
                event.startDate,
                event.endDate
            )

            is AddHabitDurationUiEvent.SetDatePickerVisibility -> setDatePickerVisibility(event.isVisible)
            AddHabitDurationUiEvent.Cancel -> handleCancel()
            AddHabitDurationUiEvent.OnNext -> handleNext()
        }
    }

    private fun selectGroupOfDays(days: List<DayOfWeek>) {
        updateState { it.copy(activeDays = days) }
    }

    private fun updateDayState(dayOfWeek: DayOfWeek) {
        val currentState = state.value
        val currentActiveDays = currentState.activeDays

        // Update active days list
        val newList = currentActiveDays.toMutableList().apply {
            if (contains(dayOfWeek)) {
                remove(dayOfWeek)
            } else {
                add(dayOfWeek)
            }
        }

        // Update the active days - the combine flow will handle date adjustments
        updateState {
            it.copy(activeDays = newList)
        }
    }

    private fun selectPresetDateRange(daysAhead: Int) {
        val currentState = state.value
        val startDate = currentState.startDate ?: return

        // Calculate new end date based on preset days
        val adjustedDaysAhead =
            daysAhead.coerceAtMost(currentState.maxHabitDurationDays) - 1 // Adjust to include start date
        val endDate = startDate.plusDays(adjustedDaysAhead.toLong())

        // Update state with new date range and calculated duration
        updateState {
            it.copy(
                endDate = endDate,
            )
        }
    }

    private fun handleReminderEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
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

    private fun handleReminderTimeChanged(time: LocalTime) {
        updateState { it.copy(reminderTime = time) }
    }

    private fun handleCancel() {
        emitUiIntent(AddHabitDurationUiIntent.NavigateBack)
    }

    private fun handleDateRangeChanged(startDate: LocalDate, endDate: LocalDate?) {
        viewModelScope.launch {
            if (endDate == null) return@launch

            // Enforce maximum duration
            val daysBetween = calculateDaysBetween(startDate, endDate)
            val maxDays = state.value.maxHabitDurationDays

            val effectiveEndDate = if (daysBetween > maxDays) {
                // Limit to max duration
                startDate.plus(maxDays, DateTimeUnit.DAY)
            } else {
                endDate
            }

            updateState {
                it.copy(
                    startDate = startDate,
                    endDate = effectiveEndDate,
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

    private fun setDatePickerVisibility(isVisible: Boolean) {
        updateState { it.copy(isDatePickerVisible = isVisible) }
    }

    private fun handleNext() {
        viewModelScope.launch {
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

            emitUiIntent(
                AddHabitDurationUiIntent.NavigateNext(
                    selectedDays = currentState.activeDays,
                    startDate = calculatedStartedDate,
                    reminderEnabled = currentState.reminderEnabled,
                    reminderTime = currentState.reminderTime,
                    durationInDays = calculateDurationValue(
                        currentState.startDate,
                        currentState.endDate
                    )
                )
            )
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
     * Calculate days between two dates (inclusive)
     */
    private fun calculateDaysBetween(startDate: LocalDate, endDate: LocalDate): Int {
        return startDate.daysUntil(endDate)
    }
}