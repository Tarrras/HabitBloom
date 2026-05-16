package com.horizondev.habitbloom.screens.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.formatToMmDdYy
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.getTitle
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
import habitbloom.composeapp.generated.resources.calendar_future_date_message
import habitbloom.composeapp.generated.resources.calendar_habit_detail_date
import habitbloom.composeapp.generated.resources.calendar_no_habits
import habitbloom.composeapp.generated.resources.calendar_past_date_message
import habitbloom.composeapp.generated.resources.calendar_screen_title
import habitbloom.composeapp.generated.resources.calendar_statistics_completed
import habitbloom.composeapp.generated.resources.calendar_statistics_title
import habitbloom.composeapp.generated.resources.calendar_statistics_total
import habitbloom.composeapp.generated.resources.calendar_streak_current
import habitbloom.composeapp.generated.resources.calendar_streak_longest
import habitbloom.composeapp.generated.resources.calendar_week_no_habits
import habitbloom.composeapp.generated.resources.days
import habitbloom.composeapp.generated.resources.habit_day_state_completed
import habitbloom.composeapp.generated.resources.habit_day_state_missed
import habitbloom.composeapp.generated.resources.this_week_short_label
import habitbloom.composeapp.generated.resources.today
import habitbloom.composeapp.generated.resources.tomorrow
import habitbloom.composeapp.generated.resources.weekly_habit_tracking
import habitbloom.composeapp.generated.resources.weekly_progress_title
import habitbloom.composeapp.generated.resources.yesterday
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
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

    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is CalendarUiIntent.OpenHabitDetails -> {}
        }
    }

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
            handleUiEvent = viewModel::handleUiEvent
        )
    }
}

