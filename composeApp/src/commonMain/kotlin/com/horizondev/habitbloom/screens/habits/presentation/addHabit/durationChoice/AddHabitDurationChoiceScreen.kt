package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.dialogs.AddHabitDateRangePickerDialog
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DragSelectDayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.TimePicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.formatToMmDdYyWithLocale
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.choose_habit_days_and_duration
import habitbloom.composeapp.generated.resources.custom_date_range
import habitbloom.composeapp.generated.resources.days_count
import habitbloom.composeapp.generated.resources.enable_reminder
import habitbloom.composeapp.generated.resources.end_date_colon
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.one_month
import habitbloom.composeapp.generated.resources.one_week
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.quick_selection
import habitbloom.composeapp.generated.resources.reminder_settings
import habitbloom.composeapp.generated.resources.select_date_range_for_habit
import habitbloom.composeapp.generated.resources.select_days_first
import habitbloom.composeapp.generated.resources.select_days_for_habit
import habitbloom.composeapp.generated.resources.select_reminder_time
import habitbloom.composeapp.generated.resources.selected_repeats
import habitbloom.composeapp.generated.resources.start_date_colon
import habitbloom.composeapp.generated.resources.tap_to_edit_dates
import habitbloom.composeapp.generated.resources.three_months
import habitbloom.composeapp.generated.resources.weekdays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt


@Composable
fun AddHabitDurationChoiceScreen(
    onDurationSelected: (LocalDate, LocalDate, List<DayOfWeek>, Int, Boolean, LocalTime) -> Unit,
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
                        uiIntent.startDate,
                        uiIntent.endDate,
                        uiIntent.selectedDays,
                        uiIntent.durationInDays,
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
            activeDays = uiState.activeDays,
            onDaysChanged = { dayOfWeek, isActive ->
                handleUiEvent(AddHabitDurationUiEvent.UpdateDayState(dayOfWeek, isActive))
            },
            onGroupChanged = { days ->
                handleUiEvent(AddHabitDurationUiEvent.SelectGroupOfDays(days))
            }
        )

        AnimatedVisibility(
            uiState.activeDays.isNotEmpty(),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            SelectDateRangeForHabitCard(
                modifier = Modifier.fillMaxWidth(),
                uiState = uiState,
                onDaysChanged = { days ->
                    handleUiEvent(AddHabitDurationUiEvent.SelectGroupOfDays(days))
                },
                handleUiEvent = handleUiEvent,
                isDatePickerVisible = uiState.isDatePickerVisible
            )
        }

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
            enabled = uiState.nextButtonEnabled
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
    activeDays: List<DayOfWeek>,
    onDaysChanged: (DayOfWeek, Boolean) -> Unit,
    onGroupChanged: (List<DayOfWeek>) -> Unit
) {
    BloomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = {}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.select_days_for_habit),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Use drag select day picker
            DragSelectDayPicker(
                activeDays = activeDays,
                onDaysChanged = onGroupChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add preset buttons in a row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Everyday button
                BloomSmallActionButton(
                    text = stringResource(Res.string.every_day),
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        onGroupChanged(DayOfWeek.entries)
                    }
                )

                // Weekdays button
                BloomSmallActionButton(
                    text = stringResource(Res.string.weekdays),
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        val weekdays = listOf(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY
                        )
                        onGroupChanged(weekdays)
                    }
                )

                // Weekends button
                BloomSmallActionButton(
                    text = stringResource(Res.string.only_weekends),
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        val weekends = listOf(
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY
                        )
                        onGroupChanged(weekends)
                    }
                )
            }
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
private fun SelectDateRangeForHabitCard(
    modifier: Modifier = Modifier,
    uiState: AddHabitDurationUiState,
    onDaysChanged: (List<DayOfWeek>) -> Unit,
    handleUiEvent: (AddHabitDurationUiEvent) -> Unit,
    isDatePickerVisible: Boolean = false
) {
    val activeDays = uiState.activeDays
    val startDate = uiState.startDate
    val endDate = uiState.endDate
    val maxDurationDays = uiState.maxHabitDurationDays
    val durationInDays = uiState.durationInDays

    val currentDate = remember { getCurrentDate() }

    // Check if any days are selected
    val hasSelectedDays = activeDays.isNotEmpty()

    // Define preset date ranges
    val presetRanges = listOf(
        PresetRange(7, stringResource(Res.string.one_week)),
        PresetRange(30, stringResource(Res.string.one_month)),
        PresetRange(90, stringResource(Res.string.three_months))
    )

    BloomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* No action */ },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.select_date_range_for_habit),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary
            )

            if (!hasSelectedDays) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.select_days_first),
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom date range selection
            Text(
                text = stringResource(Res.string.custom_date_range),
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date information with clickable behavior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (hasSelectedDays) {
                            Modifier.clickable {
                                handleUiEvent(AddHabitDurationUiEvent.SetDatePickerVisibility(true))
                            }
                        } else {
                            Modifier.alpha(0.6f)
                        }
                    )
                    .padding(vertical = 16.dp)
            ) {
                // Date range display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Start date
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.start_date_colon),
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.secondary,
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (hasSelectedDays && startDate != null) startDate.formatToMmDdYyWithLocale() else "—",
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // End date
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.end_date_colon),
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.secondary,
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (hasSelectedDays && endDate != null) endDate.formatToMmDdYyWithLocale() else "—",
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Days count badge (smaller screen display)
                        if (hasSelectedDays && durationInDays > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = BloomTheme.colors.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = pluralStringResource(
                                            Res.plurals.days_count,
                                            durationInDays,
                                            durationInDays
                                        ),
                                        style = BloomTheme.typography.small,
                                        color = BloomTheme.colors.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Tap to edit hint
                                if (hasSelectedDays) {
                                    Text(
                                        text = stringResource(Res.string.tap_to_edit_dates),
                                        style = BloomTheme.typography.small,
                                        color = BloomTheme.colors.primary,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        } else if (hasSelectedDays) {
                            // Just the tap to edit hint if no days count
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(Res.string.tap_to_edit_dates),
                                    style = BloomTheme.typography.small,
                                    color = BloomTheme.colors.primary,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }

            }


            Spacer(modifier = Modifier.height(16.dp))

            // Preset date range options
            Text(
                text = stringResource(Res.string.quick_selection),
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preset range chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                presetRanges.forEach { preset ->
                    DateRangeChip(
                        text = preset.label,
                        isSelected = hasSelectedDays && durationInDays == preset.days,
                        onClick = {
                            if (hasSelectedDays) {
                                handleUiEvent(AddHabitDurationUiEvent.SelectPresetDateRange(preset.days))
                            }
                        },
                        enabled = hasSelectedDays
                    )
                }
            }
        }
    }

    // Use the simplified date picker dialog with both start and end dates
    if (isDatePickerVisible && hasSelectedDays) {
        AddHabitDateRangePickerDialog(
            startDate = startDate,
            endDate = endDate,
            maxDurationDays = maxDurationDays,
            onDatesSelected = { start, end ->
                handleUiEvent(AddHabitDurationUiEvent.DateRangeChanged(start, end))
                handleUiEvent(AddHabitDurationUiEvent.SetDatePickerVisibility(false))
            },
            onDismiss = {
                handleUiEvent(AddHabitDurationUiEvent.SetDatePickerVisibility(false))
            },
            minDate = currentDate
        )
    }
}

