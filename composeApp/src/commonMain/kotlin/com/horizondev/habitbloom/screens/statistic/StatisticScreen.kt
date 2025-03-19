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
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnitOptionPicker
import com.horizondev.habitbloom.core.designComponents.pickers.getTitle
import com.horizondev.habitbloom.core.designComponents.text.ToolbarTitleText
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.statistic.components.NoCompletedHabitsPlaceholder
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getChartBorder
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.completed
import habitbloom.composeapp.generated.resources.completed_habit_statistic
import habitbloom.composeapp.generated.resources.completed_n_times
import habitbloom.composeapp.generated.resources.completed_weekly_habits_statistic
import habitbloom.composeapp.generated.resources.current_week
import habitbloom.composeapp.generated.resources.habit_statistic
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.no_completed_habits_in_this_unit_title
import habitbloom.composeapp.generated.resources.previous
import habitbloom.composeapp.generated.resources.scheduled
import habitbloom.composeapp.generated.resources.weekly_completion_rate
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
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Statistics screen composable that displays habit statistics.
 */
@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is StatisticUiIntent.OpenHabitDetails -> {
                // Navigation will be handled by parent NavHost
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.statusBarsPadding())

            ToolbarTitleText(
                text = stringResource(Res.string.habit_statistic),
                modifier = Modifier.fillMaxWidth()
            )

            if (!uiState.userHasAnyCompleted) {
                NoCompletedHabitsPlaceholder(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                StatisticScreenColumnContent(
                    uiState = uiState,
                    handleUiEvent = handleUiEvent
                )
            }
        }

        BloomLoader(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.isLoading
        )
    }
}

