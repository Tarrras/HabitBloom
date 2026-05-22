package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.time.TimeFormatUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail
import com.horizondev.habitbloom.screens.garden.domain.calculateLevelProgress
import com.horizondev.habitbloom.screens.garden.domain.levelToGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel for the Habit Flower Detail screen.
 * Handles data loading and UI state updates.
 */
class HabitFlowerDetailViewModel(
    private val habitId: Long,
    private val repository: HabitsRepository,
    private val themeUseCase: ThemeUseCase,
    private val timeFormatUseCase: TimeFormatUseCase
) : BloomViewModel<HabitFlowerDetailUiState, HabitFlowerDetailUiIntent>(
    HabitFlowerDetailUiState(
        isLoading = true,
        themeOption = themeUseCase.getThemeMode(),
        use24HourFormat = timeFormatUseCase.uses24HourTimeFormat()
    )
) {
    private val TAG = "HabitFlowerDetailVM"

    init {
        timeFormatUseCase.timeFormatFlow.map { option ->
            timeFormatUseCase.uses24HourTimeFormat(option)
        }.onEach { use24HourFormat ->
            updateState { it.copy(use24HourFormat = use24HourFormat) }
        }.launchIn(viewModelScope)
        loadHabitFlowerDetails()
    }

    private fun loadHabitFlowerDetails() {
        repository.getUserHabitWithAllRecordsFlow(habitId)
            .map { habitInfo ->
                if (habitInfo == null) {
                    throw IllegalStateException("Habit not found")
                }

                val today = getCurrentDate()
                val lastSevenScheduledDays = habitInfo.records
                    .filter { it.date <= today }
                    .sortedByDescending { it.date }
                    .take(7)
                    .map { record ->
                        HabitFlowerDetail.DailyCompletion(
                            date = record.date,
                            isCompleted = record.isCompleted
                        )
                    }
                    .reversed()

                val progressRecords = habitInfo.records.filter { it.date <= today }
                val levelProgress = calculateLevelProgress(
                    records = progressRecords,
                    daysPerWeek = habitInfo.days.size
                )

                val growthStage = levelToGrowthStage(levelProgress.level)
                val flowerType = FlowerType.fromTimeOfDay(habitInfo.timeOfDay)

                val flowerHealth = FlowerHealth(
                    value = levelProgress.vitality,
                    consecutiveMissedDays = levelProgress.currentMissedDays
                )

                HabitFlowerDetail(
                    habitId = habitInfo.userHabitId,
                    name = habitInfo.name,
                    description = habitInfo.description,
                    iconUrl = habitInfo.iconUrl,
                    timeOfDay = habitInfo.timeOfDay,
                    startDate = habitInfo.startDate,
                    endDate = habitInfo.endDate,
                    reminderTime = habitInfo.reminderTime.takeIf { habitInfo.reminderEnabled },
                    lastSevenDaysCompletions = lastSevenScheduledDays,
                    flowerGrowthStage = growthStage,
                    flowerType = flowerType,
                    flowerHealth = flowerHealth,
                    level = levelProgress.level,
                    totalXp = levelProgress.totalXp,
                    xpInLevel = levelProgress.xpInLevel,
                    xpForCurrentLevel = levelProgress.xpForCurrentLevel,
                    xpToNextLevel = levelProgress.xpToNextLevel
                )
            }
            .onEach { habitFlowerDetail ->
                updateState { currentState ->
                    currentState.copy(
                        isLoading = false,
                        habitFlowerDetail = habitFlowerDetail,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                Napier.e("Error loading habit flower details", error, tag = TAG)
                updateState { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Failed to load habit details: ${error.message}"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun handleUiEvent(event: HabitFlowerDetailUiEvent) {
        when (event) {
            is HabitFlowerDetailUiEvent.NavigateToHabitDetails -> {
                emitUiIntent(HabitFlowerDetailUiIntent.NavigateToHabitDetails(event.habitId))
            }

            is HabitFlowerDetailUiEvent.NavigateBack -> {
                emitUiIntent(HabitFlowerDetailUiIntent.NavigateBack)
            }
        }
    }
} 
