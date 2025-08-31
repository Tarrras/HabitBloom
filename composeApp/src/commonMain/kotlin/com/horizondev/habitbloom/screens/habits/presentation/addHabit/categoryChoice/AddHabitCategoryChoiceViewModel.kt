package com.horizondev.habitbloom.screens.habits.presentation.addHabit.categoryChoice

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.usecases.AddHabitStateUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * ViewModel for the category screen.
 */
class AddHabitCategoryChoiceViewModel(
    private val repository: HabitsRepository,
    private val addHabitStateUseCase: AddHabitStateUseCase
) :
    BloomViewModel<AddHabitCategoryUiState, AddHabitCategoryUiIntent>(
        initialState = AddHabitCategoryUiState()
    ) {

    val categoriesFlow = flow {
        emit(repository.getHabitCategories())
    }.onStart {
        updateState { it.copy(isLoading = true) }
    }.onEach { categoriesResult ->
        categoriesResult.onSuccess { data ->
            updateState { it.copy(categories = data, isLoading = false) }
        }.onFailure { error ->
            // TODO: Handle error
            updateState { it.copy(isLoading = false) }
        }
    }.catch {
        updateState { it.copy(isLoading = false) }
    }.launchIn(viewModelScope)

    fun handleUiEvent(event: AddHabitCategoryUiEvent) {
        when (event) {
            is AddHabitCategoryUiEvent.SelectCategory -> {
                // Update the UseCase with the selected category
                addHabitStateUseCase.updateHabitCategory(event.category)
                emitUiIntent(AddHabitCategoryUiIntent.NavigateToHabitChoice)
            }

            AddHabitCategoryUiEvent.NavigateBack -> {
                emitUiIntent(AddHabitCategoryUiIntent.NavigateBack)
            }
        }
    }
}

/**
 * UI State for the category choice screen.
 */
data class AddHabitCategoryUiState(
    val isLoading: Boolean = true,
    val categories: List<HabitCategoryData> = emptyList()
)

/**
 * UI Events that can be triggered from the UI.
 */
sealed interface AddHabitCategoryUiEvent {
    data class SelectCategory(val category: HabitCategoryData) : AddHabitCategoryUiEvent
    data object NavigateBack : AddHabitCategoryUiEvent
}

/**
 * UI Intents emitted by the ViewModel.
 */
sealed interface AddHabitCategoryUiIntent {
    data object NavigateToHabitChoice : AddHabitCategoryUiIntent
    data object NavigateBack : AddHabitCategoryUiIntent
} 