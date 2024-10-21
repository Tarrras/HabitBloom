package com.horizondev.habitbloom.habits.presentation.habitDetails

import androidx.compose.material3.SnackbarDuration
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getFirstDateAfterStartDateOrNextWeek
import com.horizondev.habitbloom.utils.getLongestCompletionStreak
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.update_habit_error
import habitbloom.composeapp.generated.resources.update_habit_success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class HabitDetailsScreenModel(
    private val repository: HabitsRepository,
    private val userHabitId: Long
) : StateScreenModel<HabitScreenDetailsUiState>(HabitScreenDetailsUiState()) {

    private val _uiIntent = MutableSharedFlow<HabitScreenDetailsUiIntent>()
    val uiIntent = _uiIntent.asSharedFlow()

    private val habitDetailsFlow = repository.getUserHabitWithAllRecordsFlow(
        userHabitId = userHabitId
    ).catch {
        mutableState.update { it.copy(isLoading = false) }
    }.onEach { userHabitFullInfo ->
        mutableState.update {
            it.copy(
                habitInfo = userHabitFullInfo,
                isLoading = false,
                habitRepeats = userHabitFullInfo?.repeats ?: 0,
                habitDays = userHabitFullInfo?.days ?: emptyList(),
                habitDurationEditMode = false,
                habitDurationEditEnabled = (userHabitFullInfo?.completedRepeats ?: 12) < 12,
                progressUiState = userHabitFullInfo?.let { info -> calculateHabitProgress(info) }
            )
        }
    }.launchIn(screenModelScope)

    fun handleUiEvent(event: HabitScreenDetailsUiEvent) {
        when (event) {
            HabitScreenDetailsUiEvent.BackPressed -> {
                screenModelScope.launch {
                    _uiIntent.emit(HabitScreenDetailsUiIntent.NavigateBack)
                }
            }

            is HabitScreenDetailsUiEvent.DayStateChanged -> {
                val dayToChange = event.dayOfWeek
                mutableState.update {
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

            is HabitScreenDetailsUiEvent.DurationChanged -> mutableState.update {
                it.copy(
                    habitRepeats = event.duration,
                )
            }

            HabitScreenDetailsUiEvent.DurationEditModeChanged -> {
                mutableState.update {
                    it.copy(
                        habitDurationEditMode = it.habitDurationEditMode.not(),
                        habitRepeats = it.habitInfo?.repeats ?: 0,
                        habitDays = it.habitInfo?.days ?: emptyList()
                    )
                }
            }

            HabitScreenDetailsUiEvent.UpdateHabitDuration -> {
                screenModelScope.launch {
                    val uiState = mutableState.value

                    val habitOriginalInfo = uiState.habitInfo ?: return@launch
                    val habitNewRepeats = uiState.habitRepeats
                    val habitNewDays = uiState.habitDays

                    if (habitNewRepeats == habitOriginalInfo.repeats
                        && habitNewDays == habitOriginalInfo.days
                    ) {
                        mutableState.update { it.copy(habitDurationEditMode = false) }
                        return@launch
                    }

                    val startDateOfWithNewFlow = getFirstDateAfterStartDateOrNextWeek(
                        startDate = getCurrentDate(),
                        daysList = uiState.habitDays
                    )

                    if (startDateOfWithNewFlow == null) {
                        _uiIntent.emit(
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
                        _uiIntent.emit(
                            HabitScreenDetailsUiIntent.ShowSnackbar(
                                visuals = BloomSnackbarVisuals(
                                    message = getString(Res.string.update_habit_success),
                                    state = BloomSnackbarState.Success,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            )
                        )
                        mutableState.update { it.copy(habitDurationEditMode = false) }
                    }.onFailure {
                        _uiIntent.emit(
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
                screenModelScope.launch {
                    repository.deleteUserHabit(userHabitId = userHabitId).onSuccess {
                        _uiIntent.emit(HabitScreenDetailsUiIntent.NavigateBack)
                    }.onFailure {
                        _uiIntent.emit(
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
                mutableState.update {
                    it.copy(
                        showDeleteDialog = false
                    )
                }
            }

            HabitScreenDetailsUiEvent.RequestDeleteHabit -> {
                mutableState.update { it.copy(showDeleteDialog = true) }
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