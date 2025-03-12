package com.horizondev.habitbloom.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.utils.collectAsEffect

/**
 * Calendar screen composable that displays habit calendar.
 */
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is CalendarUiIntent.OpenHabitDetails -> {
                // Navigation will be handled by parent NavHost
            }
        }
    }

    CalendarScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
    )
}

@Composable
private fun CalendarScreenContent(
    uiState: CalendarUiState,
    handleUiEvent: (CalendarUiEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BloomLoadingAnimation(
                modifier = Modifier.size(200.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("This sections is under development :(")
        }
    }
}