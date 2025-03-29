package com.horizondev.habitbloom.screens.garden.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getTimeOfDay
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

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
        .flatMapLatest { habitRecords ->
            // Group records by habit ID to get the latest record for each habit
            val habitGroups = habitRecords.groupBy { it.userHabitId }

            // We'll collect all the flowables for each habit flower
            val habitFlowerFlows = habitGroups.map { (habitId, records) ->
                // Get habit info from the first record (since they all have the same habit info)
                val habitInfo = records.firstOrNull() ?: return@map flowOf<HabitFlower?>(null)

                // Get health flowable for this habit
                val healthFlow = repository.observeFlowerHealth(habitId)

                // Combine habit info with health
                healthFlow.map { health ->
                    // Calculate the bloom stage based on streak
                    val streak = habitInfo.daysStreak
                    val bloomingStage = FlowerGrowthStage.fromStreak(streak)

                    // Get all records for this habit and calculate longest streak from completion history
                    val habitRecords = records.sortedBy { it.date }
                    val longestStreak = calculateLongestStreak(habitRecords)
                    val maxStage = FlowerGrowthStage.fromStreak(longestStreak)

                    HabitFlower(
                        habitId = habitId,
                        name = habitInfo.name,
                        iconUrl = habitInfo.iconUrl,
                        streak = streak,
                        timeOfDay = habitInfo.timeOfDay,
                        bloomingStage = bloomingStage,
                        maxStage = maxStage,
                        health = health
                    )
                }
            }

            // Combine all flowables into a single flow of list
            combine(habitFlowerFlows) { flowers ->
                flowers.filterNotNull().filter { it.timeOfDay == timeOfDay }
            }
        }
        .catch { error ->
            Napier.e("Error processing garden data", error, tag = TAG)
            emit(emptyList())
        }

    /**
     * Calculates the maximum streak achieved from a list of habit records
     */
    private fun calculateLongestStreak(records: List<UserHabitRecordFullInfo>): Int {
        if (records.isEmpty()) return 0

        var maxStreak = 0
        var currentStreak = 0
        var lastDate: LocalDate? = null

        // Sort by date to ensure we process records chronologically
        val sortedRecords = records.sortedBy { it.date }

        for (record in sortedRecords) {
            if (record.isCompleted) {
                if (lastDate == null) {
                    // Starting a new streak
                    currentStreak = 1
                } else if (record.date == lastDate.plus(1, DateTimeUnit.DAY)) {
                    // Continuing streak
                    currentStreak++
                } else {
                    // Gap in dates, start a new streak
                    currentStreak = 1
                }

                maxStreak = maxOf(maxStreak, currentStreak)
                lastDate = record.date
            } else {
                // Break in streak
                currentStreak = 0
                lastDate = null
            }
        }

        return maxStreak
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