@Composable
private fun DateRangeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val backgroundColor = if (isSelected) {
        BloomTheme.colors.primary
    } else {
        BloomTheme.colors.surface
    }

    val textColor = if (isSelected) {
        BloomTheme.colors.textColor.white
    } else {
        BloomTheme.colors.textColor.primary
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = if (!isSelected) BorderStroke(1.dp, BloomTheme.colors.background) else null,
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.body,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// Data class for preset date ranges
private data class PresetRange(
    val days: Int,
    val label: String
)

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

/**
 * Visual representation of duration with chips and calendar-based explanation
 */
/*@Composable
fun VisualDurationSelector(
    modifier: Modifier = Modifier,
    durations: List<DurationOption>,
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit
) {
    Column(modifier = modifier) {
        // Duration chips in a wrapped flow layout
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            durations.forEach { option ->
                DurationChip(
                    text = option.label,
                    isSelected = selectedDuration == option.value,
                    onClick = { onDurationSelected(option.value) },
                    enabled = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Regular slider for fine-tuning
        BloomSlider(
            value = selectedDuration.toFloat(),
            onValueChange = { newValue -> onDurationSelected(newValue.roundToInt()) },
            valueRange = 1f..12f,
            steps = 11,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Show current duration with completion date
        if (selectedDuration > 0) {
            Text(
                text = pluralStringResource(
                    resource = Res.plurals.selected_repeats,
                    quantity = selectedDuration,
                    selectedDuration
                ),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}*/

@Composable
private fun DurationChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        color = if (isSelected) BloomTheme.colors.primary else BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) BloomTheme.colors.primary else BloomTheme.colors.surface
        ),
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.body,
            color = if (isSelected) BloomTheme.colors.textColor.white else BloomTheme.colors.textColor.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


