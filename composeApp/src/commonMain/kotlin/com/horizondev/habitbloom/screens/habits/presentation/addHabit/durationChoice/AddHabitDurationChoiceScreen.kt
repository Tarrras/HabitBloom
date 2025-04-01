package com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.dialogs.SimpleDateRangePickerDialog
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DragSelectDayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.pickers.TimePicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.formatToMmDdYyWithLocale
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.choose_habit_days_and_duration
import habitbloom.composeapp.generated.resources.drag_to_select_multiple
import habitbloom.composeapp.generated.resources.enable_reminder
import habitbloom.composeapp.generated.resources.end_date_colon
import habitbloom.composeapp.generated.resources.ends_around
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.four_repeats
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.one_week
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.reminder_settings
import habitbloom.composeapp.generated.resources.repeats_count
import habitbloom.composeapp.generated.resources.select_days_for_habit
import habitbloom.composeapp.generated.resources.select_duration_for_habit
import habitbloom.composeapp.generated.resources.select_reminder_time
import habitbloom.composeapp.generated.resources.selected_repeats
import habitbloom.composeapp.generated.resources.start_date_colon
import habitbloom.composeapp.generated.resources.twelve_repeats
import habitbloom.composeapp.generated.resources.two_months
import habitbloom.composeapp.generated.resources.weekdays
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
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
            activeDays = uiState.activeDays,
            onDaysChanged = { dayOfWeek, isActive ->
                handleUiEvent(AddHabitDurationUiEvent.UpdateDayState(dayOfWeek, isActive))
            },
            onGroupChanged = { days ->
                handleUiEvent(AddHabitDurationUiEvent.SelectGroupOfDays(days))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectDurationForHabitCard(
            modifier = Modifier.fillMaxWidth(),
            startDate = uiState.startDate,
            activeDays = uiState.activeDays,
            onDaysChanged = { days ->
                handleUiEvent(AddHabitDurationUiEvent.SelectGroupOfDays(days))
            },
            onStartDateChanged = { date ->
                handleUiEvent(AddHabitDurationUiEvent.StartDateChanged(date))
            },
            onDurationChanged = { duration ->
                handleUiEvent(AddHabitDurationUiEvent.DurationChanged(duration))
            },
            duration = uiState.durationInDays
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

            Spacer(modifier = Modifier.height(8.dp))

            // Hint text for drag selection
            Text(
                text = stringResource(Res.string.drag_to_select_multiple),
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary,
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
private fun SelectDurationForHabitCard(
    modifier: Modifier = Modifier,
    startDate: LocalDate?,
    activeDays: List<DayOfWeek>,
    onDaysChanged: (List<DayOfWeek>) -> Unit,
    onStartDateChanged: (LocalDate) -> Unit,
    onDurationChanged: (Int) -> Unit,
    duration: Int = 4,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val currentDate = remember { getCurrentDate() }
    val effectiveStartDate = startDate ?: currentDate

    val endDate = remember(effectiveStartDate, duration) {
        calculateEndDate(effectiveStartDate, duration)
    }

    // Define common durations with more meaningful labels
    val durations = listOf(
        DurationOption(1, stringResource(Res.string.one_week)),
        DurationOption(4, stringResource(Res.string.four_repeats)),
        DurationOption(8, stringResource(Res.string.two_months)),
        DurationOption(12, stringResource(Res.string.twelve_repeats)),
    )

    BloomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* No action */ },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.select_duration_for_habit),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Duration Selector
            VisualDurationSelector(
                durations = durations,
                selectedDuration = duration,
                onDurationSelected = onDurationChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date information with clickable behavior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = BloomTheme.colors.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showDatePicker = true }
                    .padding(16.dp)
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
                                text = formatLocalDate(effectiveStartDate),
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
                                text = formatLocalDate(endDate),
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Duration badge
                    Surface(
                        color = BloomTheme.colors.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.repeats_count, duration),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.ends_around, formatLocalDate(endDate)),
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary,
            )
        }
    }

    // Use the simplified date picker dialog
    if (showDatePicker) {
        SimpleDateRangePickerDialog(
            startDate = effectiveStartDate,
            onDatesSelected = { start, _ ->
                onStartDateChanged(start)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            minDate = getCurrentDate()
        )
    }
}

// Helper data class for duration options
data class DurationOption(
    val value: Int,
    val label: String
)

// Helper function to calculate end date
private fun calculateEndDate(startDate: LocalDate, durationInRepeats: Int): LocalDate {
    return startDate.plus(durationInRepeats * 7L, DateTimeUnit.DAY)
}

// Helper extension to safely format LocalDate with locale
private fun formatLocalDate(date: LocalDate): String {
    return "${date.monthNumber}/${date.dayOfMonth}/${date.year}"
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

/**
 * Visual representation of duration with chips and calendar-based explanation
 */
@Composable
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
            val formattedDate =
                calculateApproximateEndDate(selectedDuration).formatToMmDdYyWithLocale()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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

                Text(
                    text = stringResource(Res.string.ends_around, formattedDate),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )
            }
        } else {
            Text(
                text = pluralStringResource(
                    resource = Res.plurals.selected_repeats,
                    quantity = selectedDuration,
                    selectedDuration
                ),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
            )
        }
    }
}

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

/**
 * Helper function to calculate approximate end date based on duration and selected days
 */
private fun calculateApproximateEndDate(duration: Int): LocalDate {
    // Simple approximation: each repeat is roughly a week
    val daysToAdd = duration * 7
    return getCurrentDate().plus(daysToAdd, DateTimeUnit.DAY)
}

