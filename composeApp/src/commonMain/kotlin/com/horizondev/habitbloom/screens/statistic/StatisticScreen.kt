package com.horizondev.habitbloom.screens.statistic

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnitOptionPicker
import com.horizondev.habitbloom.core.designComponents.pickers.getTitle
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.statistic.components.NoCompletedHabitsPlaceholder
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getChartBorder
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.completed
import habitbloom.composeapp.generated.resources.completed_habit_statistic
import habitbloom.composeapp.generated.resources.completed_n_times
import habitbloom.composeapp.generated.resources.current_month
import habitbloom.composeapp.generated.resources.current_week
import habitbloom.composeapp.generated.resources.current_year
import habitbloom.composeapp.generated.resources.habit_statistic
import habitbloom.composeapp.generated.resources.monthly_completion_rate
import habitbloom.composeapp.generated.resources.monthly_habit_tracking
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.no_completed_habits_in_this_unit_title
import habitbloom.composeapp.generated.resources.no_data_available
import habitbloom.composeapp.generated.resources.no_habits_found
import habitbloom.composeapp.generated.resources.previous
import habitbloom.composeapp.generated.resources.scheduled
import habitbloom.composeapp.generated.resources.total_habits_done
import habitbloom.composeapp.generated.resources.weekly_completion_rate
import habitbloom.composeapp.generated.resources.weekly_habit_tracking
import habitbloom.composeapp.generated.resources.yearly_completion_rate
import habitbloom.composeapp.generated.resources.yearly_habit_tracking
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.DefaultVerticalBarPosition
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberIntLinearAxisModel
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Statistics screen composable that displays habit statistics.
 */
@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel,
    onNavigateToAddHabit: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is StatisticUiIntent.OpenHabitDetails -> {
                // Navigation will be handled by parent NavHost
            }
            is StatisticUiIntent.NavigateToAddHabit -> {
                onNavigateToAddHabit()
            }
        }
    }

    StatisticScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
    )
}

@Composable
fun StatisticScreenContent(
    uiState: StatisticUiState,
    handleUiEvent: (StatisticUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading.not()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.statusBarsPadding())

                if (!uiState.userHasAnyCompleted) {
                    NoCompletedHabitsPlaceholder(
                        modifier = Modifier.fillMaxSize(),
                        onAddHabitClick = {
                            handleUiEvent(StatisticUiEvent.NavigateToAddHabit)
                        }
                    )
                } else {
                    StatisticScreenColumnContent(
                        uiState = uiState,
                        handleUiEvent = handleUiEvent
                    )
                }
            }
        } else {
            BloomLoadingAnimation(
                modifier = Modifier.align(Alignment.Center).size(200.dp),
            )
        }
    }
}

