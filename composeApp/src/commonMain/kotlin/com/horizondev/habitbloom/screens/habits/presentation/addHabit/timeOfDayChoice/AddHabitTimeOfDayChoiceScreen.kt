package com.horizondev.habitbloom.screens.habits.presentation.addHabit.timeOfDayChoice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.timeOfDayChoice.components.HabitCategoryCard
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.choose_habit_category
import habitbloom.composeapp.generated.resources.evening_reflection_2
import habitbloom.composeapp.generated.resources.evening_reflection_description
import habitbloom.composeapp.generated.resources.evening_reflection_title
import habitbloom.composeapp.generated.resources.midday_focus
import habitbloom.composeapp.generated.resources.midday_focus_description
import habitbloom.composeapp.generated.resources.midday_focus_title
import habitbloom.composeapp.generated.resources.morning_habit_description
import habitbloom.composeapp.generated.resources.morning_habit_title
import habitbloom.composeapp.generated.resources.morning_routine
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen for selecting the time of day for a habit.
 */
@Composable
fun AddHabitTimeOfDayChoiceScreen(
    onTimeOfDaySelected: (TimeOfDay) -> Unit,
    onBack: () -> Unit
) {
    // Create ViewModel using Koin
    val viewModel = koinViewModel<AddHabitTimeOfDayViewModel>()

    // Collect state
    val uiState by viewModel.state.collectAsState()

    // Handle UI intents
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                is AddHabitTimeOfDayUiIntent.NavigateWithTimeOfDay -> {
                    onTimeOfDaySelected(uiIntent.timeOfDay)
                }

                AddHabitTimeOfDayUiIntent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    // Render content
    AddHabitTimeOfDayChoiceScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

/**
 * Content for the time of day choice screen.
 */
@Composable
fun AddHabitTimeOfDayChoiceScreenContent(
    uiState: AddHabitTimeOfDayUiState,
    handleUiEvent: (AddHabitTimeOfDayUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.choose_habit_category),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        HabitCategoryCard(
            title = stringResource(Res.string.morning_habit_title),
            description = stringResource(Res.string.morning_habit_description),
            imageRes = painterResource(Res.drawable.morning_routine),
            onClick = { handleUiEvent(AddHabitTimeOfDayUiEvent.SelectTimeOfDay(TimeOfDay.Morning)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HabitCategoryCard(
            title = stringResource(Res.string.midday_focus_title),
            description = stringResource(Res.string.midday_focus_description),
            imageRes = painterResource(Res.drawable.midday_focus),
            onClick = { handleUiEvent(AddHabitTimeOfDayUiEvent.SelectTimeOfDay(TimeOfDay.Afternoon)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HabitCategoryCard(
            title = stringResource(Res.string.evening_reflection_title),
            description = stringResource(Res.string.evening_reflection_description),
            imageRes = painterResource(Res.drawable.evening_reflection_2),
            onClick = { handleUiEvent(AddHabitTimeOfDayUiEvent.SelectTimeOfDay(TimeOfDay.Evening)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}