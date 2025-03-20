package com.horizondev.habitbloom.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarDayStatusColors
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarTitle
import com.horizondev.habitbloom.core.designComponents.calendar.HabitDayState
import com.horizondev.habitbloom.core.designComponents.calendar.MonthHeader
import com.horizondev.habitbloom.core.designComponents.calendar.color
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.calendar_filter_all_habits
import habitbloom.composeapp.generated.resources.calendar_filter_button
import habitbloom.composeapp.generated.resources.calendar_habit_detail_date
import habitbloom.composeapp.generated.resources.calendar_no_habits
import habitbloom.composeapp.generated.resources.calendar_screen_title
import habitbloom.composeapp.generated.resources.calendar_statistics_completed
import habitbloom.composeapp.generated.resources.calendar_statistics_completion_rate
import habitbloom.composeapp.generated.resources.calendar_statistics_longest_streak
import habitbloom.composeapp.generated.resources.calendar_statistics_title
import habitbloom.composeapp.generated.resources.calendar_statistics_total
import habitbloom.composeapp.generated.resources.calendar_streak_current
import habitbloom.composeapp.generated.resources.calendar_streak_longest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/**
 * Calendar screen composable that displays habit calendar.
 */
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is CalendarUiIntent.OpenHabitDetails -> {
                // Navigation will be handled by parent NavHost
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.calendar_screen_title),
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary
                )

                // Filter button
                TimeOfDayFilterButton(
                    selectedFilter = uiState.selectedTimeOfDayFilter,
                    onFilterSelected = { timeOfDay ->
                        viewModel.handleUiEvent(CalendarUiEvent.FilterByTimeOfDay(timeOfDay))
                    }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                BloomLoadingAnimation(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            CalendarScreenContent(
                uiState = uiState,
                handleUiEvent = viewModel::handleUiEvent,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScreenContent(
    uiState: CalendarUiState,
    handleUiEvent: (CalendarUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Monthly Statistics Summary
        MonthlyStatisticsCard(
            stats = uiState.monthlyStats,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Legend
        CalendarDayStatusColors(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar
        FullScreenCalendar(
            uiState = uiState,
            onDateSelected = { date ->
                handleUiEvent(CalendarUiEvent.SelectDate(date))
            },
            onMonthChanged = { yearMonth ->
                handleUiEvent(CalendarUiEvent.ChangeMonth(yearMonth))
            }
        )
    }

    // Bottom sheet for habit details
    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                handleUiEvent(CalendarUiEvent.CloseBottomSheet)
            },
            sheetState = sheetState
        ) {
            DailyHabitDetailBottomSheet(
                date = uiState.selectedDate,
                habits = uiState.habitsForSelectedDate,
                habitStreaks = uiState.habitsWithStreaks,
                onHabitStatusChanged = { habitId, completed ->
                    handleUiEvent(
                        CalendarUiEvent.ToggleHabitCompletion(
                            habitId = habitId,
                            date = uiState.selectedDate,
                            completed = completed
                        )
                    )
                },
                onHabitClicked = { habitId ->
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        handleUiEvent(CalendarUiEvent.CloseBottomSheet)
                        handleUiEvent(CalendarUiEvent.OpenHabitDetails(habitId))
                    }
                }
            )
        }
    }
}

@Composable
private fun FullScreenCalendar(
    uiState: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (com.kizitonwose.calendar.core.YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMonth = uiState.currentMonth
    val currentDate = getCurrentDate()
    val startMonth = remember { currentMonth.minusMonths(12) } // Show past 12 months
    val endMonth = remember { currentMonth.plusMonths(3) } // Show next 3 months
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = state.firstVisibleMonth

    Column(modifier = modifier.fillMaxWidth()) {
        CalendarTitle(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            currentMonth = visibleMonth.yearMonth,
            goToNext = {
                coroutineScope.launch {
                    state.animateScrollToMonth(visibleMonth.yearMonth.plusMonths(1))
                    onMonthChanged(visibleMonth.yearMonth.plusMonths(1))
                }
            },
            goToPrevious = {
                coroutineScope.launch {
                    state.animateScrollToMonth(visibleMonth.yearMonth.minusMonths(1))
                    onMonthChanged(visibleMonth.yearMonth.minusMonths(1))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar View
        HorizontalCalendar(
            state = state,
            dayContent = { calendarDay ->
                val date = calendarDay.date
                val kotlinDate = LocalDate(
                    year = date.year,
                    monthNumber = date.monthNumber,
                    dayOfMonth = date.dayOfMonth
                )

                val habitsForDay = uiState.habitsByDate[kotlinDate] ?: emptyList()

                // Apply time of day filter
                val filteredHabits = if (uiState.selectedTimeOfDayFilter != null) {
                    habitsForDay.filter { it.timeOfDay == uiState.selectedTimeOfDayFilter }
                } else {
                    habitsForDay
                }

                CalendarDay(
                    calendarDay = calendarDay,
                    isSelected = kotlinDate == uiState.selectedDate,
                    habits = filteredHabits,
                    onDateClick = { onDateSelected(kotlinDate) }
                )
            },
            monthHeader = { month ->
                val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                MonthHeader(daysOfWeek = daysOfWeek, modifier = Modifier.fillMaxWidth())
            }
        )
    }
}

@Composable
private fun CalendarDay(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    habits: List<UserHabitRecordFullInfo>,
    onDateClick: () -> Unit
) {
    val hasHabits = habits.isNotEmpty()
    val hasCompletedHabits = habits.any { it.isCompleted }
    val hasMissedHabits = habits.any { !it.isCompleted }
    val currentDate = getCurrentDate()
    val date = remember(calendarDay) {
        LocalDate(
            year = calendarDay.date.year,
            monthNumber = calendarDay.date.monthNumber,
            dayOfMonth = calendarDay.date.dayOfMonth
        )
    }

    // Determine the day state
    val dayState = when {
        !hasHabits -> HabitDayState.None
        hasCompletedHabits && !hasMissedHabits -> HabitDayState.Completed
        date < currentDate && hasMissedHabits -> HabitDayState.Missed
        else -> HabitDayState.Future
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable(enabled = hasHabits) { onDateClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = BloomTheme.colors.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Day number
        Text(
            text = calendarDay.date.dayOfMonth.toString(),
            style = BloomTheme.typography.subheading.copy(
                fontWeight = if (isSelected || date == currentDate) {
                    FontWeight.Bold
                } else FontWeight.Normal
            ),
            color = when {
                calendarDay.position != com.kizitonwose.calendar.core.DayPosition.MonthDate ->
                    BloomTheme.colors.textColor.disabled

                isSelected -> BloomTheme.colors.primary
                date == currentDate -> BloomTheme.colors.primary
                else -> BloomTheme.colors.textColor.primary
            }
        )

        // Habit indicator
        if (hasHabits) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .height(4.dp)
                    .background(
                        color = dayState.color(),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}

@Composable
private fun MonthlyStatisticsCard(
    stats: MonthlyStatistics,
    modifier: Modifier = Modifier
) {
    BloomSurface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.calendar_statistics_title),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Total Habits
                StatisticItem(
                    label = stringResource(Res.string.calendar_statistics_total),
                    value = stats.totalHabits.toString(),
                    modifier = Modifier.weight(1f)
                )

                // Completed Habits
                StatisticItem(
                    label = stringResource(Res.string.calendar_statistics_completed),
                    value = stats.completedHabits.toString(),
                    modifier = Modifier.weight(1f)
                )

                // Longest Streak
                StatisticItem(
                    label = stringResource(Res.string.calendar_statistics_longest_streak),
                    value = stats.longestStreak.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Completion Rate
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.calendar_statistics_completion_rate),
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.textColor.secondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { stats.completionRate },
                    modifier = Modifier.fillMaxWidth(),
                    color = BloomTheme.colors.primary,
                    trackColor = BloomTheme.colors.disabled
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${(stats.completionRate * 100).toInt()}%",
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimeOfDayFilterButton(
    selectedFilter: TimeOfDay?,
    onFilterSelected: (TimeOfDay?) -> Unit
) {
    var showFilterOptions by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { showFilterOptions = !showFilterOptions }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(Res.string.calendar_filter_button),
                tint = if (selectedFilter != null) BloomTheme.colors.primary else BloomTheme.colors.textColor.primary
            )
        }

        AnimatedVisibility(
            visible = showFilterOptions,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            BloomSurface(
                modifier = Modifier.padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // All Habits option
                    FilterOption(
                        text = stringResource(Res.string.calendar_filter_all_habits),
                        isSelected = selectedFilter == null,
                        onClick = {
                            onFilterSelected(null)
                            showFilterOptions = false
                        }
                    )

                    // Time of Day options
                    TimeOfDay.entries.forEach { timeOfDay ->
                        FilterOption(
                            text = timeOfDay.name,
                            isSelected = selectedFilter == timeOfDay,
                            onClick = {
                                onFilterSelected(timeOfDay)
                                showFilterOptions = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.body,
            color = if (isSelected) BloomTheme.colors.primary else BloomTheme.colors.textColor.primary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(BloomTheme.colors.primary, CircleShape)
            )
        }
    }
}

@Composable
private fun DailyHabitDetailBottomSheet(
    date: LocalDate,
    habits: List<UserHabitRecordFullInfo>,
    habitStreaks: Map<Long, HabitStreakInfo>,
    onHabitStatusChanged: (Long, Boolean) -> Unit,
    onHabitClicked: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Date and close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.calendar_habit_detail_date, date.toString()),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (habits.isEmpty()) {
            Text(
                text = stringResource(Res.string.calendar_no_habits),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        } else {
            LazyColumn {
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        streakInfo = habitStreaks[habit.userHabitId],
                        onStatusChanged = { completed ->
                            onHabitStatusChanged(habit.userHabitId, completed)
                        },
                        onHabitClicked = {
                            onHabitClicked(habit.userHabitId)
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = BloomTheme.colors.disabled.copy(alpha = 0.3f)
                    )
                }

                // Add some padding at the bottom
                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
private fun HabitItem(
    habit: UserHabitRecordFullInfo,
    streakInfo: HabitStreakInfo?,
    onStatusChanged: (Boolean) -> Unit,
    onHabitClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHabitClicked() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox for completion status
        Checkbox(
            checked = habit.isCompleted,
            onCheckedChange = { onStatusChanged(it) },
            colors = androidx.compose.material3.CheckboxDefaults.colors(
                checkedColor = BloomTheme.colors.primary,
                uncheckedColor = BloomTheme.colors.disabled
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Habit details (name, time, streaks)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = habit.name,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = habit.timeOfDay.name,
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary
            )

            // Streak info if available
            if (streakInfo != null) {
                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(Res.string.calendar_streak_current))
                            append(": ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(streakInfo.currentStreak.toString())
                            }
                        },
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(Res.string.calendar_streak_longest))
                            append(": ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(streakInfo.longestStreak.toString())
                            }
                        },
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.primary
                    )
                }
            }
        }

        // Completion status icon/indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = if (habit.isCompleted) {
                        BloomTheme.colors.success
                    } else {
                        BloomTheme.colors.secondary
                    },
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun ColumnScope.Spacer(modifier: Modifier = Modifier, width: Int) {
    Spacer(modifier = Modifier.width(width.dp))
}