@Composable
fun ColumnScope.StatisticScreenColumnContent(
    uiState: StatisticUiState,
    handleUiEvent: (StatisticUiEvent) -> Unit
) {
    CombinedHabitStatisticsCard(
        modifier = Modifier.fillMaxWidth(),
        uiState = uiState,
        handleUiEvent = handleUiEvent
    )

    Spacer(modifier = Modifier.height(54.dp))
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun CombinedHabitStatisticsCard(
    modifier: Modifier = Modifier,
    uiState: StatisticUiState,
    handleUiEvent: (StatisticUiEvent) -> Unit
) {
    BloomSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .animateContentSize()
        ) {
            // Title
            Text(
                text = stringResource(Res.string.habit_statistic),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Time unit picker
            TimeUnitOptionPicker(
                modifier = Modifier.fillMaxWidth(),
                selectedOption = uiState.selectedTimeUnit,
                options = TimeUnit.entries,
                onOptionSelected = {
                    handleUiEvent(StatisticUiEvent.SelectTimeUnit(it))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Period Navigation Controls
            val timeUnit = uiState.selectedTimeUnit

            // Period label
            if (uiState.selectedPeriodLabel.isNotEmpty()) {
                Text(
                    text = uiState.selectedPeriodLabel,
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Period navigation row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous period button with text
                val previousLabel = stringResource(Res.string.previous)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = BloomTheme.colors.background.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = BloomTheme.colors.disabled,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { handleUiEvent(StatisticUiEvent.PreviousPeriod) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = previousLabel,
                        tint = BloomTheme.colors.textColor.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = previousLabel,
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.primary
                    )
                }

                // Current period button (centered) - only shown when needed
                if (uiState.selectedPeriodOffset != 0) {
                    val currentLabel = when (timeUnit) {
                        TimeUnit.WEEK -> stringResource(Res.string.current_week)
                        TimeUnit.MONTH -> stringResource(Res.string.current_month)
                        TimeUnit.YEAR -> stringResource(Res.string.current_year)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = BloomTheme.colors.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { handleUiEvent(StatisticUiEvent.CurrentPeriod) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = currentLabel,
                            tint = BloomTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentLabel,
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Empty spacer when we're at current period
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Next period button with text
                val nextLabel = stringResource(Res.string.next)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = BloomTheme.colors.background.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = BloomTheme.colors.disabled,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = uiState.selectedPeriodOffset < 0) {
                            handleUiEvent(StatisticUiEvent.NextPeriod)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = nextLabel,
                        style = BloomTheme.typography.small,
                        color = if (uiState.selectedPeriodOffset < 0)
                            BloomTheme.colors.textColor.primary
                        else BloomTheme.colors.textColor.secondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = nextLabel,
                        tint = if (uiState.selectedPeriodOffset < 0)
                            BloomTheme.colors.textColor.primary
                        else BloomTheme.colors.textColor.secondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 1: Pie Chart of Habits by Time of Day
            Text(
                text = stringResource(Res.string.completed_habit_statistic),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pie Chart Section
            val pieChartData = remember(uiState.completeHabitsByTimeOfDay) {
                uiState.completeHabitsByTimeOfDay.filter { it.value > 0 }
            }
            val pieChartValues = pieChartData.values.map { it.toFloat() }

            if (pieChartValues.sum() > 0) {
                PieChart(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    values = pieChartValues,
                    slice = { index ->
                        val color = pieChartData.entries.elementAt(index).key.getChartBorder()
                        DefaultSlice(color = color, gap = 2f)
                    },
                    maxPieDiameter = 220.dp,
                    minPieDiameter = 220.dp,
                    labelConnector = {},
                    holeSize = 0.75f,
                    holeContent = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    style = BloomTheme.typography.body,
                                    color = BloomTheme.colors.textColor.secondary,
                                    text = stringResource(Res.string.total_habits_done),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    style = BloomTheme.typography.title,
                                    color = BloomTheme.colors.textColor.primary,
                                    text = pieChartValues.sum().roundToInt().toString()
                                )
                            }
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.height(220.dp).fillMaxWidth()) {
                    Text(
                        text = stringResource(
                            Res.string.no_completed_habits_in_this_unit_title,
                            uiState.selectedTimeUnit.getTitle().lowercase()
                        ),
                        style = BloomTheme.typography.heading,
                        color = BloomTheme.colors.textColor.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                    )
                }
            }

            // Time of Day Legend
            if (pieChartValues.sum() > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimeOfDay.entries.forEach { timeOfDay ->
                        val completed = uiState.completeHabitsByTimeOfDay[timeOfDay] ?: 0
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = timeOfDay.getChartBorder(),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timeOfDay.getTitle(),
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(Res.string.completed_n_times))
                                    append(" ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(completed.toString())
                                    }
                                },
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.secondary
                            )
                        }
                    }
                }
            }

            // Divider
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BloomTheme.colors.disabled.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.height(32.dp))

            // SECTION 2: Bar Chart of Habit Completion By Period
            // Chart title based on time unit
            val chartTitle = when (timeUnit) {
                TimeUnit.WEEK -> stringResource(Res.string.weekly_habit_tracking)
                TimeUnit.MONTH -> stringResource(Res.string.monthly_habit_tracking)
                TimeUnit.YEAR -> stringResource(Res.string.yearly_habit_tracking)
            }

            Text(
                text = chartTitle,
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Get the formatted chart data from the ViewModel
            val chartData = uiState.formattedChartData
            val categories = chartData.getCategories()
            val completedData = chartData.getCompletedData()
            val scheduledData = chartData.getScheduledData()

            // Calculate the maximum value for the Y-axis
            val maxCompletedValue = completedData.values.maxOrNull() ?: 0
            val maxScheduledValue = scheduledData.values.maxOrNull() ?: 0
            val yAxisMaxValue = maxOf(maxCompletedValue, maxScheduledValue) + 2

            // Define colors with better visual hierarchy
            val scheduledColor = BloomTheme.colors.primary.copy(alpha = 0.2f)
            val completedColor = BloomTheme.colors.primary

            // Bar Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Legend item for scheduled habits
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = scheduledColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.scheduled),
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }

                // Legend item for completed habits
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = completedColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.completed),
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }

            // Bar Chart
            Spacer(modifier = Modifier.height(16.dp))

            if (categories.isNotEmpty()) {
                KoalaPlotTheme(
                    axis = KoalaPlotTheme.axis.copy(
                        color = BloomTheme.colors.textColor.secondary.copy(alpha = 0.5f),
                        minorGridlineStyle = null,
                        majorGridlineStyle = null
                    )
                ) {
                    XYGraph(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        xAxisModel = CategoryAxisModel(categories = categories),
                        yAxisModel = rememberIntLinearAxisModel(
                            range = 0..yAxisMaxValue,
                            allowZooming = false,
                            allowPanning = false
                        ),
                        xAxisTitle = {},
                        xAxisLabels = { label: String ->
                            Text(
                                text = label,
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.primary
                            )
                        },
                        yAxisLabels = { number: Int ->
                            Text(
                                text = number.toString(),
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.secondary
                            )
                        }
                    ) {
                        // If we have data to display for this time unit
                        if (completedData.isNotEmpty() || scheduledData.isNotEmpty()) {
                            // All scheduled habits (lighter bars in the background)
                            if (scheduledData.isNotEmpty()) {
                                VerticalBarPlot(
                                    data = categories.mapIndexed { index, category ->
                                        val value = scheduledData[category] ?: 0
                                        DefaultVerticalBarPlotEntry(
                                            category, DefaultVerticalBarPosition(
                                                yMax = value,
                                                yMin = 0
                                            )
                                        )
                                    },
                                    bar = {
                                        DefaultVerticalBar(
                                            brush = SolidColor(scheduledColor),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp
                                            ),
                                        )
                                    },
                                    barWidth = 0.6f
                                )
                            }

                            // Completed habits (darker bars in the foreground)
                            if (completedData.isNotEmpty()) {
                                VerticalBarPlot(
                                    data = categories.mapIndexed { index, category ->
                                        val value = completedData[category] ?: 0
                                        DefaultVerticalBarPlotEntry(
                                            category, DefaultVerticalBarPosition(
                                                yMax = value,
                                                yMin = 0
                                            )
                                        )
                                    },
                                    bar = {
                                        DefaultVerticalBar(
                                            brush = SolidColor(completedColor),
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp
                                            ),
                                        )
                                    },
                                    barWidth = 0.6f
                                )
                            }
                        } else {
                            // No data available message
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(Res.string.no_habits_found),
                                    style = BloomTheme.typography.body,
                                    color = BloomTheme.colors.textColor.secondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Add completion rate summary at the bottom
                Spacer(modifier = Modifier.height(16.dp))
                val totalCompletedThisPeriod = completedData.values.sum()
                val totalScheduledThisPeriod = scheduledData.values.sum()

                if (totalScheduledThisPeriod > 0) {
                    val completionRate =
                        (totalCompletedThisPeriod.toFloat() / totalScheduledThisPeriod * 100).roundToInt()

                    val completionText = when (timeUnit) {
                        TimeUnit.WEEK -> stringResource(Res.string.weekly_completion_rate)
                        TimeUnit.MONTH -> stringResource(Res.string.monthly_completion_rate)
                        TimeUnit.YEAR -> stringResource(Res.string.yearly_completion_rate)
                    }

                    Text(
                        text = buildAnnotatedString {
                            append(completionText)
                            append(": ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$completionRate%")
                            }
                        },
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // Empty state when no categories are available
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_data_available),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
