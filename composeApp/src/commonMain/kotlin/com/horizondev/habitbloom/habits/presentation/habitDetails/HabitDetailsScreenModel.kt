package com.horizondev.habitbloom.habits.presentation.habitDetails

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HabitDetailsScreenModel(
    private val repository: HabitsRepository,
    private val userHabitId: Long
) : StateScreenModel<HabitScreenDetailsUiState>(HabitScreenDetailsUiState()) {

    private val _uiIntent = MutableSharedFlow<HabitScreenDetailsUiIntent>()
    val uiIntent = _uiIntent.asSharedFlow()

    private val habitDetailsFlow = repository.getUserHabitWithAllRecordsFlow(
        userHabitId = userHabitId
    ).onStart {
        mutableState.update { it.copy(isLoading = true) }
    }.catch {
        mutableState.update { it.copy(isLoading = false) }
    }.onEach { userHabitFullInfo ->
        mutableState.update { it.copy(habitInfo = userHabitFullInfo) }
    }.launchIn(screenModelScope)

    fun handleUiEvent(event: HabitScreenDetailsUiEvent) {
        when (event) {
            HabitScreenDetailsUiEvent.BackPressed -> {
                screenModelScope.launch {
                    _uiIntent.emit(HabitScreenDetailsUiIntent.NavigateBack)
                }
            }
        }
    }
}