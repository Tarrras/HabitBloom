@file:OptIn(ExperimentalTime::class)

package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarDayStatusColors
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarTitle
import com.horizondev.habitbloom.core.designComponents.calendar.Day
import com.horizondev.habitbloom.core.designComponents.calendar.HabitDayState
import com.horizondev.habitbloom.core.designComponents.calendar.MonthHeader
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.containers.BloomToolbar
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.dialogs.AddHabitDateRangePickerDialog
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.TimePicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.formatTime
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.active_days
import habitbloom.composeapp.generated.resources.add
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.clear
import habitbloom.composeapp.generated.resources.clear_history_description
import habitbloom.composeapp.generated.resources.clear_history_question
import habitbloom.composeapp.generated.resources.date_range
import habitbloom.composeapp.generated.resources.delete
import habitbloom.composeapp.generated.resources.delete_habit_description
import habitbloom.composeapp.generated.resources.delete_habit_question
import habitbloom.composeapp.generated.resources.duration_and_days
import habitbloom.composeapp.generated.resources.edit
import habitbloom.composeapp.generated.resources.edit_dates
import habitbloom.composeapp.generated.resources.enable_reminder
import habitbloom.composeapp.generated.resources.habit_schedule
import habitbloom.composeapp.generated.resources.ic_warning_filled
import habitbloom.composeapp.generated.resources.no_reminder_set
import habitbloom.composeapp.generated.resources.reminder_set_for
import habitbloom.composeapp.generated.resources.reminder_settings
import habitbloom.composeapp.generated.resources.save
import habitbloom.composeapp.generated.resources.save_changes
import habitbloom.composeapp.generated.resources.select_reminder_time
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

@Composable
fun HabitDetailsScreen(
    userHabitId: Long,
    popBackStack: () -> Unit
) {

    val viewModel = koinViewModel<HabitDetailsViewModel> {
        parametersOf(userHabitId)
    }

    val uiState by viewModel.state.collectAsState()

    val scope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }

    viewModel.uiIntents.collectAsEffect { uiIntent ->
        when (uiIntent) {
            HabitScreenDetailsUiIntent.NavigateBack -> popBackStack()
            is HabitScreenDetailsUiIntent.ShowSnackbar -> {
                scope.launch {
                    snackBarState.showSnackbar(uiIntent.visuals)
                }
            }
        }
    }


    HabitDetailsScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
        snackbarHostState = snackBarState
    )
}

