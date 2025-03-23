package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallFilledActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarDayStatusColors
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarTitle
import com.horizondev.habitbloom.core.designComponents.calendar.Day
import com.horizondev.habitbloom.core.designComponents.calendar.HabitDayState
import com.horizondev.habitbloom.core.designComponents.calendar.MonthHeader
import com.horizondev.habitbloom.core.designComponents.charts.BloomProgressIndicator
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.containers.BloomToolbar
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.TimePicker
import com.horizondev.habitbloom.core.designComponents.pickers.formatTime
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.clear
import habitbloom.composeapp.generated.resources.clear_history_description
import habitbloom.composeapp.generated.resources.clear_history_question
import habitbloom.composeapp.generated.resources.completed_repeats
import habitbloom.composeapp.generated.resources.delete
import habitbloom.composeapp.generated.resources.delete_habit_description
import habitbloom.composeapp.generated.resources.delete_habit_question
import habitbloom.composeapp.generated.resources.edit
import habitbloom.composeapp.generated.resources.enable_reminder
import habitbloom.composeapp.generated.resources.habit_active_days
import habitbloom.composeapp.generated.resources.habit_repeats
import habitbloom.composeapp.generated.resources.habit_schedule
import habitbloom.composeapp.generated.resources.ic_warning_filled
import habitbloom.composeapp.generated.resources.no_reminder_set
import habitbloom.composeapp.generated.resources.reminder_set_for
import habitbloom.composeapp.generated.resources.reminder_settings
import habitbloom.composeapp.generated.resources.repeats
import habitbloom.composeapp.generated.resources.save
import habitbloom.composeapp.generated.resources.select_reminder_time
import habitbloom.composeapp.generated.resources.selected_repeats
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

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

                        UserHabitDurationCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            isEditMode = uiState.habitDurationEditMode,
                            days = uiState.habitDays,
                            repeats = uiState.habitRepeats,
                            completedRepeats = uiState.habitInfo.completedRepeats,
                            dayStateChanged = { dayOfWeek, isActive ->
                                handleUiEvent(
                                    HabitScreenDetailsUiEvent.DayStateChanged(
                                        dayOfWeek = dayOfWeek,
                                        isActive = isActive
                                    )
                                )
                            },
                            onEditModeChanged = {
                                handleUiEvent(HabitScreenDetailsUiEvent.DurationEditModeChanged)
                            },
                            onDurationChanged = {
                                handleUiEvent(HabitScreenDetailsUiEvent.DurationChanged(it))
                            },
                            onUpdateHabitDuration = {
                                handleUiEvent(HabitScreenDetailsUiEvent.UpdateHabitDuration)
                            }
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
                }
            }
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BloomLoadingAnimation(
                        modifier = Modifier.align(Alignment.Center).size(150.dp),
                    )
                }
            }
        }
    }


    DeleteHabitDialog(
        showDeleteDialog = uiState.showDeleteDialog,
        onDismiss = {
            handleUiEvent(HabitScreenDetailsUiEvent.DismissHabitDeletion)
        },
        onDelete = {
            handleUiEvent(HabitScreenDetailsUiEvent.DeleteHabit)
        }
    )

    ClearHistoryDialog(
        showDialog = uiState.showClearHistoryDialog,
        onDismiss = {
            handleUiEvent(HabitScreenDetailsUiEvent.DismissClearHistory)
        },
        onConfirm = {
            handleUiEvent(HabitScreenDetailsUiEvent.ClearHistory)
        }
    )

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
private fun UserHabitDurationCard(
    modifier: Modifier = Modifier,
    repeats: Int,
    completedRepeats: Int,
    days: List<DayOfWeek>,
    isEditMode: Boolean,
    dayStateChanged: (DayOfWeek, Boolean) -> Unit,
    onDurationChanged: (Int) -> Unit,
    onEditModeChanged: () -> Unit,
    onUpdateHabitDuration: () -> Unit
) {
    val completedRepeatsRate = (completedRepeats.toFloat() / repeats.toFloat())

    BloomSurface(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.habit_active_days),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            DayPicker(
                modifier = Modifier.fillMaxWidth(),
                activeDays = days,
                dayStateChanged = dayStateChanged,
                enabled = isEditMode
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.habit_repeats),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )

            AnimatedContent(isEditMode) { isEditModeState ->
                if (isEditModeState) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        if (completedRepeats <= 10) {
                            BloomSlider(
                                value = repeats.toFloat(),
                                onValueChange = { newValue -> onDurationChanged(newValue.roundToInt()) },
                                valueRange = (completedRepeats.toFloat() + 1)..12f, // Set the range from 1 to 12
                                steps = 11, // 11 steps because we start from 1
                                enabled = true
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pluralStringResource(
                                resource = Res.plurals.selected_repeats,
                                quantity = repeats,
                                repeats
                            ),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.primary,
                            textDecoration = TextDecoration.Underline,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = pluralStringResource(
                                resource = Res.plurals.completed_repeats,
                                quantity = completedRepeats,
                                completedRepeats
                            ),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.primary,
                            textDecoration = TextDecoration.Underline,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        BloomSmallFilledActionButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.save),
                            onClick = {
                                onUpdateHabitDuration()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BloomSmallActionButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.cancel),
                            onClick = {
                                onEditModeChanged()
                            }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(Modifier.fillMaxWidth()) {
                            BloomProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                percentage = completedRepeatsRate,
                                radius = 24.dp
                            )

                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$completedRepeats/$repeats",
                                    style = BloomTheme.typography.heading,
                                    color = BloomTheme.colors.textColor.primary,
                                )
                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = stringResource(Res.string.repeats).lowercase(),
                                    style = BloomTheme.typography.small,
                                    color = BloomTheme.colors.textColor.secondary,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))


                        BloomSmallActionButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.edit),
                            onClick = {
                                onEditModeChanged()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun UserHabitScheduleCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitFullInfo
) {
    val currentMonth = remember { YearMonth.now() }
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
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BloomAlertDialog(
        isShown = showDialog,
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
                onClick = onConfirm,
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
