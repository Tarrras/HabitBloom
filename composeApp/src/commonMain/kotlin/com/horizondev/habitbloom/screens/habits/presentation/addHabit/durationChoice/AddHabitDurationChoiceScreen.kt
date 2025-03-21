package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.pickers.SingleWeekStartOptionPicker
import com.horizondev.habitbloom.core.designComponents.pickers.TimePicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.GroupOfDays
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.choose_habit_days_and_duration
import habitbloom.composeapp.generated.resources.enable_reminder
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.four_repeats
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.one_repeat
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.quick_action
import habitbloom.composeapp.generated.resources.reminder_settings
import habitbloom.composeapp.generated.resources.select_days_for_habit
import habitbloom.composeapp.generated.resources.select_reminder_time
import habitbloom.composeapp.generated.resources.select_repeats
import habitbloom.composeapp.generated.resources.selected_repeats
import habitbloom.composeapp.generated.resources.start_date
import habitbloom.composeapp.generated.resources.twelve_repeats
import habitbloom.composeapp.generated.resources.when_do_you_want_to_start
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt


@Composable
fun AddHabitDurationChoiceScreen(
    onDurationSelected: (Int, LocalDate, List<DayOfWeek>, HabitWeekStartOption, Boolean, LocalTime) -> Unit,
    onBack: () -> Unit,
    showSnackbar: (BloomSnackbarVisuals) -> Unit,
    modifier: Modifier = Modifier
) {
    // Create ViewModel using Koin
    val viewModel = koinViewModel<AddHabitDurationViewModel>()

    // Collect state and setup UI
    val uiState by viewModel.state.collectAsState()

    // Handle UI intents from ViewModel
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                is AddHabitDurationUiIntent.NavigateNext -> {
                    onDurationSelected(
                        uiIntent.durationInDays,
                        uiIntent.startDate,
                        uiIntent.selectedDays,
                        uiIntent.weekStartOption,
                        uiIntent.reminderEnabled,
                        uiIntent.reminderTime
                    )
                }

                AddHabitDurationUiIntent.NavigateBack -> {
                    onBack()
                }

                is AddHabitDurationUiIntent.ShowValidationError -> {
                    showSnackbar(uiIntent.visuals)
                }
            }
        }
    }

    // UI Content
    AddHabitDurationChoiceScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
        modifier = modifier
    )
}

