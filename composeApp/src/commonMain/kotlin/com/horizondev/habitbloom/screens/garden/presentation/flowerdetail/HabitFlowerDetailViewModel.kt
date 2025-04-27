package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealthRepository
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getLongestCompletionStreak
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

            // Consider both streak and health for the growth stage
            val growthStage = FlowerGrowthStage.fromStreakAndHealth(
                streak = habitInfo.daysStreak,
                health = flowerHealth
            )

            // Determine flower type based on time of day
            val flowerType = FlowerType.fromTimeOfDay(habitInfo.timeOfDay)

            // Calculate streaks needed to reach next stage
            val streaksToNextStage = FlowerGrowthStage.streakToNextStage(habitInfo.daysStreak)

            // Check if habit is completed today
            val isCompletedToday = habitInfo.records
                .any { it.date == today && it.isCompleted }

            val longestStreak = habitInfo.records.getLongestCompletionStreak()
            val maxStage = FlowerGrowthStage.fromStreak(longestStreak)

            // Create the flower detail model
            HabitFlowerDetail(
                habitId = habitInfo.userHabitId,
                name = habitInfo.name,
                description = habitInfo.description,
                iconUrl = habitInfo.iconUrl,
                timeOfDay = habitInfo.timeOfDay,
                currentStreak = habitInfo.daysStreak,
                longestStreak = longestStreak,
                startDate = habitInfo.startDate,
                endDate = habitInfo.endDate,
                reminderTime = habitInfo.reminderTime.takeIf { habitInfo.reminderEnabled },
                lastSevenDaysCompletions = lastSevenScheduledDays,
                isCompletedToday = isCompletedToday,
                flowerGrowthStage = growthStage,
                flowerType = flowerType,
                streaksToNextStage = streaksToNextStage,
                flowerHealth = flowerHealth,
                flowerMaxGrowthStage = maxStage
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