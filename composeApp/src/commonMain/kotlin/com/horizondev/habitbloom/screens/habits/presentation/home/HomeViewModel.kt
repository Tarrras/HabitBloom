package com.horizondev.habitbloom.screens.habits.presentation.home

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealthRepository
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getCurrentDateTime
import com.horizondev.habitbloom.utils.getTimeOfDay
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.LocalDate

/**
 * ViewModel for the Home screen.
 * Manages the UI state for displaying habits, filtering by date and time of day,
 * and handling user interactions.
 */
class HomeViewModel(
    private val repository: HabitsRepository,
    private val flowerHealthRepository: FlowerHealthRepository
) : BloomViewModel<HomeScreenUiState, HomeScreenUiIntent>(
    HomeScreenUiState(
        selectedTimeOfDay = getTimeOfDay(),
        selectedDate = getCurrentDate()
    )
) {
    private val TAG = "HomeViewModel"

    // MutableStateFlow to hold the selected date and time of day for easy observation
    private val selectedTimeOfDayFlow = MutableStateFlow(getTimeOfDay())
    private val selectedDateFlow = MutableStateFlow(getCurrentDate())

    init {
        // Initialize repository data
        initializeData()
        
        // Set up the main data observation flow
        observeHabitData()
    }

    /**
     * Initialize repository data and prepare for habit loading
     */
    private fun initializeData() {
        launch {
            updateState { it.copy(isLoading = true) }
            repository.initData()
                .onFailure { error ->
                    Napier.e("Failed to initialize data", error, tag = TAG)
                    updateState { it.copy(isLoading = false) }
                }
        }
    }

    /**
     * Set up the main flow to observe habits based on selected date and time of day
     */
    private fun observeHabitData() {
        // Combine the date and time of day flows to reload habits when either changes
        combine(
            selectedDateFlow,
            selectedTimeOfDayFlow
        ) { date, timeOfDay ->
            Pair(date, timeOfDay)
        }.flatMapLatest { (date, timeOfDay) ->
            updateState { it.copy(isLoading = true) }
            loadHabitsForDateAndTimeOfDay(date, timeOfDay)
        }.onEach { filteredState ->
            // Update the UI state with the filtered habits
            updateState { currentState ->
                currentState.copy(
                    userHabits = filteredState.habits,
                    totalHabitsCountForTimeOfDay = filteredState.totalHabitsCountForTimeOfDay,
                    completedHabitsCountForTimeOfDay = filteredState.completedHabitsCountForTimeOfDay,
                    userCompletedAllHabitsForTimeOfDay = filteredState.allCompleted,
                    isLoading = false
                )
            }
            // Preload health snapshots for visible habits (best-effort)
            val healthMap = filteredState.habits.associate { info ->
                val health =
                    runCatching { flowerHealthRepository.getFlowerHealth(info.userHabitId) }
                        .getOrNull()
                info.userHabitId to (health ?: state.value.habitHealthMap[info.userHabitId])
            }.filterValues { it != null }.mapValues { it.value!! }
            if (healthMap.isNotEmpty()) {
                val vitality =
                    healthMap.mapValues { (_, h) -> (kotlin.math.round(h.value * 1000f) / 10f).toInt() }
                // Compute dueInMinutes from reminder if enabled and belongs to selected date
                val dueMap = filteredState.habits.associate { info ->
                    val details = repository.getUserHabitDetails(info.userHabitId)
                    val reminder = details?.reminderTime
                    val enabled = details?.reminderEnabled == true
                    val minutes =
                        if (enabled && reminder != null && selectedDateFlow.value == getCurrentDate()) {
                            val now = getCurrentDateTime()
                            val diffMinutes =
                                ((reminder.hour - now.hour) * 60) + (reminder.minute - now.minute)
                            if (diffMinutes > 0) diffMinutes else null
                        } else null
                    info.userHabitId to minutes
                }
                updateState {
                    it.copy(
                        habitHealthMap = it.habitHealthMap + healthMap,
                        habitVitalityPercent = it.habitVitalityPercent + vitality,
                        habitDueInMinutes = it.habitDueInMinutes + dueMap
                    )
                }
            }
        }.catch { error ->
            Napier.e("Error loading habits", error, tag = TAG)
            updateState { it.copy(isLoading = false) }
        }.launchIn(viewModelScope)
    }

    /**
     * Loads habits for a specific date and time of day
     *
     * @param date The date to load habits for
     * @param timeOfDay The time of day to filter by
     * @return Flow of filtered habit state
     */
    private fun loadHabitsForDateAndTimeOfDay(
        date: LocalDate,
        timeOfDay: TimeOfDay
    ): Flow<FilteredHabitState> {
        return repository.getUserHabitsByDayFlow(date)
            .map { allHabits ->
                updateState { it.copy(isLoading = true) }
                // Filter habits by time of day
                val filteredHabits = allHabits.filter { it.timeOfDay == timeOfDay }

                // Create a state object with all the calculated values
                FilteredHabitState(
                    habits = filteredHabits,
                    totalHabitsCountForTimeOfDay = filteredHabits.count(),
                    completedHabitsCountForTimeOfDay = filteredHabits.count { it.isCompleted },
                    allCompleted = filteredHabits.isNotEmpty() &&
                            filteredHabits.all { it.isCompleted }
                )
            }
            .onStart { updateState { it.copy(isLoading = true) } }
            .catch { error ->
                Napier.e("Error loading habits for date: $date", error, tag = TAG)
                emit(FilteredHabitState.empty())
            }
    }

    /**
     * Handles UI events from the Home screen.
     * @param event The UI event to handle
     */
    fun handleUiEvent(event: HomeScreenUiEvent) {
        when (event) {
            is HomeScreenUiEvent.SelectTimeOfDay -> {
                // Update the time of day in both state and flow
                updateState { it.copy(selectedTimeOfDay = event.timeOfDay) }
                selectedTimeOfDayFlow.value = event.timeOfDay
            }

            is HomeScreenUiEvent.SelectDate -> {
                val newDate = event.date
                val editMode = newDate == getCurrentDate()

                // Update the date in both state and flow
                updateState { it.copy(selectedDate = newDate, habitStatusEditMode = editMode) }
                selectedDateFlow.value = newDate
            }

            is HomeScreenUiEvent.OpenHabitDetails -> {
                emitUiIntent(HomeScreenUiIntent.OpenHabitDetails(event.userHabitId))
            }

            is HomeScreenUiEvent.ChangeHabitCompletionStatus -> {
                updateHabitCompletionStatus(
                    habitRecordId = event.id,
                    isCompleted = event.isCompleted
                )
            }

            HomeScreenUiEvent.AddNewHabit -> {
                emitUiIntent(HomeScreenUiIntent.OpenAddNewHabit)
            }
        }
    }

    /**
     * Updates the completion status of a habit
     *
     * @param habitRecordId The ID of the habit record to update
     * @param isCompleted The new completion status
     */
    private fun updateHabitCompletionStatus(habitRecordId: Long, isCompleted: Boolean) {
        launch {
            try {
                repository.updateHabitCompletionByRecordId(
                    habitRecordId = habitRecordId,
                    date = selectedDateFlow.value,
                    isCompleted = isCompleted
                )
            } catch (e: Exception) {
                Napier.e("Failed to update habit completion status", e, tag = TAG)
                // You could emit a UI event here to show an error message if needed
            }
        }
    }

    /**
     * Data class to hold filtered habit state to avoid multiple calculations
     */
    private data class FilteredHabitState(
        val habits: List<UserHabitRecordFullInfo>,
        val totalHabitsCountForTimeOfDay: Int,
        val completedHabitsCountForTimeOfDay: Int,
        val allCompleted: Boolean
    ) {
        companion object {
            fun empty() = FilteredHabitState(
                habits = emptyList(),
                totalHabitsCountForTimeOfDay = 0,
                completedHabitsCountForTimeOfDay = 0,
                allCompleted = false
            )
        }
    }
} 