@Composable
fun HabitDetailsScreenContent(
    uiState: HabitScreenDetailsUiState,
    handleUiEvent: (HabitScreenDetailsUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = BloomTheme.colors.background,
        topBar = {
            BloomToolbar(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                title = uiState.habitInfo?.name.orEmpty(),
                onBackPressed = {
                    handleUiEvent(HabitScreenDetailsUiEvent.BackPressed)
                },
                menuItems = listOf(
                    "Clear History" to {
                        handleUiEvent(HabitScreenDetailsUiEvent.RequestClearHistory)
                    },
                    "Delete Habit" to {
                        handleUiEvent(HabitScreenDetailsUiEvent.RequestDeleteHabit)
                    }
                )
            )
        },
        snackbarHost = {
            BloomSnackbarHost(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                snackBarState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().background(
                color = BloomTheme.colors.background
            )
        ) {
            if (uiState.habitInfo != null) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    UserHabitFullInfoCard(
                        modifier = Modifier.fillMaxWidth(),
                        habitInfo = uiState.habitInfo,
                        paddingValues = paddingValues
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        UserHabitProgressCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            uiState = uiState.progressUiState
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HabitDurationEditor(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            editMode = uiState.habitDurationEditMode,
                            startDate = uiState.startDate,
                            endDate = uiState.endDate,
                            activeDays = uiState.habitDays,
                            onEditModeChanged = {
                                handleUiEvent(HabitScreenDetailsUiEvent.DurationEditModeChanged)
                            },
                            onDayStateChanged = { dayOfWeek, isActive ->
                                handleUiEvent(
                                    HabitScreenDetailsUiEvent.DayStateChanged(
                                        dayOfWeek, isActive
                                    )
                                )
                            },
                            onDateRangeEditorRequest = {
                                handleUiEvent(HabitScreenDetailsUiEvent.ShowDatePickerDialog)
                            },
                            onUpdateHabitDuration = {
                                handleUiEvent(HabitScreenDetailsUiEvent.UpdateHabitDuration)
                            },
                            updateButtonEnabled = uiState.durationUpdateButtonEnabled
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        UserHabitScheduleCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            habitInfo = uiState.habitInfo
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ReminderSettingsCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            reminderEnabled = uiState.reminderEnabled,
                            reminderTime = uiState.reminderTime,
                            onShowReminderDialog = {
                                handleUiEvent(HabitScreenDetailsUiEvent.ShowReminderDialog)
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }

                    // Date Range Picker Dialog
                    DateRangePickerDialog(
                        isVisible = uiState.showDatePickerDialog,
                        startDate = uiState.startDate,
                        endDate = uiState.endDate,
                        onDismiss = {
                            handleUiEvent(HabitScreenDetailsUiEvent.DismissDatePickerDialog)
                        },
                        onDateRangeSelected = { startDate, endDate ->
                            handleUiEvent(
                                HabitScreenDetailsUiEvent.DateRangeChanged(
                                    startDate,
                                    endDate
                                )
                            )
                        }
                    )

                    // Delete Habit Confirmation Dialog
                    DeleteHabitDialog(
                        showDeleteDialog = uiState.showDeleteDialog,
                        onDismiss = {
                            handleUiEvent(HabitScreenDetailsUiEvent.DismissHabitDeletion)
                        },
                        onDelete = {
                            handleUiEvent(HabitScreenDetailsUiEvent.DeleteHabit)
                        }
                    )

                    // Clear History Confirmation Dialog
                    ClearHistoryDialog(
                        showClearDialog = uiState.showClearHistoryDialog,
                        onDismiss = {
                            handleUiEvent(HabitScreenDetailsUiEvent.DismissClearHistory)
                        },
                        onClear = {
                            handleUiEvent(HabitScreenDetailsUiEvent.ClearHistory)
                        }
                    )
                }

                ReminderDialog(
                    showDialog = uiState.showReminderDialog,
                    reminderEnabled = uiState.reminderEnabled,
                    reminderTime = uiState.reminderTime,
                    onDismiss = {
                        handleUiEvent(HabitScreenDetailsUiEvent.DismissReminderDialog)
                    },
                    onReminderEnabledChanged = { enabled ->
                        handleUiEvent(HabitScreenDetailsUiEvent.ReminderEnabledChanged(enabled))
                    },
                    onReminderTimeChanged = { time ->
                        handleUiEvent(HabitScreenDetailsUiEvent.ReminderTimeChanged(time))
                    },
                    onSave = {
                        handleUiEvent(HabitScreenDetailsUiEvent.SaveReminderSettings)
                    }
                )
            } else {
                BloomLoadingAnimation(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
private fun UserHabitFullInfoCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitFullInfo,
    paddingValues: PaddingValues
) {
    BloomSurface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
            BloomNetworkImage(
                contentDescription = "logo",
                iconUrl = habitInfo.iconUrl,
                size = 96.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = habitInfo.description,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
            )
        }
    }
}

@Composable
fun HabitDurationEditor(
    modifier: Modifier = Modifier,
    editMode: Boolean,
    startDate: LocalDate?,
    endDate: LocalDate?,
    activeDays: List<DayOfWeek>,
    onEditModeChanged: () -> Unit,
    onDayStateChanged: (DayOfWeek, Boolean) -> Unit,
    onDateRangeEditorRequest: () -> Unit,
    onUpdateHabitDuration: () -> Unit,
    updateButtonEnabled: Boolean
) {
    val editButtonText = if (editMode) {
        stringResource(Res.string.cancel)
    } else {
        stringResource(Res.string.edit)
    }

    BloomCard(
        modifier = modifier,
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.duration_and_days),
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary
                )

                BloomSmallActionButton(
                    text = editButtonText,
                    onClick = onEditModeChanged
                )
            }

            // Date Range Section (only editable in edit mode)
            if (startDate != null && endDate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.date_range),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.secondary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$startDate - $endDate",
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.primary
                            )
                        }
                    }

                    if (editMode) {
                        BloomSmallActionButton(
                            text = stringResource(Res.string.edit_dates),
                            onClick = onDateRangeEditorRequest
                        )
                    }
                }
            }

            // Active Days Section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.active_days),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                DayPicker(
                    activeDays = activeDays,
                    dayStateChanged = { day, selected ->
                        onDayStateChanged(day, selected)
                    },
                    enabled = editMode
                )
            }

            // Update Button (only shown in edit mode)
            if (editMode) {
                BloomPrimaryFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = updateButtonEnabled,
                    text = stringResource(Res.string.save_changes),
                    onClick = onUpdateHabitDuration
                )
            }
        }
    }
}

// Helper to capitalize the first letter of a string
fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

// Add the DateRangePickerDialog component
@Composable
fun DateRangePickerDialog(
    isVisible: Boolean,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateRangeSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    if (!isVisible) return

    // Use the existing AddHabitDateRangePickerDialog component
    val minimumStartDate = getCurrentDate()
    val initialStartDate = startDate ?: minimumStartDate
    val initialEndDate = endDate ?: initialStartDate.plus(30, kotlinx.datetime.DateTimeUnit.DAY)

    AddHabitDateRangePickerDialog(
        startDate = initialStartDate,
        endDate = initialEndDate,
        maxDurationDays = 90, // Maximum 3 months
        onDatesSelected = { newStartDate, newEndDate ->
            if (newEndDate != null) {
                onDateRangeSelected(newStartDate, newEndDate)
            }
        },
        onDismiss = onDismiss,
        minDate = minimumStartDate
    )
}