@Composable
private fun AddHabitDurationChoiceScreenContent(
    uiState: AddHabitDurationUiState,
    handleUiEvent: (AddHabitDurationUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
            text = stringResource(Res.string.choose_habit_days_and_duration),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))

        SelectDaysForHabitCard(
            modifier = Modifier.fillMaxWidth(),
            startDate = uiState.displayedStartDate,
            activeDays = uiState.activeDays,
            weekStartOption = uiState.weekStartOption,
            dayStateChanged = { day, isActive ->
                handleUiEvent(AddHabitDurationUiEvent.UpdateDayState(day, isActive))
            },
            selectGroupOfDays = {
                handleUiEvent(AddHabitDurationUiEvent.SelectGroupOfDays(it))
            },
            onOptionSelected = {
                handleUiEvent(AddHabitDurationUiEvent.SelectWeekStartOption(it))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectDurationForHabitCard(
            modifier = Modifier.fillMaxWidth(),
            duration = uiState.durationInDays,
            onDurationChanged = {
                handleUiEvent(AddHabitDurationUiEvent.DurationChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReminderSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            reminderEnabled = uiState.reminderEnabled,
            reminderTime = uiState.reminderTime,
            onReminderEnabledChanged = { enabled ->
                handleUiEvent(AddHabitDurationUiEvent.ReminderEnabledChanged(enabled))
            },
            onReminderTimeChanged = { time ->
                handleUiEvent(AddHabitDurationUiEvent.ReminderTimeChanged(time))
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        BloomPrimaryFilledButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.next),
            onClick = {
                handleUiEvent(AddHabitDurationUiEvent.OnNext)
            },
            enabled = uiState.displayedStartDate != null
        )

        Spacer(modifier = Modifier.height(12.dp))

        BloomPrimaryOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.cancel),
            onClick = {
                handleUiEvent(AddHabitDurationUiEvent.Cancel)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SelectDaysForHabitCard(
    modifier: Modifier = Modifier,
    startDate: String?,
    activeDays: List<DayOfWeek>,
    weekStartOption: HabitWeekStartOption,
    dayStateChanged: (DayOfWeek, Boolean) -> Unit,
    selectGroupOfDays: (GroupOfDays) -> Unit,
    onOptionSelected: (HabitWeekStartOption) -> Unit
) {
    Surface(
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(Res.string.select_days_for_habit),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            DayPicker(
                modifier = Modifier.fillMaxWidth(),
                activeDays = activeDays,
                dayStateChanged = dayStateChanged
            )
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                startDate != null,
            ) {
                Text(
                    text = stringResource(Res.string.start_date, startDate.orEmpty()),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.when_do_you_want_to_start),
                color = BloomTheme.colors.textColor.secondary,
                style = BloomTheme.typography.subheading
            )
            Spacer(modifier = Modifier.height(12.dp))
            SingleWeekStartOptionPicker(
                modifier = Modifier,
                onOptionSelected = onOptionSelected,
                selectedOption = weekStartOption,
                options = HabitWeekStartOption.entries
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.quick_action),
                color = BloomTheme.colors.textColor.secondary,
                style = BloomTheme.typography.subheading
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomSmallActionButton(
                text = stringResource(Res.string.every_day),
                onClick = {
                    selectGroupOfDays(GroupOfDays.EVERY_DAY)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomSmallActionButton(
                text = stringResource(Res.string.only_weekends),
                onClick = {
                    selectGroupOfDays(GroupOfDays.WEEKENDS)
                }
            )
        }
    }
}

@Composable
private fun ReminderSettingsCard(
    modifier: Modifier = Modifier,
    reminderEnabled: Boolean,
    reminderTime: LocalTime,
    onReminderEnabledChanged: (Boolean) -> Unit,
    onReminderTimeChanged: (LocalTime) -> Unit
) {
    Surface(
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(Res.string.reminder_settings),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.enable_reminder),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    modifier = Modifier.weight(1f)
                )

                BloomSwitch(
                    checked = reminderEnabled,
                    onCheckedChange = onReminderEnabledChanged
                )
            }

            AnimatedVisibility(visible = reminderEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.select_reminder_time),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TimePicker(
                        time = reminderTime,
                        onTimeSelected = onReminderTimeChanged,
                        use24HourFormat = true
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectDurationForHabitCard(
    modifier: Modifier = Modifier,
    duration: Int,
    onDurationChanged: (Int) -> Unit
) {
    Surface(
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(Res.string.select_repeats),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            DurationSlider(
                duration = duration,
                onDurationChanged = onDurationChanged,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.quick_action),
                color = BloomTheme.colors.textColor.secondary,
                style = BloomTheme.typography.subheading
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomSmallActionButton(
                text = stringResource(Res.string.one_repeat),
                onClick = {
                    onDurationChanged(1)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomSmallActionButton(
                text = stringResource(Res.string.four_repeats),
                onClick = {
                    onDurationChanged(4)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomSmallActionButton(
                text = stringResource(Res.string.twelve_repeats),
                onClick = {
                    onDurationChanged(12)
                }
            )
        }
    }
}

@Composable
fun DurationSlider(
    modifier: Modifier = Modifier,
    duration: Int,
    onDurationChanged: (Int) -> Unit,
    enabled: Boolean = true
) {
    Column {
        BloomSlider(
            value = duration.toFloat(),
            onValueChange = { newValue -> onDurationChanged(newValue.roundToInt()) },
            valueRange = 1f..12f, // Set the range from 1 to 12
            steps = 11, // 11 steps because we start from 1
            modifier = modifier,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = pluralStringResource(
                resource = Res.plurals.selected_repeats,
                quantity = duration,
                duration
            ),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.primary,
            textDecoration = TextDecoration.Underline,
        )
    }
}

