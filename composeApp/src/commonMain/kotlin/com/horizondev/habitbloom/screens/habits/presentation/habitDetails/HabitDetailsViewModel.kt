package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.pickers.formatTime
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import com.horizondev.habitbloom.utils.getLongestCompletionStreak
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

class HabitDetailsViewModel(
    private val repository: HabitsRepository,
    private val userHabitId: Long
) : BloomViewModel<HabitScreenDetailsUiState, HabitScreenDetailsUiIntent>(
    HabitScreenDetailsUiState()
) {

    private val habitDetailsFlow = repository.getUserHabitWithAllRecordsFlow(
        userHabitId = userHabitId
    ).catch {
        updateState { it.copy(isLoading = false) }
    }.onEach { userHabitFullInfo ->
        updateState {
            it.copy(
                habitInfo = userHabitFullInfo,
                isLoading = false,
                habitRepeats = userHabitFullInfo?.repeats ?: 0,
                habitDays = userHabitFullInfo?.days ?: emptyList(),
                habitDurationEditMode = false,
                habitDurationEditEnabled = (userHabitFullInfo?.completedRepeats ?: 12) < 12,
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

            is HabitScreenDetailsUiEvent.DurationChanged -> updateState {
                it.copy(
                    habitRepeats = event.duration,
                )
            }

            HabitScreenDetailsUiEvent.DurationEditModeChanged -> {
                updateState {
                    it.copy(
                        habitDurationEditMode = it.habitDurationEditMode.not(),
                        habitRepeats = it.habitInfo?.repeats ?: 0,
                        habitDays = it.habitInfo?.days ?: emptyList()
                    )
                }
            }

            HabitScreenDetailsUiEvent.UpdateHabitDuration -> {
                viewModelScope.launch {
                    val uiState = state.value

                    val habitOriginalInfo = uiState.habitInfo ?: return@launch
                    val habitNewRepeats = uiState.habitRepeats
                    val habitNewDays = uiState.habitDays

                    if (habitNewRepeats == habitOriginalInfo.repeats
                        && habitNewDays == habitOriginalInfo.days
                    ) {
                        updateState { it.copy(habitDurationEditMode = false) }
                        return@launch
                    }

                    val startDateOfWithNewFlow = getFirstDateAfterStartDateOrNextWeek(
                        startDate = getCurrentDate(),
                        daysList = uiState.habitDays
                    )

                    if (startDateOfWithNewFlow == null) {
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

                    val repeatsToChangeRecords =
                        (habitNewRepeats - habitOriginalInfo.completedRepeats).coerceIn(
                            minimumValue = 1,
                            maximumValue = 12
                        )

                    repository.updateExistingHabit(
                        userHabitId = uiState.habitInfo.userHabitId,
                        allRepeats = habitNewRepeats,
                        days = habitNewDays,
                        repeatsToChangeRecords = repeatsToChangeRecords
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
                        updateState { it.copy(habitDurationEditMode = false) }
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
                                            formatTime(time!!, use24HourFormat = false)
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
        }
    }

    private fun calculateHabitProgress(
        info: UserHabitFullInfo
    ): UserHabitProgressUiState {
        val currentStreak = info.daysStreak
        val bestStreak = info.records.getLongestCompletionStreak()
        val habitRecordsBeforeUntilToday = info.records
            .sortedBy {
                it.date
            }.filter {
                it.date <= getCurrentDate()
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