@Composable
private fun UserHabitScheduleCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitFullInfo
) {
    val currentMonth = remember { getCurrentDate().yearMonth }
    val currentDate = remember { getCurrentDate() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(3) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = state.firstVisibleMonth

    BloomSurface(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.habit_schedule),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            CalendarDayStatusColors(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            CalendarTitle(
                modifier = Modifier.fillMaxWidth(),
                currentMonth = visibleMonth.yearMonth,
                goToNext = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.plusMonths(1))
                    }
                },
                goToPrevious = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.minusMonths(1))
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalCalendar(
                state = state,
                dayContent = { calendarDay ->
                    val date = calendarDay.date
                    val existingHabitRecord = habitInfo.records.find {
                        it.date == date
                    }

                    Day(
                        day = calendarDay,
                        state = when {
                            existingHabitRecord == null -> HabitDayState.None
                            existingHabitRecord.isCompleted -> HabitDayState.Completed
                            existingHabitRecord.isCompleted.not() && date < currentDate -> {
                                HabitDayState.Missed
                            }

                            else -> HabitDayState.Future
                        },
                        selected = date == currentDate
                    )
                },
                monthHeader = { month ->
                    val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                    MonthHeader(daysOfWeek = daysOfWeek, modifier = Modifier.fillMaxWidth())
                }
            )
        }
    }
}

@Composable
fun DeleteHabitDialog(
    modifier: Modifier = Modifier,
    showDeleteDialog: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    BloomAlertDialog(
        isShown = showDeleteDialog,
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(resource = Res.drawable.ic_warning_filled),
                modifier = Modifier.size(48.dp),
                contentDescription = "warning"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.delete_habit_question),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.subheading,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.delete_habit_description),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.body,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.delete),
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BloomTheme.colors.error,
                    contentColor = BloomTheme.colors.textColor.white
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomPrimaryOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.cancel),
                onClick = onDismiss,
                borderStroke = BorderStroke(
                    width = 2.dp,
                    color = BloomTheme.colors.error
                )
            )
        }
    }
}

@Composable
fun ClearHistoryDialog(
    modifier: Modifier = Modifier,
    showClearDialog: Boolean,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    BloomAlertDialog(
        isShown = showClearDialog,
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(resource = Res.drawable.ic_warning_filled),
                modifier = Modifier.size(48.dp),
                contentDescription = "warning"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.clear_history_question),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.subheading,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.clear_history_description),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.body,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.clear),
                onClick = onClear,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BloomTheme.colors.error,
                    contentColor = BloomTheme.colors.textColor.white
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomPrimaryOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.cancel),
                onClick = onDismiss,
                borderStroke = BorderStroke(
                    width = 2.dp,
                    color = BloomTheme.colors.error
                )
            )
        }
    }
}

@Composable
private fun ReminderSettingsCard(
    modifier: Modifier = Modifier,
    reminderEnabled: Boolean,
    reminderTime: LocalTime,
    onShowReminderDialog: () -> Unit
) {
    BloomSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.reminder_settings),
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (reminderEnabled) Icons.Default.Notifications else Icons.Default.Notifications,
                    contentDescription = "Reminder status",
                    tint = if (reminderEnabled) BloomTheme.colors.primary else BloomTheme.colors.textColor.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (reminderEnabled) {
                    Text(
                        text = stringResource(
                            Res.string.reminder_set_for, formatTime(
                                reminderTime,
                                use24HourFormat = true
                            )
                        ),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = stringResource(
                            Res.string.no_reminder_set
                        ),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                BloomSmallActionButton(
                    text = if (reminderEnabled) stringResource(Res.string.edit)
                    else stringResource(Res.string.add),
                    onClick = onShowReminderDialog
                )
            }
        }
    }
}

@Composable
private fun ReminderDialog(
    showDialog: Boolean,
    reminderEnabled: Boolean,
    reminderTime: LocalTime,
    onDismiss: () -> Unit,
    onReminderEnabledChanged: (Boolean) -> Unit,
    onReminderTimeChanged: (LocalTime) -> Unit,
    onSave: () -> Unit
) {
    BloomAlertDialog(
        isShown = showDialog,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.reminder_settings),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Enable/disable reminder switch
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

            Spacer(modifier = Modifier.height(24.dp))

            // Time picker (only enabled if reminders are enabled)
            if (reminderEnabled) {
                Text(
                    text = stringResource(Res.string.select_reminder_time),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TimePicker(
                    time = reminderTime,
                    onTimeSelected = onReminderTimeChanged
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                BloomPrimaryOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.cancel),
                    onClick = onDismiss
                )

                Spacer(modifier = Modifier.width(16.dp))

                BloomPrimaryFilledButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.save),
                    onClick = onSave
                )
            }
        }
    }
}
