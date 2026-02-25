package com.horizondev.habitbloom.screens.habits.presentation.home

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
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


class HomeViewModel(
    private val repository: HabitsRepository
) : BloomViewModel<HomeScreenUiState, HomeScreenUiIntent>(
    HomeScreenUiState(
        selectedTimeOfDay = getTimeOfDay(),
        selectedDate = getCurrentDate()
    )
) {
    private val TAG = "HomeViewModel"

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
            .onStart { updateState { it.copy(isLoading = true) } }
            .map { allHabits ->
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
                if (state.value.selectedTimeOfDay == event.timeOfDay) return
                // Update the time of day in both state and flow
                updateState { it.copy(selectedTimeOfDay = event.timeOfDay) }
                selectedTimeOfDayFlow.value = event.timeOfDay
            }

            is HomeScreenUiEvent.SelectDate -> {
                val newDate = event.date
                val editMode = newDate == getCurrentDate()
                val currentState = state.value
                if (currentState.selectedDate == newDate &&
                    currentState.habitStatusEditMode == editMode
                ) {
                    return
                }

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
