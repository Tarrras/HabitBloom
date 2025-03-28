package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.components.flowerdetail.CompletionHistorySection
import com.horizondev.habitbloom.screens.garden.components.flowerdetail.FlowerVisualization
import com.horizondev.habitbloom.screens.garden.components.flowerdetail.HabitDetailSection
import com.horizondev.habitbloom.screens.garden.components.flowerdetail.HabitInfoSection
import com.horizondev.habitbloom.screens.garden.components.flowerdetail.WaterHabitButton
import com.horizondev.habitbloom.utils.getGardenBackgroundRes
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.habit_flower_details
import habitbloom.composeapp.generated.resources.show_bloom_progress
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Main screen to display a habit as a growing flower.
 *
 * @param habitId The ID of the habit to display
 * @param onNavigateBack Callback when the back button is pressed
 * @param onNavigateToHabitDetails Callback when the edit habit button is pressed
 */
@Composable
fun HabitFlowerDetailScreen(
    habitId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToHabitDetails: (Long) -> Unit,
) {
    val viewModel = koinViewModel<HabitFlowerDetailViewModel> {
        parametersOf(habitId)
    }
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Process UI intents
    LaunchedEffect(key1 = viewModel) {
        viewModel.uiIntents.collect { intent ->
            when (intent) {
                is HabitFlowerDetailUiIntent.NavigateBack -> {
                    onNavigateBack()
                }

                is HabitFlowerDetailUiIntent.NavigateToHabitDetails -> {
                    onNavigateToHabitDetails(intent.habitId)
                }

                is HabitFlowerDetailUiIntent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(intent.visuals)
                }
            }
        }
    }

    HabitFlowerDetailScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

@Composable
fun HabitFlowerDetailScreenContent(
    uiState: HabitFlowerDetailUiState,
    snackbarHostState: SnackbarHostState,
    handleUiEvent: (HabitFlowerDetailUiEvent) -> Unit
) {
    val hazeState = remember { HazeState() }
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val backgroundImage = remember(uiState.themeOption) {
        uiState.themeOption.getGardenBackgroundRes(isSystemInDarkTheme)
    }

    // Show Bloom Progress button
    var showGrowthPathBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            BloomSnackbarHost(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                snackBarState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
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
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.statusBarsPadding())

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false)
                            ) {
                                handleUiEvent(HabitFlowerDetailUiEvent.NavigateBack)
                            },
                        tint = BloomTheme.colors.textColor.primary
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Text(
                        text = stringResource(Res.string.habit_flower_details),
                        style = BloomTheme.typography.title,
                        color = BloomTheme.colors.textColor.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    uiState.isLoading -> {
                        // Loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            BloomLoadingAnimation(
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    uiState.errorMessage != null -> {
                        // Error state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    uiState.habitFlowerDetail != null -> {
                        // Habit flower detail content
                        val habitFlowerDetail = uiState.habitFlowerDetail

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 16.dp)
                        ) {
                            // Flower visualization
                            FlowerVisualization(
                                flowerType = habitFlowerDetail.flowerType,
                                growthStage = habitFlowerDetail.flowerGrowthStage,
                                showWateringAnimation = uiState.showWateringAnimation,
                                flowerHealth = habitFlowerDetail.flowerHealth
                            )

                            // Water habit button
                            WaterHabitButton(
                                isCompleted = habitFlowerDetail.isCompletedToday,
                                isLoading = uiState.showWateringAnimation,
                                onClick = {
                                    handleUiEvent(HabitFlowerDetailUiEvent.WaterTodaysHabit)
                                }
                            )

                            // Habit info section
                            HabitInfoSection(
                                habitName = habitFlowerDetail.name,
                                timeOfDay = habitFlowerDetail.timeOfDay,
                                growthStage = habitFlowerDetail.flowerGrowthStage,
                                currentStreak = habitFlowerDetail.currentStreak,
                                streaksToNextStage = habitFlowerDetail.streaksToNextStage
                            )

                            Spacer(modifier = Modifier.height(8.dp))


                            BloomPrimaryFilledButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                text = stringResource(Res.string.show_bloom_progress),
                                onClick = {
                                    showGrowthPathBottomSheet = true
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 7-day completion history
                            CompletionHistorySection(
                                completions = habitFlowerDetail.lastSevenDaysCompletions
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Habit details section
                            HabitDetailSection(
                                description = habitFlowerDetail.description,
                                startDate = habitFlowerDetail.startDate,
                                reminderTime = habitFlowerDetail.reminderTime,
                                onCheckFullHabitInfoClick = {
                                    handleUiEvent(
                                        HabitFlowerDetailUiEvent.NavigateToHabitDetails(
                                            habitFlowerDetail.habitId
                                        )
                                    )
                                }
                            )
                        }


                        if (showGrowthPathBottomSheet) {
                            HabitGrowthPathBottomSheet(
                                currentStage = habitFlowerDetail.flowerGrowthStage,
                                streaksToNextStage = habitFlowerDetail.streaksToNextStage,
                                currentStreak = habitFlowerDetail.currentStreak,
                                onDismissRequest = { showGrowthPathBottomSheet = false },
                                flowerType = habitFlowerDetail.flowerType
                            )
                        }
                    }
                }
            }
        }
    }
}