package com.horizondev.habitbloom.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel class for HabitBloom that provides state and uiIntent patterns.
 */
abstract class BloomViewModel<S, I>(initialState: S) : ViewModel() {

    // UI state
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // UI intents (navigation, messages, etc.)
    private val _uiIntents = MutableSharedFlow<I>()
    val uiIntents = _uiIntents.asSharedFlow()

    /**
     * Updates the UI state.
     */
    protected fun updateState(update: (S) -> S) {
        _state.update(update)
    }

    /**
     * Emits a UI intent (navigation, snackbar, dialog, etc.).
     */
    protected fun emitUiIntent(intent: I) {
        viewModelScope.launch {
            _uiIntents.emit(intent)
        }
    }

    /**
     * Launches a coroutine in the ViewModel's scope.
     */
    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }
} 