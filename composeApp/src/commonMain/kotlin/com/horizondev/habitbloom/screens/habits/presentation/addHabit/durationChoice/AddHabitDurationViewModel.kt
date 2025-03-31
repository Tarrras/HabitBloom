package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.models.GroupOfDays
import com.horizondev.habitbloom.utils.formatToMmDdYyWithLocale
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.notification_permission_denied
import habitbloom.composeapp.generated.resources.notifications_required
import habitbloom.composeapp.generated.resources.the_habit_cannot_start_on_past_days
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

/**
 * ViewModel for the duration choice step in the Add Habit flow.
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
            val firstDateFormatted = firstDay?.formatToMmDdYyWithLocale()

            if (firstDay != null) {
                updateState {
                    it.copy(
                        startDate = firstDay,
                        formattedStartDate = firstDateFormatted
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
                val newList = when (event.group) {
                    GroupOfDays.EVERY_DAY -> DayOfWeek.entries
                    GroupOfDays.WEEKENDS -> listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                    GroupOfDays.WORK_DAYS -> listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY
                    )
                }
                updateState { it.copy(activeDays = newList, selectedGroupOfDays = event.group) }
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

                // Determine if this matches any predefined group
                val newSelectedGroup = when {
                    newList.containsAll(DayOfWeek.entries) && newList.size == DayOfWeek.entries.size ->
                        GroupOfDays.EVERY_DAY

                    newList.containsAll(listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) &&
                            newList.size == 2 ->
                        GroupOfDays.WEEKENDS

                    newList.containsAll(
                        listOf(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY
                        )
                    ) && newList.size == 5 ->
                        GroupOfDays.WORK_DAYS

                    else ->
                        null
                }

                updateState {
                    it.copy(
                        activeDays = newList,
                        selectedGroupOfDays = newSelectedGroup
                    )
                }
            }

            is AddHabitDurationUiEvent.DurationChanged -> {
                updateState { it.copy(durationInDays = event.duration) }
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
                } else {
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
                            durationInDays = currentState.durationInDays,
                            weekStartOption = currentState.weekStartOption,
                            startDate = calculatedStartedDate,
                            reminderEnabled = currentState.reminderEnabled,
                            reminderTime = currentState.reminderTime
                        )
                    )
                }
            }

            is AddHabitDurationUiEvent.SelectWeekStartOption -> {
                updateState { it.copy(weekStartOption = event.option) }
            }
        }
    }

    /**
     * Helper method to get string resources.
     */
    private suspend fun getString(resource: org.jetbrains.compose.resources.StringResource): String {
        return org.jetbrains.compose.resources.getString(resource)
    }
}