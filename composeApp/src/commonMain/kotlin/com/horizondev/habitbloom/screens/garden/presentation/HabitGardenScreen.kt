package com.horizondev.habitbloom.screens.garden.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.switcher.TimeOfDaySwitcher
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.components.HabitFlowerCell
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getTitle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.garden_background_evening
import habitbloom.composeapp.generated.resources.garden_background_morning
import habitbloom.composeapp.generated.resources.no_habits_for_time_of_day_in_garden
import habitbloom.composeapp.generated.resources.retry
import habitbloom.composeapp.generated.resources.your_habit_garden
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Main screen component for the Habit Garden.
 * Displays a grid of habit flowers with their blooming stages.
 */
@Composable
fun HabitGardenScreen(
    viewModel: HabitGardenViewModel,
    onNavigateToHabitFlower: (Long) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.state.collectAsState()

    viewModel.uiIntents.collectAsEffect { uiIntent ->
        when (uiIntent) {
            HabitGardenUiIntent.NavigateBack -> {
                onNavigateBack()
            }

            is HabitGardenUiIntent.OpenFlowerDetails -> {
                onNavigateToHabitFlower(uiIntent.habitId)
            }
        }
    }

    HabitGardenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

/**
 * Content component for the Habit Garden screen.
 */
@Composable
private fun HabitGardenContent(
    uiState: HabitGardenUiState,
    handleUiEvent: (HabitGardenUiEvent) -> Unit
) {
    val hazeState = remember { HazeState() }

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val backgroundImage = remember(uiState.themeOption) {
        when (uiState.themeOption) {
            ThemeOption.Device -> {
                if (isSystemInDarkTheme) Res.drawable.garden_background_evening
                else Res.drawable.garden_background_morning
            }

            ThemeOption.Dark -> Res.drawable.garden_background_evening
            ThemeOption.Light -> Res.drawable.garden_background_morning
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .fillMaxSize(),
            painter = painterResource(backgroundImage),
            contentScale = ContentScale.Crop,
            contentDescription = "background"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())

            Spacer(modifier = Modifier.height(18.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false)
                        ) {
                            handleUiEvent(HabitGardenUiEvent.BackPressed)
                        },
                    tint = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = stringResource(Res.string.your_habit_garden),
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time of day switcher
            TimeOfDaySwitcher(
                selectedTimeOfDay = uiState.selectedTimeOfDay,
                onTimeOfDaySelected = { timeOfDay ->
                    handleUiEvent(HabitGardenUiEvent.SelectTimeOfDay(timeOfDay))
                },
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.background(
                                color = BloomTheme.colors.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                        ) {

                            Text(
                                text = uiState.errorMessage,
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.primary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            BloomPrimaryOutlinedButton(
                                onClick = { handleUiEvent(HabitGardenUiEvent.RefreshGarden) },
                                text = stringResource(Res.string.retry)
                            )
                        }
                    }
                }

                // Show empty state
                uiState.habitFlowers.isEmpty() -> {
                    Box(
                        modifier = Modifier.background(
                            color = BloomTheme.colors.surface,
                            shape = RoundedCornerShape(16.dp)
                        ).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.no_habits_for_time_of_day_in_garden,
                                uiState.selectedTimeOfDay.getTitle().lowercase()
                            ),
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
                                onClick = {
                                    handleUiEvent(
                                        HabitGardenUiEvent.OpenFlowerDetails(
                                            habitId = habitFlower.habitId
                                        )
                                    )
                                },
                                modifier = Modifier.padding(8.dp),
                                hazeState = hazeState
                            )
                        }
                    }
                }
            }
        }
    }
} 