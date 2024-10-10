package com.horizondev.habitbloom.habits.presentation.habitDetails

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.horizondev.habitbloom.habits.domain.HabitsRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class HabitDetailsScreenModel(
    private val repository: HabitsRepository,
    private val userHabitId: Long
) : StateScreenModel<HabitScreenDetailsUiState>(HabitScreenDetailsUiState()) {

    private val habitDetailsFlow = repository.getUserHabitWithAllRecordsFlow(
        userHabitId = userHabitId
    ).onStart {
        mutableState.update { it.copy(isLoading = true) }
    }.catch {
        mutableState.update { it.copy(isLoading = false) }
    }.onEach { userHabitFullInfo ->
        mutableState.update { it.copy(habitInfo = userHabitFullInfo) }
    }.launchIn(screenModelScope)
}