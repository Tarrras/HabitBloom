package com.horizondev.habitbloom.screens.garden.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.garden.domain.calculateLevelProgress
import com.horizondev.habitbloom.screens.garden.domain.levelToGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getTimeOfDay
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate

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
    private var observeHabitDataJob: Job? = null

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
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHabitData() {
        observeHabitDataJob?.cancel()
        observeHabitDataJob = selectedTimeOfDayFlow
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
    private fun loadGardenData(timeOfDay: TimeOfDay) =
        repository.getListOfAllUserHabitRecordsFlow()
            .map { habitRecords ->
                val userHabits = repository.getUserHabitsWithoutDetails()
                buildHabitFlowersForGarden(
                    habitRecords = habitRecords.filter { it.timeOfDay == timeOfDay },
                    userHabits = userHabits
                )
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

internal fun buildHabitFlowersForGarden(
    habitRecords: List<UserHabitRecordFullInfo>,
    userHabits: List<UserHabit>,
    today: LocalDate = getCurrentDate()
): List<HabitFlower> {
    val habitIdToDaysPerWeek = userHabits.associate { it.id to it.daysOfWeek.size }
    val habitGroups = habitRecords.groupBy { it.userHabitId }

    return habitGroups.keys.mapNotNull { habitId ->
        val records = habitGroups[habitId] ?: return@mapNotNull null
        val habitInfo = records.firstOrNull() ?: return@mapNotNull null
        val domainRecords: List<UserHabitRecord> = records.map { record ->
            UserHabitRecord(
                id = record.id,
                userHabitId = record.userHabitId,
                date = record.date,
                isCompleted = record.isCompleted
            )
        }.sortedBy { it.date }.filter { it.date <= today }
        val levelProgress = calculateLevelProgress(
            records = domainRecords,
            daysPerWeek = habitIdToDaysPerWeek[habitId] ?: 7
        )

        HabitFlower(
            habitId = habitId,
            name = habitInfo.name,
            iconUrl = habitInfo.iconUrl,
            timeOfDay = habitInfo.timeOfDay,
            bloomingStage = levelToGrowthStage(levelProgress.level),
            health = FlowerHealth(
                value = levelProgress.vitality,
                consecutiveMissedDays = levelProgress.currentMissedDays
            )
        )
    }
}
