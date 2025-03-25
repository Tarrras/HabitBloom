package com.horizondev.habitbloom.screens.flowerdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.flowerdetail.components.CompletionHistorySection
import com.horizondev.habitbloom.screens.flowerdetail.components.FlowerVisualization
import com.horizondev.habitbloom.screens.flowerdetail.components.HabitDetailSection
import com.horizondev.habitbloom.screens.flowerdetail.components.HabitInfoSection
import com.horizondev.habitbloom.screens.flowerdetail.components.WaterHabitButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Main screen to display a habit as a growing flower.
 *
 * @param habitId The ID of the habit to display
 * @param onNavigateBack Callback when the back button is pressed
 * @param onNavigateToEditHabit Callback when the edit habit button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFlowerDetailScreen(
    habitId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEditHabit: (Long) -> Unit,
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

                is HabitFlowerDetailUiIntent.NavigateToEditHabit -> {
                    onNavigateToEditHabit(intent.habitId)
                }

                is HabitFlowerDetailUiIntent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(intent.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Habit Flower",
                        style = BloomTheme.typography.title,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleUiEvent(HabitFlowerDetailUiEvent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BloomTheme.colors.background,
                    titleContentColor = BloomTheme.colors.textColor.primary,
                    navigationIconContentColor = BloomTheme.colors.textColor.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BloomTheme.colors.background)
        ) {
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
                            text = uiState.errorMessage!!,
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                uiState.habitFlowerDetail != null -> {
                    // Habit flower detail content
                    val habitFlowerDetail = uiState.habitFlowerDetail!!

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
                            showWateringAnimation = uiState.showWateringAnimation
                        )

                        // Habit info section
                        HabitInfoSection(
                            habitName = habitFlowerDetail.name,
                            timeOfDay = habitFlowerDetail.timeOfDay,
                            growthStage = habitFlowerDetail.flowerGrowthStage,
                            currentStreak = habitFlowerDetail.currentStreak,
                            streaksToNextStage = habitFlowerDetail.streaksToNextStage
                        )

                        // Water habit button
                        WaterHabitButton(
                            isCompleted = habitFlowerDetail.isCompletedToday,
                            isLoading = uiState.showWateringAnimation,
                            onClick = {
                                viewModel.handleUiEvent(HabitFlowerDetailUiEvent.WaterTodaysHabit)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 7-day completion history
                        CompletionHistorySection(
                            completions = habitFlowerDetail.lastSevenDaysCompletions
                        )

                        // Habit details section
                        HabitDetailSection(
                            description = habitFlowerDetail.description,
                            startDate = habitFlowerDetail.startDate,
                            repeatDays = habitFlowerDetail.repeatDays,
                            reminderTime = habitFlowerDetail.reminderTime,
                            onEditClick = {
                                viewModel.handleUiEvent(
                                    HabitFlowerDetailUiEvent.NavigateToEditHabit(habitFlowerDetail.habitId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
} 