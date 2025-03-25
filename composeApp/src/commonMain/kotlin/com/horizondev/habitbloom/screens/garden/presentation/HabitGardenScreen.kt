package com.horizondev.habitbloom.screens.garden.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.switcher.TimeOfDaySwitcher
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.components.HabitFlowerCell
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.your_habit_garden
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Main screen component for the Habit Garden.
 * Displays a grid of habit flowers with their blooming stages.
 */
@Composable
fun HabitGardenScreen(
    onNavigateToHabitFlower: (Long) -> Unit,
    viewModel: HabitGardenViewModel = koinInject()
) {
    val uiState by viewModel.state.collectAsState()

    HabitGardenContent(
        uiState = uiState,
        onTimeOfDaySelected = { timeOfDay ->
            viewModel.handleUiEvent(HabitGardenUiEvent.SelectTimeOfDay(timeOfDay))
        },
        onHabitClick = { habitId ->
            onNavigateToHabitFlower(habitId)
        },
        onRefresh = {
            viewModel.handleUiEvent(HabitGardenUiEvent.RefreshGarden)
        }
    )
}

/**
 * Content component for the Habit Garden screen.
 */
@Composable
private fun HabitGardenContent(
    uiState: HabitGardenUiState,
    onTimeOfDaySelected: (TimeOfDay) -> Unit,
    onHabitClick: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())

        Text(
            text = stringResource(Res.string.your_habit_garden),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time of day switcher
        TimeOfDaySwitcher(
            selectedTimeOfDay = uiState.selectedTimeOfDay,
            onTimeOfDaySelected = onTimeOfDaySelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on state
        when {
            // Show loading
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    BloomLoadingAnimation(
                        modifier = Modifier.size(150.dp)
                    )
                }
            }

            // Show error if present
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = uiState.errorMessage,
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onRefresh
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            // Show empty state
            uiState.habitFlowers.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits to display for ${uiState.selectedTimeOfDay.name.lowercase()} time.\nAdd some habits to see your garden grow!",
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // Show garden grid
            else -> {
                // Staggered grid of habit flowers
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.habitFlowers,
                        key = { it.habitId }
                    ) { habitFlower ->
                        HabitFlowerCell(
                            habitFlower = habitFlower,
                            onClick = { onHabitClick(habitFlower.habitId) },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
} 