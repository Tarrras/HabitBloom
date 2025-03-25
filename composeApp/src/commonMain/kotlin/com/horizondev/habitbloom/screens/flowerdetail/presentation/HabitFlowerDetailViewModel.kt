package com.horizondev.habitbloom.screens.flowerdetail.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.flowerdetail.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.flowerdetail.domain.FlowerType
import com.horizondev.habitbloom.screens.flowerdetail.domain.HabitFlowerDetail
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.minusDays
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.daysUntil

/**
 * ViewModel for the Habit Flower Detail screen.
 * Handles data loading, habit completion, and UI state updates.
 */
class HabitFlowerDetailViewModel(
    private val habitId: Long,
    private val repository: HabitsRepository
) : BloomViewModel<HabitFlowerDetailUiState, HabitFlowerDetailUiIntent>(
    HabitFlowerDetailUiState(isLoading = true)
) {
    private val TAG = "HabitFlowerDetailVM"

    init {
        loadHabitFlowerDetails()
    }

    /**
     * Loads habit details and transforms them into flower detail model.
     */
    private fun loadHabitFlowerDetails() {
        repository.getUserHabitWithAllRecordsFlow(habitId)
            .map { habitInfo ->
                if (habitInfo == null) {
                    throw IllegalStateException("Habit not found")
                }

                // Get current date for calculations
                val today = getCurrentDate()

                // Calculate which records are within the last 7 days
                val lastSevenDaysRecords = habitInfo.records
                    .filter { record ->
                        val recordDate = record.date
                        val daysBetween = recordDate.daysUntil(today)
                        daysBetween <= 6 // 0 to 6 days ago (including today)
                    }
                    .sortedBy { it.date }

                // Map to daily completion records for the last 7 days
                val lastSevenDays = (0..6).map { daysAgo ->
                    val targetDate = today.minusDays(daysAgo.toLong())
                    val record = lastSevenDaysRecords.firstOrNull { it.date == targetDate }

                    HabitFlowerDetail.DailyCompletion(
                        date = targetDate,
                        isCompleted = record?.isCompleted ?: false
                    )
                }.reversed() // Most recent last

                // Determine flower growth stage based on current streak
                val growthStage = FlowerGrowthStage.fromStreak(habitInfo.daysStreak)

                // Determine flower type based on time of day
                val flowerType = FlowerType.fromTimeOfDay(habitInfo.timeOfDay)

                // Calculate streaks needed to reach next stage
                val streaksToNextStage = FlowerGrowthStage.streakToNextStage(habitInfo.daysStreak)

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
                    currentStreak = habitInfo.daysStreak,
                    longestStreak = 0, //todo add later
                    startDate = habitInfo.startDate,
                    repeats = habitInfo.repeats,
                    repeatDays = habitInfo.days,
                    reminderTime = habitInfo.reminderTime,
                    lastSevenDaysCompletions = lastSevenDays,
                    isCompletedToday = isCompletedToday,
                    flowerGrowthStage = growthStage,
                    flowerType = flowerType,
                    streaksToNextStage = streaksToNextStage
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

            is HabitFlowerDetailUiEvent.NavigateToEditHabit -> {
                emitUiIntent(HabitFlowerDetailUiIntent.NavigateToEditHabit(event.habitId))
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
            emitUiIntent(HabitFlowerDetailUiIntent.ShowSnackbar("You've already watered this habit today"))
            return
        }

        // Show watering animation
        updateState { it.copy(showWateringAnimation = true) }

        launch {
            try {
                // Update habit completion status
                repository.updateHabitCompletion(
                    habitRecordId = habitId,
                    date = getCurrentDate(),
                    isCompleted = true
                )

                // Keep animation visible for a moment
                delay(1500)

                // Show success message
                emitUiIntent(HabitFlowerDetailUiIntent.ShowSnackbar("Habit watered successfully!"))
            } catch (e: Exception) {
                Napier.e("Error watering habit", e, tag = TAG)
                emitUiIntent(HabitFlowerDetailUiIntent.ShowSnackbar("Failed to water habit"))
            } finally {
                // Hide watering animation
                updateState { it.copy(showWateringAnimation = false) }
            }
        }
    }
} 