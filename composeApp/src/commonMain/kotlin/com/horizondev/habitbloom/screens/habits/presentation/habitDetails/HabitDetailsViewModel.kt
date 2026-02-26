package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.screens.habits.domain.usecases.EnableNotificationsForReminderUseCase
import com.horizondev.habitbloom.utils.formatTime
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.failed_to_clear_history
import habitbloom.composeapp.generated.resources.failed_to_update_reminder_settings
import habitbloom.composeapp.generated.resources.history_cleared_past_records_deleted
import habitbloom.composeapp.generated.resources.notification_permission_denied
import habitbloom.composeapp.generated.resources.reminder_disabled
import habitbloom.composeapp.generated.resources.reminder_set_for
import habitbloom.composeapp.generated.resources.update_habit_error
import habitbloom.composeapp.generated.resources.update_habit_success
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HabitDetailsViewModel(
    private val repository: HabitsRepository,
    private val userHabitId: Long
) : BloomViewModel<HabitScreenDetailsUiState, HabitScreenDetailsUiIntent>(
    HabitScreenDetailsUiState()
), KoinComponent {

    private val enableNotificationsUseCase: EnableNotificationsForReminderUseCase by inject()

    private val habitDetailsFlow = repository.getUserHabitWithAllRecordsFlow(
        userHabitId = userHabitId
    ).catch {
        updateState { it.copy(isLoading = false) }
    }.onEach { userHabitFullInfo ->
        updateState {
            it.copy(
                habitInfo = userHabitFullInfo,
                isLoading = false,
                habitDays = userHabitFullInfo?.days ?: emptyList(),
                startDate = userHabitFullInfo?.startDate,
                endDate = userHabitFullInfo?.endDate ?: userHabitFullInfo?.startDate,
                habitDurationEditMode = false,
                habitDurationEditEnabled = true,
                reminderEnabled = userHabitFullInfo?.reminderEnabled ?: false,
                reminderTime = userHabitFullInfo?.reminderTime ?: LocalTime(8, 0),
                progressUiState = userHabitFullInfo?.let { info -> calculateHabitProgress(info) }
            )
        }
    }.launchIn(viewModelScope)

    fun handleUiEvent(event: HabitScreenDetailsUiEvent) {
        when (event) {
            HabitScreenDetailsUiEvent.BackPressed -> {
                viewModelScope.launch {
                    emitUiIntent(HabitScreenDetailsUiIntent.NavigateBack)
                }
            }

            is HabitScreenDetailsUiEvent.DayStateChanged -> {
                val dayToChange = event.dayOfWeek
                updateState {
                    val updatedList = it.habitDays.toMutableList().apply {
                        if (contains(dayToChange)) {
                            remove(dayToChange)
                        } else add(dayToChange)
                    }.sortedBy { item -> item.ordinal }.toList()
                    val startDateOfWithNewFlow = getFirstDateAfterStartDateOrNextWeek(
                        startDate = getCurrentDate(),
                        daysList = updatedList
                    )

                    it.copy(
                        habitDays = updatedList,
                        durationUpdateButtonEnabled = startDateOfWithNewFlow != null
                    )
                }
            }

            HabitScreenDetailsUiEvent.DurationEditModeChanged -> {
                updateState(::reduceDurationEditModeChanged)
            }

            HabitScreenDetailsUiEvent.UpdateHabitDuration -> {
                viewModelScope.launch {
                    val uiState = state.value

                    when (val validation = validateHabitDurationUpdate(uiState)) {
                        HabitDurationUpdateValidation.MissingData -> return@launch
                        HabitDurationUpdateValidation.NoChanges -> {
                            updateState(::reduceDurationUpdatedSuccessfully)
                            return@launch
                        }

                        HabitDurationUpdateValidation.InvalidDays -> {
                            emitUiIntent(
                                HabitScreenDetailsUiIntent.ShowSnackbar(
                                    visuals = BloomSnackbarVisuals(
                                        message = getString(Res.string.update_habit_error),
                                        state = BloomSnackbarState.Error,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                )
                            )
                            return@launch
                        }

                        is HabitDurationUpdateValidation.Ready -> {
                            repository.updateExistingHabit(
                                userHabitId = validation.payload.userHabitId,
                                endDate = validation.payload.endDate,
                                days = validation.payload.days
                            ).onSuccess {
                                emitUiIntent(
                                    HabitScreenDetailsUiIntent.ShowSnackbar(
                                        visuals = BloomSnackbarVisuals(
                                            message = getString(Res.string.update_habit_success),
                                            state = BloomSnackbarState.Success,
                                            duration = SnackbarDuration.Short,
                                            withDismissAction = true
                                        )
                                    )
                                )
                                updateState(::reduceDurationUpdatedSuccessfully)
                            }.onFailure {
                                emitUiIntent(
                                    HabitScreenDetailsUiIntent.ShowSnackbar(
                                        visuals = BloomSnackbarVisuals(
                                            message = getString(Res.string.update_habit_error),
                                            state = BloomSnackbarState.Error,
                                            duration = SnackbarDuration.Short,
                                            withDismissAction = true
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }

            HabitScreenDetailsUiEvent.DeleteHabit -> {
                viewModelScope.launch {
                    repository.deleteUserHabit(userHabitId = userHabitId).onSuccess {
                        updateState { it.copy(showDeleteDialog = false) }
                        emitUiIntent(HabitScreenDetailsUiIntent.NavigateBack)
                    }.onFailure {
                        updateState { it.copy(showDeleteDialog = false) }
                        emitUiIntent(
                            HabitScreenDetailsUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = getString(Res.string.update_habit_error),
                                    state = BloomSnackbarState.Error,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }
                }
            }

            HabitScreenDetailsUiEvent.DismissHabitDeletion -> {
                updateState {
                    it.copy(
                        showDeleteDialog = false
                    )
                }
            }

            HabitScreenDetailsUiEvent.RequestDeleteHabit -> {
                updateState { it.copy(showDeleteDialog = true) }
            }

            HabitScreenDetailsUiEvent.RequestClearHistory -> {
                updateState { it.copy(showClearHistoryDialog = true) }
            }

            HabitScreenDetailsUiEvent.DismissClearHistory -> {
                updateState { it.copy(showClearHistoryDialog = false) }
            }

            HabitScreenDetailsUiEvent.ClearHistory -> {
                viewModelScope.launch {
                    updateState { it.copy(showClearHistoryDialog = false) }

                    repository.clearPastRecords(userHabitId = userHabitId).onSuccess { count ->
                        emitUiIntent(
                            HabitScreenDetailsUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = getString(
                                        Res.string.history_cleared_past_records_deleted,
                                        count.toString()
                                    ),
                                    state = BloomSnackbarState.Success,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }.onFailure {
                        emitUiIntent(
                            HabitScreenDetailsUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = getString(
                                        Res.string.failed_to_clear_history
                                    ),
                                    state = BloomSnackbarState.Error,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }
                }
            }

            // New reminder-related event handlers
            HabitScreenDetailsUiEvent.ShowReminderDialog -> {
                updateState { it.copy(showReminderDialog = true) }
            }

            HabitScreenDetailsUiEvent.DismissReminderDialog -> {
                updateState { it.copy(showReminderDialog = false) }
            }

            is HabitScreenDetailsUiEvent.ReminderTimeChanged -> {
                updateState { it.copy(reminderTime = event.time) }
            }

            is HabitScreenDetailsUiEvent.ReminderEnabledChanged -> {
                updateState { it.copy(reminderEnabled = event.enabled) }
            }

            HabitScreenDetailsUiEvent.SaveReminderSettings -> {
                viewModelScope.launch {
                    val currentState = state.value
                    val habitId = currentState.habitInfo?.userHabitId ?: return@launch
                    val enabled = currentState.reminderEnabled
                    val time = if (enabled) currentState.reminderTime else null

                    // If enabling reminder, try to enable notifications if needed
                    if (enabled && !currentState.habitInfo.reminderEnabled) {
                        enableNotificationsUseCase.execute()
                    }

                    repository.updateHabitReminder(
                        habitId = habitId,
                        enabled = enabled,
                        reminderTime = time
                    ).onSuccess { success ->
                        if (success) {
                            updateState { it.copy(showReminderDialog = false) }
                            emitUiIntent(
                                HabitScreenDetailsUiIntent.ShowSnackbar(
                                    visuals = BloomSnackbarVisuals(
                                        message = if (enabled) getString(
                                            Res.string.reminder_set_for,
                                            formatTime(time!!, use24HourFormat = true)
                                        ) else getString(
                                            Res.string.reminder_disabled
                                        ),
                                        state = BloomSnackbarState.Success,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                )
                            )
                        } else {
                            emitUiIntent(
                                HabitScreenDetailsUiIntent.ShowSnackbar(
                                    visuals = BloomSnackbarVisuals(
                                        message = getString(Res.string.notification_permission_denied),
                                        state = BloomSnackbarState.Error,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                )
                            )
                        }
                    }.onFailure {
                        emitUiIntent(
                            HabitScreenDetailsUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = getString(
                                        Res.string.failed_to_update_reminder_settings
                                    ),
                                    state = BloomSnackbarState.Error,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                    }
                }
            }

            // End date dialog events
            HabitScreenDetailsUiEvent.ShowEndDatePickerDialog -> {
                updateState { it.copy(showEndDatePickerDialog = true) }
            }

            HabitScreenDetailsUiEvent.DismissEndDatePickerDialog -> {
                updateState { it.copy(showEndDatePickerDialog = false) }
            }

            is HabitScreenDetailsUiEvent.EndDateChanged -> {
                updateState { reduceEndDateChanged(it, event.endDate) }
            }
        }
    }

    private fun calculateHabitProgress(
        info: UserHabitFullInfo
    ): UserHabitProgressUiState {
        val currentStreak = info.daysStreak
        val habitRecordsBeforeUntilToday = info.records
            .sortedBy {
                it.date
            }.filter {
                it.date <= getCurrentDate()
            }

        // Longest run of consecutive completed scheduled records (up to today)
        var bestStreak = 0
        var run = 0
        for (record in habitRecordsBeforeUntilToday) {
            if (record.isCompleted) {
                run += 1
                if (run > bestStreak) bestStreak = run
            } else {
                run = 0
            }
        }

        val totalDone = habitRecordsBeforeUntilToday.count { it.isCompleted }
        val overallRate = habitRecordsBeforeUntilToday.let {
            totalDone.toFloat() / it.count().toFloat()
        } * 100f

        return UserHabitProgressUiState(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            totalDone = totalDone,
            overallRate = overallRate
        )
    }
}

internal data class HabitDurationUpdatePayload(
    val userHabitId: Long,
    val endDate: kotlinx.datetime.LocalDate,
    val days: List<kotlinx.datetime.DayOfWeek>
)

internal sealed interface HabitDurationUpdateValidation {
    data object MissingData : HabitDurationUpdateValidation
    data object NoChanges : HabitDurationUpdateValidation
    data object InvalidDays : HabitDurationUpdateValidation
    data class Ready(val payload: HabitDurationUpdatePayload) : HabitDurationUpdateValidation
}

internal fun reduceDurationEditModeChanged(
    state: HabitScreenDetailsUiState
): HabitScreenDetailsUiState {
    return state.copy(
        habitDurationEditMode = state.habitDurationEditMode.not(),
        habitDays = state.habitInfo?.days ?: emptyList(),
        startDate = state.habitInfo?.startDate,
        endDate = state.habitInfo?.endDate ?: state.habitInfo?.startDate
    )
}

internal fun reduceEndDateChanged(
    state: HabitScreenDetailsUiState,
    endDate: kotlinx.datetime.LocalDate
): HabitScreenDetailsUiState {
    return state.copy(
        endDate = endDate,
        showEndDatePickerDialog = false,
        durationUpdateButtonEnabled = true
    )
}

internal fun reduceDurationUpdatedSuccessfully(
    state: HabitScreenDetailsUiState
): HabitScreenDetailsUiState = state.copy(habitDurationEditMode = false)

internal fun validateHabitDurationUpdate(
    state: HabitScreenDetailsUiState
): HabitDurationUpdateValidation {
    val habitInfo = state.habitInfo ?: return HabitDurationUpdateValidation.MissingData
    val startDate = state.startDate ?: return HabitDurationUpdateValidation.MissingData
    val endDate = state.endDate ?: return HabitDurationUpdateValidation.MissingData

    if (startDate == habitInfo.startDate &&
        endDate == habitInfo.endDate &&
        state.habitDays == habitInfo.days
    ) {
        return HabitDurationUpdateValidation.NoChanges
    }

    val startDateOfWithNewFlow = getFirstDateAfterStartDateOrNextWeek(
        startDate = getCurrentDate(),
        daysList = state.habitDays
    )

    if (startDateOfWithNewFlow == null) {
        return HabitDurationUpdateValidation.InvalidDays
    }

    return HabitDurationUpdateValidation.Ready(
        payload = HabitDurationUpdatePayload(
            userHabitId = habitInfo.userHabitId,
            endDate = endDate,
            days = state.habitDays
        )
    )
}
