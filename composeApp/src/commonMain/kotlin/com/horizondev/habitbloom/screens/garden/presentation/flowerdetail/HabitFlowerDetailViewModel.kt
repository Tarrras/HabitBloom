package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealthRepository
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail
import com.horizondev.habitbloom.screens.garden.domain.calculateLevelProgress
import com.horizondev.habitbloom.screens.garden.domain.levelToGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel for the Habit Flower Detail screen.
 * Handles data loading, habit completion, and UI state updates.
 */
class HabitFlowerDetailViewModel(
    private val habitId: Long,
    private val repository: HabitsRepository,
    private val flowerHealthRepository: FlowerHealthRepository,
    private val themeUseCase: ThemeUseCase
) : BloomViewModel<HabitFlowerDetailUiState, HabitFlowerDetailUiIntent>(
    HabitFlowerDetailUiState(
        isLoading = true,
        themeOption = themeUseCase.getThemeMode()
    )
) {
    private val TAG = "HabitFlowerDetailVM"

    init {
        loadHabitFlowerDetails()
    }

    /**
     * Loads habit details and transforms them into flower detail model.
     */
    private fun loadHabitFlowerDetails() {
        // Combine habit data with flower health data
        val habitDataFlow = repository.getUserHabitWithAllRecordsFlow(habitId)
        val healthDataFlow = flowerHealthRepository.observeFlowerHealth(habitId)

        combine(habitDataFlow, healthDataFlow) { habitInfo, flowerHealth ->
            if (habitInfo == null) {
                throw IllegalStateException("Habit not found")
            }

            // Get current date for calculations
            val today = getCurrentDate()

            // Reverse to get chronological order (oldest first)
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

            // Compute Level/Vitality/XP using EMA over scheduled records up to today (exclude future)
            val progressRecords = habitInfo.records.filter { it.date <= today }
            val levelProgress = calculateLevelProgress(
                records = progressRecords,
                daysPerWeek = habitInfo.days.size
            )

            val growthStage = levelToGrowthStage(levelProgress.level)

            // Determine flower type based on time of day
            val flowerType = FlowerType.fromTimeOfDay(habitInfo.timeOfDay)

            // Check if habit is completed today
            val isCompletedToday = habitInfo.records
                .any { it.date == today && it.isCompleted }

            // Create the flower detail model
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
                isCompletedToday = isCompletedToday,
                flowerGrowthStage = growthStage,
                flowerType = flowerType,
                flowerHealth = flowerHealth.copy(value = levelProgress.vitality),
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

    /**
     * Handles UI events from the Habit Flower Detail screen.
     */
    fun handleUiEvent(event: HabitFlowerDetailUiEvent) {
        when (event) {
            is HabitFlowerDetailUiEvent.WaterTodaysHabit -> {
                waterHabit()
            }

            is HabitFlowerDetailUiEvent.NavigateToHabitDetails -> {
                emitUiIntent(HabitFlowerDetailUiIntent.NavigateToHabitDetails(event.habitId))
            }

            is HabitFlowerDetailUiEvent.NavigateBack -> {
                emitUiIntent(HabitFlowerDetailUiIntent.NavigateBack)
            }
        }
    }

    /**
     * Completes (waters) the habit for today.
     */
    private fun waterHabit() {
        val currentState = state.value
        val habitFlowerDetail = currentState.habitFlowerDetail ?: return

        // If already completed today, don't do anything
        if (habitFlowerDetail.isCompletedToday) {
            //todo show snackbar with bloom visuals
            //emitUiIntent(HabitFlowerDetailUiIntent.ShowSnackbar("You've already watered this habit today"))
            return
        }

        // Show watering animation
        updateState { it.copy(showWateringAnimation = true) }

        launch {
            try {
                // Update habit completion status through repository
                repository.updateHabitCompletionByHabitId(
                    habitId = habitId,
                    date = getCurrentDate(),
                    isCompleted = true
                )
                // Note: The repository now takes care of updating flower health

                // Keep animation visible for a moment
                delay(1500)

                // Show success message
                //todo show snackbar with bloom visuals
                //emitUiIntent(HabitFlowerDetailUiIntent.ShowSnackbar("Habit watered successfully!"))
            } catch (e: Exception) {
                Napier.e("Error watering habit", e, tag = TAG)
                //todo show snackbar with bloom visuals
                //emitUiIntent(HabitFlowerDetailUiIntent.ShowSnackbar("Failed to water habit"))
            } finally {
                // Hide watering animation
                updateState { it.copy(showWateringAnimation = false) }
            }
        }
    }
} 