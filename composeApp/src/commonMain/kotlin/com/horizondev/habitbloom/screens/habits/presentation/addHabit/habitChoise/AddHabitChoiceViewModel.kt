package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import androidx.compose.material3.SnackbarDuration
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarState
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.delete_custom_habit_success
import habitbloom.composeapp.generated.resources.habit_already_added
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString

class AddHabitChoiceViewModel(
    private val repository: HabitsRepository,
    private val addHabitStateUseCase: AddHabitStateUseCase
) : BloomViewModel<AddHabitChoiceUiState, AddHabitChoiceUiIntent>(
    initialState = AddHabitChoiceUiState()
) {

    private val currentCategory: HabitCategoryData?
        get() = addHabitStateUseCase.getCurrentDraft().habitCategory

    private var cachedHabits: List<HabitInfo> = emptyList()
    private var isInitialLoad = true

    init {
        requestHabits(forceRefresh = true)

        launch {
            state
                .map { it.searchInput }
                .distinctUntilChanged()
                .debounce(300) // Reduced from 500ms for better UX
                .collect { query ->
                    applySearch(query)
                }
        }
    }

    fun handleScreenResumed() {
        if (!isInitialLoad) {
            // Only refresh if this is not the initial load
            handleUiEvent(AddHabitChoiceUiEvent.RefreshPage)
        }
    }

    fun handleUiEvent(event: AddHabitChoiceUiEvent) {
        when (event) {
            is AddHabitChoiceUiEvent.UpdateSearchInput -> {
                updateState { it.copy(searchInput = event.input) }
            }

            is AddHabitChoiceUiEvent.SelectHabit -> {
                // Update the UseCase with the selected habit
                addHabitStateUseCase.updateHabitInfo(event.habit)
                checkHabitAndProceed(event.habit)
            }

            AddHabitChoiceUiEvent.CreateCustomHabit -> {
                emitUiIntent(AddHabitChoiceUiIntent.NavigateToCreateCustomHabit(currentCategory?.id))
            }

            is AddHabitChoiceUiEvent.DeleteHabit -> {
                updateState {
                    it.copy(
                        showDeleteDialog = true,
                        habitToDelete = event.habit
                    )
                }
            }

            AddHabitChoiceUiEvent.CancelDeleteHabit -> {
                updateState {
                    it.copy(
                        showDeleteDialog = false,
                        habitToDelete = null
                    )
                }
            }

            AddHabitChoiceUiEvent.ConfirmDeleteHabit -> {
                confirmDeleteHabit()
            }

            AddHabitChoiceUiEvent.NavigateBack -> {
                emitUiIntent(AddHabitChoiceUiIntent.NavigateBack)
            }

            AddHabitChoiceUiEvent.RefreshPage -> {
                requestHabits(forceRefresh = true)
            }
        }
    }

    private fun checkHabitAndProceed(habit: HabitInfo) {
        launch {
            runCatching {
                repository.isHabitAlreadyAdded(habit.id)
            }.onSuccess { isAlreadyAdded ->
                if (isAlreadyAdded) {
                    showErrorSnackbar(getString(Res.string.habit_already_added))
                } else {
                    emitUiIntent(AddHabitChoiceUiIntent.NavigateNext(habit))
                }
            }.onFailure {
                Napier.e("Error checking if habit exists", it)
                // Proceed anyway in case of error
                emitUiIntent(AddHabitChoiceUiIntent.NavigateNext(habit))
            }
        }
    }

    private fun requestHabits(forceRefresh: Boolean) {
        launch {
            val selectedCategory = currentCategory

            if (!forceRefresh && cachedHabits.isNotEmpty()) {
                applySearch(state.value.searchInput, selectedCategory)
                return@launch
            }

            updateState { it.copy(isLoading = true, currentCategory = selectedCategory) }

            runCatching {
                repository.getHabits(searchInput = "", categoryId = selectedCategory?.id)
            }.onSuccess { result ->
                result.fold(
                    onSuccess = { habits ->
                        cachedHabits = habits
                        isInitialLoad = false
                        applySearch(state.value.searchInput, selectedCategory)
                    },
                    onFailure = {
                        Napier.e("Failed to load habits", it)
                        updateState { state -> state.copy(isLoading = false) }
                    }
                )
            }.onFailure {
                Napier.e("Failed to load habits", it)
                updateState { state -> state.copy(isLoading = false) }
            }
        }
    }


    private fun applySearch(query: String, categoryOverride: HabitCategoryData? = null) {
        val normalizedQuery = query.trim()
        val selectedCategory = categoryOverride ?: currentCategory
        val filteredHabits = if (cachedHabits.isEmpty()) {
            emptyList()
        } else if (normalizedQuery.isBlank()) {
            cachedHabits
        } else {
            cachedHabits.filter { habit ->
                habit.name.contains(normalizedQuery, ignoreCase = true)
            }
        }

        updateState {
            it.copy(
                habits = filteredHabits,
                isLoading = false,
                currentCategory = selectedCategory
            )
        }
    }


    private fun showErrorSnackbar(message: String) {
        emitUiIntent(
            AddHabitChoiceUiIntent.ShowSnackbar(
                BloomSnackbarVisuals(
                    message = message,
                    state = BloomSnackbarState.Error,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            )
        )
    }


    private fun confirmDeleteHabit() {
        val habitToDelete = state.value.habitToDelete ?: return

        launch {
            updateState { it.copy(isLoading = true, showDeleteDialog = false) }

            runCatching {
                repository.deleteCustomHabit(habitToDelete.id)
            }.onSuccess { result ->
                result.fold(
                    onSuccess = {
                        // Remove from cache and update list without extra loading
                        cachedHabits = cachedHabits.filterNot { habit ->
                            habit.id == habitToDelete.id
                        }
                        applySearch(state.value.searchInput)
                        emitUiIntent(
                            AddHabitChoiceUiIntent.ShowSnackbar(
                                BloomSnackbarVisuals(
                                    message = getString(Res.string.delete_custom_habit_success),
                                    state = BloomSnackbarState.Success,
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                            )
                        )
                    },
                    onFailure = { error ->
                        updateState { state -> state.copy(isLoading = false) }
                        showErrorSnackbar("Failed to delete habit: ${error.message}")
                    }
                )
            }.onFailure { exception ->
                Napier.e("Error deleting habit", exception)
                updateState { state -> state.copy(isLoading = false) }
                showErrorSnackbar("Error deleting habit: ${exception.message}")
            }
        }
    }
}