@Composable
fun ColumnScope.StatisticScreenColumnContent(
    uiState: StatisticUiState,
    handleUiEvent: (StatisticUiEvent) -> Unit
) {
    GeneralCompletedHabitsChartCard(
        modifier = Modifier.fillMaxWidth(),
        uiState = uiState,
        handleUiEvent = handleUiEvent
    )

    Spacer(modifier = Modifier.height(16.dp))

    WeeklyCompletedHabitsChartCard(
        modifier = Modifier.fillMaxWidth(),
        uiState = uiState,
        onEvent = handleUiEvent
    )

    Spacer(modifier = Modifier.height(54.dp))
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun GeneralCompletedHabitsChartCard(
    modifier: Modifier = Modifier,
    uiState: StatisticUiState,
    handleUiEvent: (StatisticUiEvent) -> Unit
) {
    val pieChartData = remember(uiState.completeHabitsByTimeOfDay) {
        uiState.completeHabitsByTimeOfDay.filter { it.value > 0 }
    }
    val pieChartValues = pieChartData.values.map { it.toFloat() }

    BloomSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.completed_habit_statistic),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            TimeUnitOptionPicker(
                modifier = Modifier.fillMaxWidth(),
                selectedOption = uiState.selectedTimeUnit,
                options = TimeUnit.entries,
                onOptionSelected = {
                    handleUiEvent(StatisticUiEvent.SelectTimeUnit(it))
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (pieChartValues.sum() > 0) {
                PieChart(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    values = pieChartValues,
                    slice = { index ->
                        val color = pieChartData.entries.elementAt(index).key.getChartBorder()

                        DefaultSlice(color = color, gap = 2f)
                    },
                    maxPieDiameter = 250.dp,
                    minPieDiameter = 250.dp,
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
                                    style = BloomTheme.typography.body,
                                    color = BloomTheme.colors.textColor.secondary,
                                    text = "Total habits done",
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
                Box(modifier = Modifier.height(250.dp).fillMaxWidth()) {
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
                                .size(32.dp)
                                .background(
                                    color = timeOfDay.getChartBorder(),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timeOfDay.getTitle(),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(Res.string.completed_n_times))
                                append(" ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(completed.toString())
                                }
                            },
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.textColor.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun WeeklyCompletedHabitsChartCard(
    modifier: Modifier = Modifier,
    uiState: StatisticUiState,
    onEvent: (StatisticUiEvent) -> Unit = {}
) {
    val completedHabits = uiState.completedHabitsThisWeek
    val allScheduledHabits = uiState.allScheduledHabitsThisWeek

    // Calculate the maximum value for the Y-axis (allowing some space at the top)
    val maxCompletedValue = completedHabits.values.maxOrNull() ?: 0
    val maxScheduledValue = allScheduledHabits.values.maxOrNull() ?: 0
    val yAxisMaxValue = maxOf(maxCompletedValue, maxScheduledValue) + 2

    // Define colors with better visual hierarchy
    val scheduledColor = BloomTheme.colors.primary.copy(alpha = 0.2f)
    val completedColor = BloomTheme.colors.primary

    BloomSurface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .animateContentSize()
        ) {
            // Title
            Text(
                text = stringResource(Res.string.completed_weekly_habits_statistic),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )

            // Week date with improved contrast 
            if (uiState.selectedWeekLabel.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.selectedWeekLabel,
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Navigation controls with text labels
            if (uiState.selectedTimeUnit == TimeUnit.WEEK) {
                Spacer(modifier = Modifier.height(16.dp))

                // Week navigation row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous week button with text
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
                            .clickable { onEvent(StatisticUiEvent.PreviousWeek) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous week",
                            tint = BloomTheme.colors.textColor.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(Res.string.previous),
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.textColor.primary
                        )
                    }

                    // Current week button (centered) - only shown when needed
                    if (uiState.selectedWeekOffset != 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = BloomTheme.colors.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onEvent(StatisticUiEvent.CurrentWeek) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Current week",
                                tint = BloomTheme.colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.current_week),
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // Empty spacer when we're at current week
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Next week button with text (only enabled if not at current week)
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
                            .clickable(enabled = uiState.selectedWeekOffset < 0) {
                                onEvent(StatisticUiEvent.NextWeek)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = stringResource(Res.string.next),
                            style = BloomTheme.typography.small,
                            color = if (uiState.selectedWeekOffset < 0)
                                BloomTheme.colors.textColor.primary
                            else BloomTheme.colors.textColor.secondary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next week",
                            tint = if (uiState.selectedWeekOffset < 0)
                                BloomTheme.colors.textColor.primary
                            else BloomTheme.colors.textColor.secondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Legend 
            Spacer(modifier = Modifier.height(24.dp))
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

            // Rest of the component remains unchanged
            Spacer(modifier = Modifier.height(20.dp))

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
                    xAxisModel = CategoryAxisModel(
                        categories = DayOfWeek.entries.map { it.getShortTitle() }
                    ),
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
                    // All scheduled habits (lighter bars in the background)
                    VerticalBarPlot(
                        data = allScheduledHabits.map { (key, value) ->
                            DefaultVerticalBarPlotEntry(
                                key.getShortTitle(), DefaultVerticalBarPosition(
                                    yMax = value,
                                    yMin = 0
                                )
                            )
                        },
                        bar = {
                            DefaultVerticalBar(
                                brush = SolidColor(scheduledColor),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                            )
                        },
                        barWidth = 0.6f
                    )

                    // Completed habits (darker bars in the foreground)
                    VerticalBarPlot(
                        data = completedHabits.map { (key, value) ->
                            DefaultVerticalBarPlotEntry(
                                key.getShortTitle(), DefaultVerticalBarPosition(
                                    yMax = value,
                                    yMin = 0
                                )
                            )
                        },
                        bar = {
                            DefaultVerticalBar(
                                brush = SolidColor(completedColor),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                            )
                        },
                        barWidth = 0.6f
                    )
                }
            }

            // Add completion rate summary at the bottom
            Spacer(modifier = Modifier.height(16.dp))
            val totalCompletedThisWeek = completedHabits.values.sum()
            val totalScheduledThisWeek = allScheduledHabits.values.sum()

            if (totalScheduledThisWeek > 0) {
                val completionRate =
                    (totalCompletedThisWeek.toFloat() / totalScheduledThisWeek * 100).roundToInt()

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.weekly_completion_rate))
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
        }
    }
}
