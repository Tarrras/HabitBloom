package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.already_watered_habit_today
import habitbloom.composeapp.generated.resources.failed_to_water_habit
import habitbloom.composeapp.generated.resources.habit_watered_successfully
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the Habit Flower Detail screen.
 * Handles data loading, habit completion, and UI state updates.
 */
class HabitFlowerDetailViewModel(
    private val habitId: Long,
    private val repository: HabitsRepository,
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
        repository.getUserHabitWithAllRecordsFlow(habitId)
            .map { habitInfo ->
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
                    reminderTime = habitInfo.reminderTime,
                    lastSevenDaysCompletions = lastSevenScheduledDays,
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
    private fun waterHabit() = viewModelScope.launch {
        val currentState = state.value
        val habitFlowerDetail = currentState.habitFlowerDetail ?: return@launch

        // If already completed today, don't do anything
        if (habitFlowerDetail.isCompletedToday) {
            emitUiIntent(
                HabitFlowerDetailUiIntent.ShowSnackbar(
                    BloomSnackbarVisuals(
                        message = getString(Res.string.already_watered_habit_today),
                        state = BloomSnackbarState.Warning,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                )
            )
        }

        // Show watering animation
        updateState { it.copy(showWateringAnimation = true) }

        runCatching {
            // Update habit completion status
            repository.updateHabitCompletionByHabitId(
                habitId = habitId,
                date = getCurrentDate(),
                isCompleted = true
            )

            // Keep animation visible for a moment
            delay(1500)

            // Show success message
            emitUiIntent(
                HabitFlowerDetailUiIntent.ShowSnackbar(
                    BloomSnackbarVisuals(
                        message = getString(Res.string.habit_watered_successfully),
                        state = BloomSnackbarState.Success,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                )
            )
        }.onFailure { e ->
            Napier.e("Error watering habit", e, tag = TAG)

            // Show error message
            emitUiIntent(
                HabitFlowerDetailUiIntent.ShowSnackbar(
                    BloomSnackbarVisuals(
                        message = getString(Res.string.failed_to_water_habit),
                        state = BloomSnackbarState.Error,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                )
            )
        }

        // Hide watering animation regardless of success or failure
        updateState { it.copy(showWateringAnimation = false) }
    }
} 