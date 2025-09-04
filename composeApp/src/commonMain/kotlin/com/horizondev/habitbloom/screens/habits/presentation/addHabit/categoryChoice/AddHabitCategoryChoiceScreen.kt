package com.horizondev.habitbloom.screens.habits.presentation.addHabit.categoryChoice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.components.BloomLoader
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.categoryChoice.component.CategoryCard
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.habit_category_subtitle
import habitbloom.composeapp.generated.resources.habit_category_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen for selecting the time of day for a habit.
 */
@Composable
fun AddHabitCategoryChoiceScreen(
    onCategorySelected: () -> Unit,
    onBack: () -> Unit
) {
    // Create ViewModel using Koin
    val viewModel = koinViewModel<AddHabitCategoryChoiceViewModel>()

    // Collect state
    val uiState by viewModel.state.collectAsState()

    // Handle UI intents
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                is AddHabitCategoryUiIntent.NavigateToHabitChoice -> {
                    onCategorySelected()
                }

                AddHabitCategoryUiIntent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    // Render content
    AddHabitCategoryChoiceScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

/**
 * Content for the time of day choice screen.
 */
@Composable
fun AddHabitCategoryChoiceScreenContent(
    uiState: AddHabitCategoryUiState,
    handleUiEvent: (AddHabitCategoryUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(all = 16.dp)
        ) {
            item(key = "title") {
                Text(
                    text = stringResource(Res.string.habit_category_title),
                    style = BloomTheme.typography.headlineLarge,
                    color = BloomTheme.colors.foreground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item(key = "subtitle") {
                Text(
                    text = stringResource(Res.string.habit_category_subtitle),
                    style = BloomTheme.typography.bodyLarge,
                    color = BloomTheme.colors.mutedForeground,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(
                items = uiState.categories,
                key = { it.id }
            ) { category ->
                CategoryCard(category = category, onClick = {
                    handleUiEvent(AddHabitCategoryUiEvent.SelectCategory(category))
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        BloomLoader(modifier = Modifier.align(Alignment.Center), isLoading = uiState.isLoading)
    }
}