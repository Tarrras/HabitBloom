package com.horizondev.habitbloom.screens.garden.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealthRepository
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.garden.domain.calculateLevelProgress
import com.horizondev.habitbloom.screens.garden.domain.levelToGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.utils.getTimeOfDay
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel for the Habit Garden screen.
 * Manages the UI state for displaying habits with their flower stages.
 */
class HabitGardenViewModel(
    private val repository: HabitsRepository,
    private val flowerHealthRepository: FlowerHealthRepository,
    themeUseCase: ThemeUseCase
) : BloomViewModel<HabitGardenUiState, HabitGardenUiIntent>(
    HabitGardenUiState(
        selectedTimeOfDay = getTimeOfDay(),
        isLoading = true,
        themeOption = themeUseCase.getThemeMode()
    )
) {
    private val TAG = "HabitGardenViewModel"

    // MutableStateFlow to hold the selected time of day for easy observation
    private val selectedTimeOfDayFlow = MutableStateFlow(getTimeOfDay())

    init {
        // Initialize repository data
        initializeData()
    }

    /**
     * Initialize repository data and prepare for habit loading
     */
    private fun initializeData() {
        launch {
            updateState { it.copy(isLoading = true) }
            repository.initData()
                .onSuccess {
                    // After successful initialization, start observing habit data
                    observeHabitData()
                }
                .onFailure { error ->
                    Napier.e("Failed to initialize data", error, tag = TAG)
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to initialize data"
                        )
                    }
                }
        }
    }

    /**
     * Set up the main flow to observe habits
     */
    private fun observeHabitData() {
        selectedTimeOfDayFlow
            .flatMapLatest { timeOfDay ->
                updateState { it.copy(isLoading = true) }
                loadGardenData(timeOfDay)
            }
            .onEach { habitFlowers ->
                updateState { currentState ->
                    currentState.copy(
                        habitFlowers = habitFlowers,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                Napier.e("Error loading garden data", error, tag = TAG)
                updateState {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load garden data"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Loads garden data and transforms habit records into flower representations
     *
     * @param timeOfDay The time of day to filter by
     * @return Flow of habit flowers
     */
    private fun loadGardenData(timeOfDay: TimeOfDay) = flow {
        // Get only the habit records for this time of day
        val habitRecords = repository.getHabitRecordsByTimeOfDay(timeOfDay)
        // Also fetch basic habit configs to derive days-per-week for EMA alpha
        val userHabits = repository.getUserHabitsWithoutDetails()
        val habitIdToDaysPerWeek = userHabits.associate { it.id to it.daysOfWeek.size }

        // Group records by habit ID
        val habitGroups = habitRecords.groupBy { it.userHabitId }

        // Process each unique habit (we don't need to process every record)
        val habitFlowers = habitGroups.keys.mapNotNull { habitId ->
            // Get all records for this habit
            val records = habitGroups[habitId] ?: return@mapNotNull null

            // Get habit info from the first record
            val habitInfo = records.firstOrNull() ?: return@mapNotNull null

            // Get health in a single call
            val health = flowerHealthRepository.getFlowerHealth(habitId)

            // Calculate Level/Vitality via EMA and map to growth stage
            val domainRecords: List<UserHabitRecord> = records.map { r ->
                UserHabitRecord(
                    id = r.id,
                    userHabitId = r.userHabitId,
                    date = r.date,
                    isCompleted = r.isCompleted
                )
            }.sortedBy { it.date }
            val daysPerWeek = habitIdToDaysPerWeek[habitId] ?: 7
            val levelProgress = calculateLevelProgress(
                records = domainRecords,
                daysPerWeek = daysPerWeek
            )
            val bloomingStage = levelToGrowthStage(levelProgress.level)

            // Create the flower object
            HabitFlower(
                habitId = habitId,
                name = habitInfo.name,
                iconUrl = habitInfo.iconUrl,
                timeOfDay = habitInfo.timeOfDay,
                bloomingStage = bloomingStage,
                health = health
            )
        }

        // Emit the list of flowers
        emit(habitFlowers)
    }.catch { error ->
        Napier.e("Error processing garden data", error, tag = TAG)
        emit(emptyList())
    }

    /**
     * Handles UI events from the Habit Garden screen.
     * @param event The UI event to handle
     */
    fun handleUiEvent(event: HabitGardenUiEvent) {
        when (event) {
            is HabitGardenUiEvent.SelectTimeOfDay -> {
                // Update the time of day in both state and flow
                updateState { it.copy(selectedTimeOfDay = event.timeOfDay) }
                selectedTimeOfDayFlow.value = event.timeOfDay
            }

            is HabitGardenUiEvent.OpenFlowerDetails -> {
                emitUiIntent(HabitGardenUiIntent.OpenFlowerDetails(event.habitId))
            }

            is HabitGardenUiEvent.RefreshGarden -> {
                updateState { it.copy(isLoading = true) }
                initializeData()
            }

            HabitGardenUiEvent.BackPressed -> {
                emitUiIntent(HabitGardenUiIntent.NavigateBack)
            }
        }
    }
} 