@Composable
private fun CalendarHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarGlyph()
            Text(
                text = title,
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(Res.string.calendar_statistics_title),
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

@Composable
private fun CalendarGlyph(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        val accent = Color(0xFF22D3EE)
        drawRoundRect(
            color = accent,
            topLeft = Offset(2.dp.toPx(), 4.dp.toPx()),
            size = Size(size.width - 4.dp.toPx(), size.height - 5.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = stroke
        )
        drawLine(
            color = accent,
            start = Offset(6.dp.toPx(), 2.dp.toPx()),
            end = Offset(6.dp.toPx(), 6.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = accent,
            start = Offset(size.width - 6.dp.toPx(), 2.dp.toPx()),
            end = Offset(size.width - 6.dp.toPx(), 6.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = accent,
            start = Offset(2.dp.toPx(), 9.dp.toPx()),
            end = Offset(size.width - 2.dp.toPx(), 9.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CalendarStatisticsCards(
    stats: MonthlyStatistics,
    modifier: Modifier = Modifier
) {
    val missedHabits = (stats.totalHabits - stats.completedHabits).coerceAtLeast(0)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarMetricCard(
            value = "${(stats.completionRate * 100).toInt()}%",
            label = stringResource(Res.string.calendar_statistics_total),
            accentColor = BloomTheme.colors.primary,
            marker = CalendarMetricMarker.Ring,
            modifier = Modifier.weight(1f)
        )
        CalendarMetricCard(
            value = stats.completedHabits.toString(),
            label = stringResource(Res.string.calendar_statistics_completed),
            accentColor = BloomTheme.colors.success,
            marker = CalendarMetricMarker.Dot,
            modifier = Modifier.weight(1f)
        )
        CalendarMetricCard(
            value = missedHabits.toString(),
            label = stringResource(Res.string.habit_day_state_missed),
            accentColor = BloomTheme.colors.destructive.copy(alpha = 0.75f),
            marker = CalendarMetricMarker.Dot,
            modifier = Modifier.weight(1f)
        )
    }
}

private enum class CalendarMetricMarker {
    Ring,
    Dot
}

@Composable
private fun CalendarMetricCard(
    value: String,
    label: String,
    accentColor: Color,
    marker: CalendarMetricMarker,
    modifier: Modifier = Modifier
) {
    BloomSurface(
        modifier = modifier.height(88.dp),
        color = BloomTheme.colors.glassBackground,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BloomTheme.colors.glassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CalendarMetricMarkerView(
                color = accentColor,
                marker = marker
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = value,
                style = BloomTheme.typography.subheading,
                color = accentColor,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = label,
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarMetricMarkerView(
    color: Color,
    marker: CalendarMetricMarker,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(18.dp),
        contentAlignment = Alignment.Center
    ) {
        when (marker) {
            CalendarMetricMarker.Ring -> Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = color.copy(alpha = 0.25f),
                    radius = size.minDimension / 2f,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = color,
                    radius = 2.5.dp.toPx()
                )
            }

            CalendarMetricMarker.Dot -> {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(color.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                }
            }
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
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BloomTheme.colors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())
        Spacer(modifier = Modifier.height(20.dp))

        CalendarHeader(
            title = stringResource(Res.string.calendar_screen_title)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time of day filter chips
        TimeOfDayFilterChips(
            selectedFilter = uiState.selectedTimeOfDayFilter,
            onFilterSelected = { timeOfDay ->
                handleUiEvent(CalendarUiEvent.FilterByTimeOfDay(timeOfDay))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalendarStatisticsCards(
            stats = uiState.monthlyStats,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        FullScreenCalendar(
            uiState = uiState,
            onDateSelected = { date ->
                handleUiEvent(CalendarUiEvent.SelectDate(date))
            },
            onMonthChanged = { yearMonth ->
                handleUiEvent(CalendarUiEvent.ChangeMonth(yearMonth))
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        WeeklyHabitsContainer(
            uiState = uiState,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

    }

    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                handleUiEvent(CalendarUiEvent.CloseBottomSheet)
            },
            sheetState = sheetState,
            containerColor = BloomTheme.colors.background,
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
                onHabitStatusChanged = { habitRecordId, completed ->
                    handleUiEvent(
                        CalendarUiEvent.ToggleHabitCompletion(
                            habitRecordId = habitRecordId,
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
    onMonthChanged: (YearMonth) -> Unit,
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

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CalendarMonthControls(
            currentMonth = visibleMonth.yearMonth,
            completedHabits = uiState.monthlyStats.completedHabits,
            totalHabits = uiState.monthlyStats.totalHabits,
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

        BloomSurface(
            modifier = Modifier.fillMaxWidth(),
            color = BloomTheme.colors.glassBackgroundStrong,
            shape = RoundedCornerShape(21.dp),
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BloomTheme.colors.glassBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp)
            ) {
                HorizontalCalendar(
                    state = state,
                    dayContent = { calendarDay ->
                        val date = calendarDay.date
                        val kotlinDate = LocalDate(
                            year = date.year,
                            month = date.month,
                            day = date.day
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
                        CalendarWeekHeader(
                            daysOfWeek = daysOfWeek,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 12.dp),
                    color = BloomTheme.colors.border.copy(alpha = 0.28f)
                )

                CalendarProgressLegend(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthControls(
    currentMonth: YearMonth,
    completedHabits: Int,
    totalHabits: Int,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CalendarNavigationButton(
            onClick = goToPrevious,
            direction = CalendarNavigationDirection.Previous
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${currentMonth.month.getTitle()} ${currentMonth.year}",
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(BloomTheme.colors.primary.copy(alpha = 0.55f), CircleShape)
                )
                Text(
                    text = "$completedHabits/$totalHabits ${stringResource(Res.string.calendar_statistics_completed)}",
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.textColor.secondary
                )
            }
        }

        CalendarNavigationButton(
            onClick = goToNext,
            direction = CalendarNavigationDirection.Next
        )
    }
}

private enum class CalendarNavigationDirection {
    Previous,
    Next
}

@Composable
private fun CalendarNavigationButton(
    onClick: () -> Unit,
    direction: CalendarNavigationDirection,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .background(BloomTheme.colors.surfaceVariant, RoundedCornerShape(14.dp))
            .border(1.dp, BloomTheme.colors.border.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (direction) {
                CalendarNavigationDirection.Previous -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
                CalendarNavigationDirection.Next -> Icons.AutoMirrored.Filled.KeyboardArrowRight
            },
            contentDescription = when (direction) {
                CalendarNavigationDirection.Previous -> "previous_month"
                CalendarNavigationDirection.Next -> "next_month"
            },
            tint = BloomTheme.colors.textColor.primary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun CalendarWeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysOfWeek.forEach { day ->
            Text(
                modifier = Modifier.weight(1f),
                text = day.getShortTitle(),
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CalendarProgressLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CalendarLegendItem(
            label = stringResource(Res.string.habit_day_state_completed),
            marker = CalendarLegendMarker.Completed
        )
        CalendarLegendItem(
            label = stringResource(Res.string.habit_day_state_missed),
            marker = CalendarLegendMarker.Missed
        )
        CalendarLegendItem(
            label = stringResource(Res.string.today),
            marker = CalendarLegendMarker.Today
        )
    }
}

private enum class CalendarLegendMarker {
    Completed,
    Missed,
    Today
}

@Composable
private fun CalendarLegendItem(
    label: String,
    marker: CalendarLegendMarker,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when (marker) {
            CalendarLegendMarker.Completed -> Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(BloomTheme.colors.primary, CircleShape)
            )

            CalendarLegendMarker.Missed -> Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(BloomTheme.colors.surface, CircleShape)
                    .border(1.dp, BloomTheme.colors.border.copy(alpha = 0.65f), CircleShape)
            )

            CalendarLegendMarker.Today -> Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(BloomTheme.colors.surface, CircleShape)
                    .border(2.dp, BloomTheme.colors.primary.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(BloomTheme.colors.primary.copy(alpha = 0.45f), CircleShape)
                )
            }
        }

        Text(
            text = label,
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

@Composable
private fun WeeklyHabitsContainer(
    uiState: CalendarUiState,
    modifier: Modifier = Modifier
) {
    val today = getCurrentDate()
    var weekOffset by remember(uiState.selectedDate) { mutableStateOf(0) }
    val selectedWeekStart =
        remember(uiState.selectedDate) { uiState.selectedDate.calculateStartOfWeek() }
    val weekStart = remember(selectedWeekStart, weekOffset) {
        selectedWeekStart.plusDays(weekOffset * 7L)
    }
    val weekDates = remember(weekStart) {
        (0..6).map { offset -> weekStart.plusDays(offset.toLong()) }
    }
    val filteredHabitsByDate = remember(
        uiState.habitsByDate,
        uiState.selectedTimeOfDayFilter,
        weekDates
    ) {
        weekDates.associateWith { date ->
            val habits = uiState.habitsByDate[date].orEmpty()
            if (uiState.selectedTimeOfDayFilter != null) {
                habits.filter { it.timeOfDay == uiState.selectedTimeOfDayFilter }
            } else {
                habits
            }
        }
    }
    val rows = remember(filteredHabitsByDate) {
        filteredHabitsByDate.values
            .flatten()
            .groupBy { it.userHabitId }
            .map { (_, records) ->
                WeeklyHabitRowData(
                    userHabitId = records.first().userHabitId,
                    name = records.first().name,
                    timeOfDay = records.first().timeOfDay,
                    daysStreak = records.maxOfOrNull { it.daysStreak } ?: 0,
                    recordsByDate = records.associateBy { it.date }
                )
            }
            .sortedWith(compareBy<WeeklyHabitRowData> { it.timeOfDay.ordinal }.thenBy { it.name })
    }
    val weeklyTotalHabits = remember(filteredHabitsByDate) {
        filteredHabitsByDate.values.sumOf { it.size }
    }
    val weeklyCompletedHabits = remember(filteredHabitsByDate) {
        filteredHabitsByDate.values.sumOf { habits -> habits.count { it.isCompleted } }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(Res.string.weekly_progress_title),
            style = BloomTheme.typography.subheading,
            color = BloomTheme.colors.textColor.primary,
            fontWeight = FontWeight.SemiBold
        )

        WeeklyHabitsWeekSwitcher(
            weekDates = weekDates,
            weekOffset = weekOffset,
            completedHabits = weeklyCompletedHabits,
            totalHabits = weeklyTotalHabits,
            onPreviousWeek = { weekOffset -= 1 },
            onNextWeek = { weekOffset += 1 },
            onResetWeek = { weekOffset = 0 },
            modifier = Modifier.fillMaxWidth()
        )

        BloomSurface(
            modifier = Modifier.fillMaxWidth(),
            color = BloomTheme.colors.glassBackgroundStrong,
            shape = RoundedCornerShape(21.dp),
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BloomTheme.colors.glassBorder)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.width(500.dp)
                    ) {
                        WeeklyHabitsHeader(weekDates = weekDates)
                        rows.take(7).forEachIndexed { index, row ->
                            WeeklyHabitMatrixRow(
                                row = row,
                                weekDates = weekDates,
                                today = today,
                                alternate = index % 2 == 0
                            )
                        }
                    }
                }

                if (rows.isEmpty()) {
                    WeeklyHabitsEmptyState(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                WeeklyHabitsLegend(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private data class WeeklyHabitRowData(
    val userHabitId: Long,
    val name: String,
    val timeOfDay: TimeOfDay,
    val daysStreak: Int,
    val recordsByDate: Map<LocalDate, UserHabitRecordFullInfo>
)

@Composable
private fun WeeklyHabitsWeekSwitcher(
    weekDates: List<LocalDate>,
    weekOffset: Int,
    completedHabits: Int,
    totalHabits: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onResetWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(54.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CalendarNavigationButton(
            onClick = onPreviousWeek,
            direction = CalendarNavigationDirection.Previous,
            modifier = Modifier.size(36.dp)
        )

        BloomSurface(
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            color = BloomTheme.colors.glassBackground,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BloomTheme.colors.glassBorder),
            shadowElevation = 2.dp,
            onClick = onResetWeek.takeIf { weekOffset != 0 }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = weeklySwitcherTitle(weekDates, weekOffset),
                        style = BloomTheme.typography.body,
                        color = if (weekOffset == 0) {
                            BloomTheme.colors.primary
                        } else {
                            BloomTheme.colors.textColor.primary
                        },
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$completedHabits/$totalHabits ${stringResource(Res.string.calendar_statistics_completed)}",
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        CalendarNavigationButton(
            onClick = onNextWeek,
            direction = CalendarNavigationDirection.Next,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun weeklySwitcherTitle(
    weekDates: List<LocalDate>,
    weekOffset: Int
): String {
    val firstDate = weekDates.first()
    val lastDate = weekDates.last()
    val range = if (firstDate.month == lastDate.month) {
        "${firstDate.day} - ${lastDate.day} ${firstDate.month.getTitle()}"
    } else {
        "${firstDate.day} ${firstDate.month.getTitle()} - ${lastDate.day} ${lastDate.month.getTitle()}"
    }

    return if (weekOffset == 0) {
        "${stringResource(Res.string.this_week_short_label)} • $range"
    } else {
        range
    }
}

@Composable
private fun WeeklyHabitsEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(118.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.calendar_week_no_habits),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun WeeklyHabitsHeader(
    weekDates: List<LocalDate>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(BloomTheme.colors.surface.copy(alpha = 0.95f))
            .border(
                width = 0.5.dp,
                color = BloomTheme.colors.border.copy(alpha = 0.35f)
            )
            .padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.weekly_habit_tracking),
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.secondary.copy(alpha = 0.75f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2.3f)
        )

        weekDates.forEach { date ->
            val isToday = date == getCurrentDate()
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfWeek.getShortTitle(),
                    style = BloomTheme.typography.small,
                    color = if (isToday) {
                        BloomTheme.colors.primary
                    } else {
                        BloomTheme.colors.textColor.secondary.copy(alpha = 0.7f)
                    },
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = date.day.toString(),
                    style = BloomTheme.typography.body,
                    color = if (isToday) {
                        BloomTheme.colors.primary
                    } else {
                        BloomTheme.colors.textColor.primary.copy(alpha = 0.8f)
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun WeeklyHabitMatrixRow(
    row: WeeklyHabitRowData,
    weekDates: List<LocalDate>,
    today: LocalDate,
    alternate: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(63.dp)
            .background(
                if (alternate) {
                    BloomTheme.colors.surface.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                }
            )
            .border(
                width = 0.5.dp,
                color = BloomTheme.colors.border.copy(alpha = 0.16f)
            )
            .padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(2.3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WeeklyHabitIcon(
                label = row.name.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = row.name,
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    maxLines = 1
                )
                Text(
                    text = "${row.daysStreak} ${stringResource(Res.string.days)}",
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.textColor.secondary
                )
            }
        }

        weekDates.forEach { date ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                WeeklyHabitStatusCell(
                    state = weeklyStatusFor(
                        record = row.recordsByDate[date],
                        date = date,
                        today = today
                    ),
                    isToday = date == today
                )
            }
        }
    }
}

@Composable
private fun WeeklyHabitIcon(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(35.dp)
            .background(BloomTheme.colors.surface, RoundedCornerShape(14.dp))
            .border(1.dp, BloomTheme.colors.border.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private enum class WeeklyHabitStatus {
    Completed,
    Missed,
    Today,
    Empty
}

private fun weeklyStatusFor(
    record: UserHabitRecordFullInfo?,
    date: LocalDate,
    today: LocalDate
): WeeklyHabitStatus {
    return when {
        record?.isCompleted == true -> WeeklyHabitStatus.Completed
        record != null && date < today -> WeeklyHabitStatus.Missed
        record != null && date == today -> WeeklyHabitStatus.Today
        else -> WeeklyHabitStatus.Empty
    }
}

@Composable
private fun WeeklyHabitStatusCell(
    state: WeeklyHabitStatus,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    when (state) {
        WeeklyHabitStatus.Completed -> Box(
            modifier = modifier
                .size(28.dp)
                .background(BloomTheme.colors.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = BloomTheme.colors.primaryForeground,
                modifier = Modifier.size(15.dp)
            )
        }

        WeeklyHabitStatus.Missed -> Box(
            modifier = modifier
                .size(27.dp)
                .background(BloomTheme.colors.surface, CircleShape)
                .border(1.dp, BloomTheme.colors.border.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = BloomTheme.colors.textColor.secondary.copy(alpha = 0.35f),
                modifier = Modifier.size(13.dp)
            )
        }

        WeeklyHabitStatus.Today -> Box(
            modifier = modifier
                .size(28.dp)
                .background(BloomTheme.colors.surface, CircleShape)
                .border(2.dp, BloomTheme.colors.primary.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(BloomTheme.colors.primary.copy(alpha = 0.45f), CircleShape)
            )
        }

        WeeklyHabitStatus.Empty -> Box(
            modifier = modifier
                .size(27.dp)
                .border(
                    width = 1.dp,
                    color = BloomTheme.colors.border.copy(alpha = if (isToday) 0.45f else 0.28f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun WeeklyHabitsLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(43.dp)
            .background(BloomTheme.colors.surface.copy(alpha = 0.9f))
            .border(0.5.dp, BloomTheme.colors.border.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CalendarLegendItem(
            label = stringResource(Res.string.habit_day_state_completed),
            marker = CalendarLegendMarker.Completed
        )
        CalendarLegendItem(
            label = stringResource(Res.string.habit_day_state_missed),
            marker = CalendarLegendMarker.Missed
        )
        CalendarLegendItem(
            label = stringResource(Res.string.today),
            marker = CalendarLegendMarker.Today
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

    // Is day part of current month
    val isOutOfMonth = calendarDay.position != com.kizitonwose.calendar.core.DayPosition.MonthDate
    val completedHabits = remember(habits) { habits.count { it.isCompleted } }
    val progress = remember(completedHabits, habits.size) {
        if (habits.isNotEmpty()) completedHabits.toFloat() / habits.size else 0f
    }
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f))
    val dayTextColor = when {
        isOutOfMonth -> BloomTheme.colors.textColor.disabled.copy(alpha = 0.7f)
        isToday || isSelected -> BloomTheme.colors.primary
        hasHabits -> BloomTheme.colors.textColor.primary
        else -> BloomTheme.colors.textColor.secondary
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable(enabled = hasHabits) { onDateClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularCalendarDayProgress(
            progress = animatedProgress,
            showTrack = hasHabits || isToday || isSelected,
            isSelected = isSelected,
            isToday = isToday,
            isOutOfMonth = isOutOfMonth,
            isWeekend = isWeekend,
            modifier = Modifier
                .align(Alignment.Center)
                .size(38.dp)
        ) {
            Text(
                text = calendarDay.date.day.toString(),
                style = BloomTheme.typography.body.copy(
                    fontWeight = if (isSelected || isToday || hasHabits) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Normal
                    }
                ),
                color = dayTextColor
            )
        }
    }
}

@Composable
private fun CircularCalendarDayProgress(
    progress: Float,
    showTrack: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    isOutOfMonth: Boolean,
    isWeekend: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val trackColor = when {
        isOutOfMonth -> BloomTheme.colors.border.copy(alpha = 0.18f)
        isWeekend -> BloomTheme.colors.border.copy(alpha = 0.32f)
        else -> BloomTheme.colors.border.copy(alpha = 0.42f)
    }
    val progressColor = BloomTheme.colors.primary
    val currentDayMarkerColor = BloomTheme.colors.primary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (showTrack) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                val arcInset = strokeWidth / 2f
                val ringStroke = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
                val dashedStroke = Stroke(
                    width = 1.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(4.dp.toPx(), 5.dp.toPx())
                    )
                )

                drawCircle(
                    color = trackColor,
                    radius = (size.minDimension - strokeWidth) / 2f,
                    style = if (progress > 0f || isSelected || isToday) ringStroke else dashedStroke
                )

                if (progress > 0f) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = Offset(arcInset, arcInset),
                        size = Size(
                            width = size.width - strokeWidth,
                            height = size.height - strokeWidth
                        ),
                        style = ringStroke
                    )
                }
            }
        }

        content()

        if (isSelected || isToday) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(9.dp)
                    .background(currentDayMarkerColor, CircleShape)
                    .border(2.dp, BloomTheme.colors.background, CircleShape)
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
                        style = BloomTheme.typography.small.copy(
                            fontWeight = if (selectedFilter == null) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selectedFilter == null)
                            BloomTheme.colors.textColor.white
                        else
                            BloomTheme.colors.textColor.primary
                    )
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedFilter == null) BloomTheme.colors.primary else BloomTheme.colors.disabled
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selectedFilter == null)
                        BloomTheme.colors.primary
                    else
                        Color.Transparent,
                    selectedContainerColor = BloomTheme.colors.primary,
                    labelColor = BloomTheme.colors.textColor.primary,
                    selectedLabelColor = BloomTheme.colors.textColor.white
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
                        text = timeOfDay.getTitle(),
                        style = BloomTheme.typography.small.copy(
                            fontWeight = if (selectedFilter == timeOfDay) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selectedFilter == timeOfDay)
                            BloomTheme.colors.textColor.white
                        else
                            BloomTheme.colors.textColor.primary
                    )
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedFilter == timeOfDay) BloomTheme.colors.primary else BloomTheme.colors.disabled
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selectedFilter == timeOfDay)
                        BloomTheme.colors.primary
                    else
                        Color.Transparent,
                    selectedContainerColor = BloomTheme.colors.primary,
                    labelColor = BloomTheme.colors.textColor.primary,
                    selectedLabelColor = BloomTheme.colors.textColor.white
                )
            )
        }
    }
}

@Composable
private fun DailyHabitDetailBottomSheet(
    uiState: CalendarUiState,
    onHabitStatusChanged: (Long, Boolean) -> Unit,
    onHabitClicked: (Long) -> Unit,
) {
    val date = uiState.selectedDate
    val habits = uiState.habitsForSelectedDate
    val habitStreaks = uiState.habitsWithStreaks
    val today = getCurrentDate() // Get current date for comparison
    val todayString = stringResource(Res.string.today)
    val yesterdayString = stringResource(Res.string.yesterday)
    val tomorrowString = stringResource(Res.string.tomorrow)

    Surface(
        color = BloomTheme.colors.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
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
                        today -> todayString
                        today.minusDays(1) -> yesterdayString
                        today.plusDays(1) -> tomorrowString
                        else -> date.formatToMmDdYy()
                    }
                }

                Text(
                    text = stringResource(Res.string.calendar_habit_detail_date, formattedDate),
                    style = BloomTheme.typography.subheading,
                    color = BloomTheme.colors.textColor.primary
                )
            }

            // Show message for non-today dates
            if (date != today) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (date < today) {
                        stringResource(Res.string.calendar_past_date_message)
                    } else {
                        stringResource(Res.string.calendar_future_date_message)
                    },
                    style = BloomTheme.typography.small,
                    color = BloomTheme.colors.textColor.secondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
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
                // Group habits by time of day
                val habitsByTimeOfDay = habits.groupBy { it.timeOfDay }

                LazyColumn {
                    habitsByTimeOfDay.forEach { (timeOfDay, habitsForTimeOfDay) ->
                        // Time of day header
                        item {
                            Text(
                                text = timeOfDay.getTitle(),
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
                                    onHabitStatusChanged(habit.id, completed)
                                },
                                onHabitClicked = {
                                    onHabitClicked(habit.userHabitId)
                                },
                                showStreakCelebration = habit.userHabitId == uiState.celebratingHabitId,
                                isCurrentDay = date == today
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
}

@Composable
private fun HabitItem(
    habit: UserHabitRecordFullInfo,
    streakInfo: HabitStreakInfo?,
    onStatusChanged: (Boolean) -> Unit,
    onHabitClicked: () -> Unit,
    showStreakCelebration: Boolean = false,
    isCurrentDay: Boolean = false
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
            // Checkbox for completion status - enabled only for current day
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = if (isCurrentDay) {
                    onStatusChanged
                } else {
                    null
                },
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    checkedColor = if (isCurrentDay) BloomTheme.colors.primary else BloomTheme.colors.disabled,
                    uncheckedColor = BloomTheme.colors.disabled,
                    checkmarkColor = BloomTheme.colors.textColor.white
                ),
                enabled = isCurrentDay
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
                                        color = if (currentStreak > 7) BloomTheme.colors.success else BloomTheme.colors.textColor.accent
                                    )
                                ) {
                                    append(currentStreak.toString())

                                    // Add fire emoji for impressive streaks
                                    if (currentStreak > 7) append(" 🔥")
                                }
                            },
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.textColor.secondary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(Res.string.calendar_streak_longest))
                                append(": ")
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = if (longestStreak > 14) BloomTheme.colors.success else BloomTheme.colors.textColor.accent
                                    )
                                ) {
                                    append(longestStreak.toString())

                                    // Add trophy emoji for impressive streaks
                                    if (longestStreak > 14) append(" 🏆")
                                }
                            },
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.textColor.secondary
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
                            BloomTheme.colors.disabled.copy(alpha = 0.5f)
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}
