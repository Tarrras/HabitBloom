package com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.components.BloomLoader
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.getRangeLabel
import com.horizondev.habitbloom.utils.formatDateRange
import com.horizondev.habitbloom.utils.formatTime
import com.horizondev.habitbloom.utils.getIcon
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_habit_summary_cta_message
import habitbloom.composeapp.generated.resources.add_habit_summary_cta_title
import habitbloom.composeapp.generated.resources.add_habit_summary_subtitle
import habitbloom.composeapp.generated.resources.add_habit_summary_value_not_set
import habitbloom.composeapp.generated.resources.back
import habitbloom.composeapp.generated.resources.complete
import habitbloom.composeapp.generated.resources.habit_schedule
import habitbloom.composeapp.generated.resources.ic_lucid_calendar
import habitbloom.composeapp.generated.resources.ic_lucid_clock
import habitbloom.composeapp.generated.resources.ic_lucid_repeat
import habitbloom.composeapp.generated.resources.ic_lucid_tag
import habitbloom.composeapp.generated.resources.no_reminder_set
import habitbloom.composeapp.generated.resources.period
import habitbloom.composeapp.generated.resources.reminder
import habitbloom.composeapp.generated.resources.reminder_disabled
import habitbloom.composeapp.generated.resources.reminder_set_for
import habitbloom.composeapp.generated.resources.summary
import habitbloom.composeapp.generated.resources.time_of_day
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AddHabitSummaryScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    showSnackbar: (BloomSnackbarVisuals) -> Unit
) {
    val viewModel = koinViewModel<AddHabitSummaryViewModel>()

    val uiState by viewModel.state.collectAsState()

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SummaryHeader()
            SummaryHabitCard(uiState = uiState)
            SummaryEncouragementCard()
            SummaryActions(handleUiEvent = handleUiEvent)
        }

        BloomLoader(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun SummaryHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.summary),
            style = BloomTheme.typography.headlineLarge,
            color = BloomTheme.colors.textColor.primary
        )
        Text(
            text = stringResource(Res.string.add_habit_summary_subtitle),
            style = BloomTheme.typography.bodyMedium,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

@Composable
private fun SummaryHabitCard(
    modifier: Modifier = Modifier,
    uiState: AddHabitSummaryUiState
) {
    val habitInfo = uiState.habitInfo
    val timeOfDayValue = uiState.timeOfDay?.let {
        "${it.getTitle()} (${it.getRangeLabel()})"
    } ?: stringResource(Res.string.add_habit_summary_value_not_set)

    val scheduleValue = if (uiState.days.isEmpty()) {
        stringResource(Res.string.add_habit_summary_value_not_set)
    } else {
        uiState.days.map { it.getShortTitle() }.joinToString(separator = ", ") { it }
    }

    val periodValue = if (uiState.startDate != null && uiState.endDate != null) {
        formatDateRange(uiState.startDate, uiState.endDate)
    } else {
        stringResource(Res.string.add_habit_summary_value_not_set)
    }

    val reminderValue = when {
        uiState.reminderEnabled && uiState.reminderTime != null -> {
            stringResource(
                Res.string.reminder_set_for,
                formatTime(uiState.reminderTime, use24HourFormat = true)
            )
        }

        uiState.reminderEnabled -> stringResource(Res.string.no_reminder_set)
        else -> stringResource(Res.string.reminder_disabled)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloomNetworkImage(
                    size = 48.dp,
                    shape = RoundedCornerShape(16.dp),
                    iconUrl = habitInfo?.iconUrl.orEmpty(),
                    contentDescription = habitInfo?.name
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = habitInfo?.name.orEmpty(),
                        style = BloomTheme.typography.headlineMedium,
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    uiState.habitCategory?.title?.takeIf { it.isNotBlank() }?.let { categoryTitle ->
                        SummaryCategoryChip(title = categoryTitle)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                uiState.timeOfDay?.getIcon()?.let {
                    SummaryInfoRow(
                        icon = it,
                        label = stringResource(Res.string.time_of_day),
                        value = timeOfDayValue
                    )
                }
                SummaryInfoRow(
                    icon = painterResource(Res.drawable.ic_lucid_repeat),
                    label = stringResource(Res.string.habit_schedule),
                    value = scheduleValue
                )
                SummaryInfoRow(
                    icon = painterResource(Res.drawable.ic_lucid_calendar),
                    label = stringResource(Res.string.period),
                    value = periodValue
                )
                SummaryInfoRow(
                    icon = painterResource(Res.drawable.ic_lucid_clock),
                    label = stringResource(Res.string.reminder),
                    value = reminderValue
                )
            }
        }
    }
}

@Composable
private fun SummaryCategoryChip(title: String) {
    Surface(
        color = BloomTheme.colors.secondary,
        contentColor = BloomTheme.colors.secondaryForeground,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_lucid_tag),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = BloomTheme.colors.primary
            )
            Text(
                text = title,
                style = BloomTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun SummaryInfoRow(
    icon: Painter,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = BloomTheme.colors.cardSecondary,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = BloomTheme.colors.primary
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary
            )
            Text(
                text = value,
                style = BloomTheme.typography.titleMedium,
                color = BloomTheme.colors.textColor.primary
            )
        }
    }
}

@Composable
private fun SummaryEncouragementCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.add_habit_summary_cta_title),
                style = BloomTheme.typography.titleMedium,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(Res.string.add_habit_summary_cta_message),
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary
            )
        }
    }
}

@Composable
private fun SummaryActions(handleUiEvent: (AddHabitSummaryUiEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BloomPrimaryFilledButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.complete),
            onClick = { handleUiEvent(AddHabitSummaryUiEvent.Confirm) },
        )
        BloomPrimaryOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.back),
            onClick = { handleUiEvent(AddHabitSummaryUiEvent.BackPressed) },
        )
    }
}

