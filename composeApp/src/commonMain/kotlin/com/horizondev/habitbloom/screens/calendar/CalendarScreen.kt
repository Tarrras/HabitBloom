package com.horizondev.habitbloom.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarDayStatusColors
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarTitle
import com.horizondev.habitbloom.core.designComponents.calendar.MonthHeader
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.habits.presentation.home.components.HabitProgressIndicator
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.formatToMmDdYy
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.minusDays
import com.horizondev.habitbloom.utils.plusDays
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.calendar_filter_all_habits
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
import kotlinx.coroutines.delay
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
            }
        },
        floatingActionButton = {
            // Jump to today FAB
            SmallFloatingActionButton(
                onClick = {
                    viewModel.handleUiEvent(CalendarUiEvent.JumpToToday)
                },
                containerColor = BloomTheme.colors.primary.copy(alpha = 0.9f),
                shape = CircleShape
            ) {
                // Custom today indicator using simple shapes
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(24.dp)
                ) {
                    // Calendar outline
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(1.dp, Color.White, RoundedCornerShape(2.dp))
                    )

                    // Day number
                    Text(
                        text = getCurrentDate().dayOfMonth.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    val coroutineScope = rememberCoroutineScope()

    // Collapsible statistics card state
    var statsCardExpanded by remember { mutableStateOf(true) }
    val rotationState by animateFloatAsState(
        targetValue = if (statsCardExpanded) 0f else 180f
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Time of day filter chips
        TimeOfDayFilterChips(
            selectedFilter = uiState.selectedTimeOfDayFilter,
            onFilterSelected = { timeOfDay ->
                handleUiEvent(CalendarUiEvent.FilterByTimeOfDay(timeOfDay))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Collapsible Monthly Statistics Card
        BloomSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
                    .animateContentSize()
            ) {
                // Header with expand/collapse button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { statsCardExpanded = !statsCardExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.calendar_statistics_title),
                        style = BloomTheme.typography.subheading,
                        color = BloomTheme.colors.textColor.primary
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = if (statsCardExpanded) "Collapse" else "Expand",
                        tint = BloomTheme.colors.textColor.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationState)
                    )
                }

                // Content that expands/collapses
                AnimatedVisibility(visible = statsCardExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Total Habits
                            StatisticItem(
                                label = stringResource(Res.string.calendar_statistics_total),
                                value = uiState.monthlyStats.totalHabits.toString(),
                                modifier = Modifier.weight(1f)
                            )

                            // Completed Habits
                            StatisticItem(
                                label = stringResource(Res.string.calendar_statistics_completed),
                                value = uiState.monthlyStats.completedHabits.toString(),
                                modifier = Modifier.weight(1f)
                            )

                            // Longest Streak
                            StatisticItem(
                                label = stringResource(Res.string.calendar_statistics_longest_streak),
                                value = uiState.monthlyStats.longestStreak.toString(),
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

                            HabitProgressIndicator(
                                totalHabits = uiState.monthlyStats.totalHabits,
                                totalCompletedHabits = uiState.monthlyStats.completedHabits,
                                strokeHeight = 4.dp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${(uiState.monthlyStats.completionRate * 100).toInt()}%",
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Legend
        CalendarDayStatusColors(
            modifier = Modifier.fillMaxWidth()
        )

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
            sheetState = sheetState,
            dragHandle = {
                // Custom drag handle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = BloomTheme.colors.disabled,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        ) {
            DailyHabitDetailBottomSheet(
                uiState = uiState,
                onHabitStatusChanged = { habitId, completed ->
                    handleUiEvent(
                        CalendarUiEvent.ToggleHabitCompletion(
                            habitId = habitId,
                            date = uiState.selectedDate,
                            completed = completed
                        )
                    )

                    // Show confetti if habit is completed
                    if (completed) {
                        coroutineScope.launch {
                            handleUiEvent(CalendarUiEvent.ShowStreakCelebration(habitId))
                        }
                    }
                },
                onHabitClicked = { habitId ->
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        handleUiEvent(CalendarUiEvent.CloseBottomSheet)
                        handleUiEvent(CalendarUiEvent.OpenHabitDetails(habitId))
                    }
                },
                isHalfExpanded = sheetState.currentValue == SheetValue.PartiallyExpanded,
                onExpandSheet = {
                    coroutineScope.launch {
                        sheetState.expand()
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

                ImprovedCalendarDay(
                    calendarDay = calendarDay,
                    isSelected = kotlinDate == uiState.selectedDate,
                    habits = filteredHabits,
                    onDateClick = { onDateSelected(kotlinDate) },
                    isToday = kotlinDate == currentDate,
                    isWeekend = kotlinDate.dayOfWeek.ordinal > 5 // Saturday and Sunday
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
private fun ImprovedCalendarDay(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    isWeekend: Boolean,
    habits: List<UserHabitRecordFullInfo>,
    onDateClick: () -> Unit
) {
    val hasHabits = habits.isNotEmpty()
    val currentDate = getCurrentDate()
    val date = remember(calendarDay) {
        LocalDate(
            year = calendarDay.date.year,
            monthNumber = calendarDay.date.monthNumber,
            dayOfMonth = calendarDay.date.dayOfMonth
        )
    }

    // Background color based on weekend
    val backgroundColor = when {
        isSelected -> BloomTheme.colors.primary.copy(alpha = 0.1f)
        isWeekend -> BloomTheme.colors.disabled.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .clickable(enabled = hasHabits) { onDateClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = BloomTheme.colors.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                } else if (isToday) {
                    Modifier.border(
                        width = 1.dp,
                        color = BloomTheme.colors.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Day number
        Text(
            text = calendarDay.date.dayOfMonth.toString(),
            style = BloomTheme.typography.subheading.copy(
                fontWeight = if (isSelected || isToday) {
                    FontWeight.Bold
                } else FontWeight.Normal
            ),
            color = when {
                calendarDay.position != com.kizitonwose.calendar.core.DayPosition.MonthDate ->
                    BloomTheme.colors.textColor.disabled

                isSelected -> BloomTheme.colors.primary
                isToday -> BloomTheme.colors.primary
                else -> BloomTheme.colors.textColor.primary
            }
        )

        // Multiple habit dots at the bottom
        if (hasHabits) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Group habits by completion status
                val completedHabits = habits.filter { it.isCompleted }
                val missedHabits = habits.filter { !it.isCompleted && date < currentDate }
                val futureHabits = habits.filter { !it.isCompleted && date >= currentDate }

                // Show dots for each habit category (max 3 dots per category)
                val maxDotsPerCategory = 3
                val showCompleted = completedHabits.isNotEmpty()
                val showMissed = missedHabits.isNotEmpty()
                val showFuture = futureHabits.isNotEmpty()

                if (showCompleted) {
                    HabitDot(
                        color = BloomTheme.colors.success,
                        count = completedHabits.size.coerceAtMost(maxDotsPerCategory)
                    )
                }

                if (showMissed) {
                    HabitDot(
                        color = BloomTheme.colors.secondary,
                        count = missedHabits.size.coerceAtMost(maxDotsPerCategory)
                    )
                }

                if (showFuture) {
                    HabitDot(
                        color = BloomTheme.colors.tertiary,
                        count = futureHabits.size.coerceAtMost(maxDotsPerCategory)
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitDot(
    color: Color,
    count: Int = 1
) {
    Row(
        modifier = Modifier.padding(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        repeat(count.coerceAtMost(3)) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
private fun TimeOfDayFilterChips(
    selectedFilter: TimeOfDay?,
    onFilterSelected: (TimeOfDay?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All Habits chip
        item {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelected(null) },
                label = {
                    Text(
                        text = stringResource(Res.string.calendar_filter_all_habits),
                        style = BloomTheme.typography.small
                    )
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedFilter == null) BloomTheme.colors.primary else BloomTheme.colors.disabled
                )
            )
        }

        // Time of Day chips
        items(TimeOfDay.entries) { timeOfDay ->
            FilterChip(
                selected = selectedFilter == timeOfDay,
                onClick = { onFilterSelected(timeOfDay) },
                label = {
                    Text(
                        text = timeOfDay.name,
                        style = BloomTheme.typography.small
                    )
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedFilter == timeOfDay) BloomTheme.colors.primary else BloomTheme.colors.disabled
                )
            )
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
private fun DailyHabitDetailBottomSheet(
    uiState: CalendarUiState,
    onHabitStatusChanged: (Long, Boolean) -> Unit,
    onHabitClicked: (Long) -> Unit,
    isHalfExpanded: Boolean = false,
    onExpandSheet: () -> Unit
) {
    val date = uiState.selectedDate
    val habits = uiState.habitsForSelectedDate
    val habitStreaks = uiState.habitsWithStreaks

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Date header with formatted date
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val formattedDate = remember(date) {
                // Format date in a user-friendly way (e.g., "Monday, January 15")
                val today = getCurrentDate()
                when (date) {
                    today -> "Today"
                    today.minusDays(1) -> "Yesterday"
                    today.plusDays(1) -> "Tomorrow"
                    else -> date.formatToMmDdYy()
                }
            }

            Text(
                text = stringResource(Res.string.calendar_habit_detail_date, formattedDate),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary
            )

            // If sheet is half-expanded, show expand button
            if (isHalfExpanded) {
                TextButton(onClick = onExpandSheet) {
                    Text(
                        text = "Show All",
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.primary
                    )
                }
            }
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
            // Group habits by time of day
            val habitsByTimeOfDay = habits.groupBy { it.timeOfDay }

            LazyColumn {
                habitsByTimeOfDay.forEach { (timeOfDay, habitsForTimeOfDay) ->
                    // Time of day header
                    item {
                        Text(
                            text = timeOfDay.name,
                            style = BloomTheme.typography.subheading,
                            color = BloomTheme.colors.textColor.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Habits for this time of day
                    items(habitsForTimeOfDay) { habit ->
                        HabitItem(
                            habit = habit,
                            streakInfo = habitStreaks[habit.userHabitId],
                            onStatusChanged = { completed ->
                                onHabitStatusChanged(habit.userHabitId, completed)
                            },
                            onHabitClicked = {
                                onHabitClicked(habit.userHabitId)
                            },
                            showStreakCelebration = habit.userHabitId == uiState.celebratingHabitId
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = BloomTheme.colors.disabled.copy(alpha = 0.3f)
                        )
                    }
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
    onHabitClicked: () -> Unit,
    showStreakCelebration: Boolean = false
) {
    // Celebration animation
    var isShowingConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(showStreakCelebration) {
        if (showStreakCelebration) {
            isShowingConfetti = true
            delay(2000) // Show celebration for 2 seconds
            isShowingConfetti = false
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
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

                // Only show time of day if not grouped by time of day
                /* Removed since we now group by time of day
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = habit.timeOfDay.name,
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.textColor.secondary
                )
                */

                // Streak info if available
                if (streakInfo != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        val currentStreak = streakInfo.currentStreak
                        val longestStreak = streakInfo.longestStreak

                        // Enhanced current streak visualization
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(Res.string.calendar_streak_current))
                                append(": ")
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentStreak > 7) BloomTheme.colors.success else BloomTheme.colors.textColor.primary
                                    )
                                ) {
                                    append(currentStreak.toString())

                                    // Add fire emoji for impressive streaks
                                    if (currentStreak > 7) append(" 🔥")
                                }
                            },
                            style = BloomTheme.typography.small
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(Res.string.calendar_streak_longest))
                                append(": ")
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = if (longestStreak > 14) BloomTheme.colors.success else BloomTheme.colors.textColor.primary
                                    )
                                ) {
                                    append(longestStreak.toString())

                                    // Add trophy emoji for impressive streaks
                                    if (longestStreak > 14) append(" 🏆")
                                }
                            },
                            style = BloomTheme.typography.small
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

        // Confetti celebration on streak milestone
        if (isShowingConfetti) {
            // Simple confetti animation (replace with a more sophisticated implementation if needed)
            Text(
                text = "🎉 Streak milestone! 🎊",
                color = BloomTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 4.dp)
            )
        }
    }
}