package com.horizondev.habitbloom.habits.presentation.addHabit.summary

import androidx.compose.material3.SnackbarDuration
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import com.horizondev.habitbloom.habits.domain.models.NewUserHabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddHabitSummaryScreenModel(
    private val habitsRepository: HabitsRepository,
    private val newUserHabitInfo: NewUserHabitInfo
) : StateScreenModel<AddHabitSummaryUiState>(
    AddHabitSummaryUiState(
        timeOfDay = newUserHabitInfo.timeOfDay ?: TimeOfDay.Morning,
        habitInfo = newUserHabitInfo.habitInfo ?: throw IllegalStateException("Habit info is null"),
        days = newUserHabitInfo.days ?: emptyList(),
        duration = newUserHabitInfo.duration ?: 1,
        startDate = newUserHabitInfo.startDate ?: getCurrentDate()
    )
) {

    private val _uiIntent = MutableSharedFlow<AddHabitSummaryUiIntent>()
    val uiIntent = _uiIntent.asSharedFlow()

    fun handleUiEvent(event: AddHabitSummaryUiEvent) = screenModelScope.launch {
        when (event) {
            AddHabitSummaryUiEvent.BackPressed -> {
                _uiIntent.emit(AddHabitSummaryUiIntent.NavigateBack)
            }

            AddHabitSummaryUiEvent.Confirm -> {
                mutableState.update { it.copy(isLoading = true) }
                habitsRepository.addHabit(
                    habitInfo = newUserHabitInfo.habitInfo ?: return@launch,
                    startDate = newUserHabitInfo.startDate ?: return@launch,
                    repeats = newUserHabitInfo.duration ?: return@launch,
                    days = newUserHabitInfo.days ?: return@launch
                ).onSuccess {
                    mutableState.update { it.copy(isLoading = false) }
                    _uiIntent.emit(AddHabitSummaryUiIntent.NavigateToSuccess)
                }.onFailure { throwable ->
                    mutableState.update { it.copy(isLoading = false) }
                    _uiIntent.emit(
                        AddHabitSummaryUiIntent.ShowSnackBar(
                            visuals = BloomSnackbarVisuals(
                                message = throwable.message.orEmpty(),
                                actionLabel = null,
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