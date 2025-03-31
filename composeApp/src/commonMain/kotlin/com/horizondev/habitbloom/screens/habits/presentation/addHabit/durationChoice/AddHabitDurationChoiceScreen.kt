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
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.pickers.TimePicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.choose_habit_days_and_duration
import habitbloom.composeapp.generated.resources.enable_reminder
import habitbloom.composeapp.generated.resources.ends_around
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.four_repeats
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.one_week
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.reminder_settings
import habitbloom.composeapp.generated.resources.select_days_for_habit
import habitbloom.composeapp.generated.resources.select_reminder_time
import habitbloom.composeapp.generated.resources.select_repeats
import habitbloom.composeapp.generated.resources.selected_repeats
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
            duration = uiState.durationInDays,
            onDurationChanged = {
                handleUiEvent(AddHabitDurationUiEvent.DurationChanged(it))
            },
            startDate = uiState.startDate,
            activeDays = uiState.activeDays
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
    onDaysChanged: (dayOfWeek: DayOfWeek, isActive: Boolean) -> Unit,
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

            // Regular day picker
            DayPicker(
                activeDays = activeDays,
                dayStateChanged = onDaysChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add preset buttons in a row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PresetDayButton(
                    text = stringResource(Res.string.weekdays),
                    onClick = {
                        onGroupChanged(
                            listOf(
                                DayOfWeek.MONDAY,
                                DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY
                            )
                        )
                    }
                )

                PresetDayButton(
                    text = stringResource(Res.string.only_weekends),
                    onClick = {
                        onGroupChanged(
                            listOf(
                                DayOfWeek.SATURDAY,
                                DayOfWeek.SUNDAY
                            )
                        )
                    }
                )

                PresetDayButton(
                    text = stringResource(Res.string.every_day),
                    onClick = {
                        onGroupChanged(DayOfWeek.entries)
                    }
                )
            }
        }
    }
}

@Composable
private fun PresetDayButton(
    text: String,
    onClick: () -> Unit
) {
    BloomSmallActionButton(
        text = text,
        onClick = onClick,
    )
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
    onDurationChanged: (Int) -> Unit,
    startDate: LocalDate? = null,
    activeDays: List<DayOfWeek> = emptyList()
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
            Spacer(modifier = Modifier.height(16.dp))

            VisualDurationSelector(
                duration = duration,
                onDurationChanged = onDurationChanged,
                startDate = startDate,
                activeDays = activeDays,
                modifier = Modifier.fillMaxWidth()
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

/**
 * Visual representation of duration with chips and calendar-based explanation
 */
@Composable
fun VisualDurationSelector(
    modifier: Modifier = Modifier,
    duration: Int,
    onDurationChanged: (Int) -> Unit,
    startDate: LocalDate?,
    activeDays: List<DayOfWeek>,
    enabled: Boolean = true
) {
    // Define common durations with more meaningful labels
    val durations = listOf(
        DurationOption(1, stringResource(Res.string.one_week)),
        DurationOption(4, stringResource(Res.string.four_repeats)),
        DurationOption(8, stringResource(Res.string.two_months)),
        DurationOption(12, stringResource(Res.string.twelve_repeats)),
    )

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
                    isSelected = duration == option.value,
                    onClick = { onDurationChanged(option.value) },
                    enabled = enabled
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Regular slider for fine-tuning
        BloomSlider(
            value = duration.toFloat(),
            onValueChange = { newValue -> onDurationChanged(newValue.roundToInt()) },
            valueRange = 1f..12f,
            steps = 11,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Show current duration with completion date
        if (startDate != null && activeDays.isNotEmpty()) {
            val approximateEndDate = calculateApproximateEndDate(startDate, duration, activeDays)
            val formattedDate = approximateEndDate.formatToMmDdYyWithLocale()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pluralStringResource(
                        resource = Res.plurals.selected_repeats,
                        quantity = duration,
                        duration
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
                    quantity = duration,
                    duration
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
private fun calculateApproximateEndDate(
    startDate: LocalDate,
    durationInRepeats: Int,
    activeDays: List<DayOfWeek>
): LocalDate {
    // Simple approximation: each repeat is roughly a week
    val daysToAdd = durationInRepeats * 7
    return startDate.plus(daysToAdd, DateTimeUnit.DAY)
}

// Helper data class for duration options
private data class DurationOption(val value: Int, val label: String)

/**
 * Safe extension to format LocalDate with a fallback
 */
private fun LocalDate.formatToMmDdYyWithLocale(): String {
    return try {
        this.toString() // Use the toString method as fallback
    } catch (e: Exception) {
        "${monthNumber}/${dayOfMonth}/${year}"
    }
}

