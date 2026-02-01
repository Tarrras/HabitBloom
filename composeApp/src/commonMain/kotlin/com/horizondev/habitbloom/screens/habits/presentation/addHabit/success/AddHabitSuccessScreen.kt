package com.horizondev.habitbloom.screens.habits.presentation.addHabit.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.designSystem.BloomThemePreview
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_habit_success_add_another
import habitbloom.composeapp.generated.resources.add_habit_success_habit_name_placeholder
import habitbloom.composeapp.generated.resources.add_habit_success_message
import habitbloom.composeapp.generated.resources.add_habit_success_primary_action
import habitbloom.composeapp.generated.resources.add_habit_success_tip_message
import habitbloom.composeapp.generated.resources.add_habit_success_tip_title
import habitbloom.composeapp.generated.resources.add_habit_success_title
import habitbloom.composeapp.generated.resources.habit_added_success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen shown after successfully adding a habit.
 */
@Composable
fun AddHabitSuccessScreen(
    onFinish: () -> Unit,
    onAddAnother: () -> Unit
) {
    val viewModel = koinViewModel<AddHabitSuccessViewModel>()

    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                AddHabitSuccessUiIntent.FinishFlow -> {
                    onFinish()
                }
                AddHabitSuccessUiIntent.AddAnotherHabit -> {
                    onAddAnother()
                }
            }
        }
    }

    // Render content
    AddHabitSuccessScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun AddHabitSuccessScreenContent(
    uiState: AddHabitSuccessUiState,
    handleUiEvent: (AddHabitSuccessUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.habit_added_success),
                    contentDescription = stringResource(Res.string.add_habit_success_title),
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = stringResource(Res.string.add_habit_success_title),
                    style = BloomTheme.typography.displaySmall,
                    color = BloomTheme.colors.textColor.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(Res.string.add_habit_success_message),
                    style = BloomTheme.typography.bodyMedium,
                    color = BloomTheme.colors.textColor.secondary,
                    textAlign = TextAlign.Center
                )
                val habitName = uiState.habitName?.trim().orEmpty()
                val habitNameDisplay = if (habitName.isNotBlank()) {
                    "\"$habitName\""
                } else {
                    stringResource(Res.string.add_habit_success_habit_name_placeholder)
                }
                Text(
                    text = habitNameDisplay,
                    style = BloomTheme.typography.headlineMedium,
                    color = BloomTheme.colors.accentTealLight,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            SuccessTipCard()
            Spacer(modifier = Modifier.height(48.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BloomPrimaryFilledButton(
                    onClick = { handleUiEvent(AddHabitSuccessUiEvent.FinishFlow) },
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.add_habit_success_primary_action),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = BloomTheme.colors.textColor.white
                        )
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                )
                BloomPrimaryOutlinedButton(
                    onClick = { handleUiEvent(AddHabitSuccessUiEvent.AddAnotherHabit) },
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.add_habit_success_add_another),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = BloomTheme.colors.primary
                        )
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun SuccessTipCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BloomTheme.colors.glassBackground,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✨ " + stringResource(Res.string.add_habit_success_tip_title),
                    style = BloomTheme.typography.titleMedium,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = stringResource(Res.string.add_habit_success_tip_message),
                style = BloomTheme.typography.bodySmall,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
fun AddHabitSuccessScreenContentPreview() {
    BloomThemePreview {
        AddHabitSuccessScreenContent(
            uiState = AddHabitSuccessUiState(habitName = "New habit"),
            handleUiEvent = {}
        )
    }
}