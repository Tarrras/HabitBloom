package com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.AddHabitFlowState
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.back
import habitbloom.composeapp.generated.resources.complete
import habitbloom.composeapp.generated.resources.do_you_want_add_this_habit
import habitbloom.composeapp.generated.resources.duration_display
import habitbloom.composeapp.generated.resources.end_date_display
import habitbloom.composeapp.generated.resources.reminder_set_for
import habitbloom.composeapp.generated.resources.start_date_display
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddHabitSummaryScreen(
    hostState: AddHabitFlowState,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    showSnackbar: (BloomSnackbarVisuals) -> Unit,
    modifier: Modifier = Modifier
) {

    // Create ViewModel using Koin
    val viewModel = koinViewModel<AddHabitSummaryViewModel> {
        parametersOf(hostState)
    }

    // Collect state and setup UI
    val uiState by viewModel.state.collectAsState()

    // Handle UI intents from ViewModel
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                is AddHabitSummaryUiIntent.ShowSnackBar -> {
                    showSnackbar(
                        uiIntent.visuals
                    )
                }

                AddHabitSummaryUiIntent.NavigateToSuccess -> {
                    onSuccess()
                }

                AddHabitSummaryUiIntent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    // UI Content
    AddHabitSummaryContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun AddHabitSummaryContent(
    uiState: AddHabitSummaryUiState,
    handleUiEvent: (AddHabitSummaryUiEvent) -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.do_you_want_add_this_habit),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            SummaryHabitCard(uiState = uiState, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.complete),
                onClick = {
                    handleUiEvent(AddHabitSummaryUiEvent.Confirm)
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomPrimaryOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.back),
                onClick = {
                    handleUiEvent(AddHabitSummaryUiEvent.BackPressed)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        BloomLoader(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun SummaryHabitCard(
    modifier: Modifier = Modifier,
    uiState: AddHabitSummaryUiState
) {
    val habitInfo = uiState.habitInfo
    Surface(
        modifier = modifier,
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BloomNetworkImage(
                    iconUrl = habitInfo.iconUrl,
                    contentDescription = habitInfo.name
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = habitInfo.name,
                        style = BloomTheme.typography.heading,
                        color = BloomTheme.colors.textColor.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habitInfo.description,
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }

            DayPicker(
                modifier = Modifier.fillMaxWidth(),
                activeDays = uiState.days,
                dayStateChanged = { _, _ -> },
                enabled = false
            )

            // Date information section
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Start date
                Text(
                    text = stringResource(
                        Res.string.start_date_display,
                        uiState.startDate.toString()
                    ),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary
                )

                // End date
                Text(
                    text = stringResource(Res.string.end_date_display, uiState.endDate.toString()),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary
                )

                // Duration in days
                Text(
                    text = stringResource(Res.string.duration_display, uiState.durationInDays),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.primary
                )
            }

            // Reminder information
            if (uiState.reminderEnabled && uiState.reminderTime != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Reminder",
                        tint = BloomTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(
                            Res.string.reminder_set_for,
                            formatTime(uiState.reminderTime)
                        ),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary
                    )
                }
            }
        }
    }
}

private fun formatTime(time: LocalTime): String {
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}


