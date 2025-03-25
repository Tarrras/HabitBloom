package com.horizondev.habitbloom.screens.garden.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getTimeOfDay
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * ViewModel for the Habit Garden screen.
 * Manages the UI state for displaying habits with their flower stages.
 */
class HabitGardenViewModel(
    private val repository: HabitsRepository,
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
     * Set up the main flow to observe habits and transform them into flowers
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
    private fun loadGardenData(timeOfDay: TimeOfDay) = repository.getListOfAllUserHabitRecordsFlow()
        .onStart { updateState { it.copy(isLoading = true) } }
        .map { habitRecords ->
            // Group records by habit ID to get the latest record for each habit
            val habitGroups = habitRecords.groupBy { it.userHabitId }

            // Create HabitFlower objects for each unique habit
            val allHabitFlowers = habitGroups.mapNotNull { (habitId, records) ->
                // Get habit info from the first record (since they all have the same habit info)
                val habitInfo = records.firstOrNull() ?: return@mapNotNull null

                // Calculate the bloom stage based on streak
                val streak = habitInfo.daysStreak
                val bloomingStage = FlowerGrowthStage.fromStreak(streak)

                HabitFlower(
                    habitId = habitId,
                    name = habitInfo.name,
                    iconUrl = habitInfo.iconUrl,
                    streak = streak,
                    timeOfDay = habitInfo.timeOfDay,
                    bloomingStage = bloomingStage
                )
            }

            // Filter by the selected time of day
            allHabitFlowers.filter { it.timeOfDay == timeOfDay }
        }
        .catch { error ->
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