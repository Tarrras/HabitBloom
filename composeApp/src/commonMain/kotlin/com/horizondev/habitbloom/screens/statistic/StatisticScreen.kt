package com.horizondev.habitbloom.screens.statistic

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import habitbloom.composeapp.generated.resources.completed_habit_statistic
import habitbloom.composeapp.generated.resources.completed_n_times
import habitbloom.composeapp.generated.resources.completed_weekly_habits_statistic
import habitbloom.composeapp.generated.resources.habit_statistic
import habitbloom.composeapp.generated.resources.no_completed_habits_in_this_unit_title
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
    val yAxisMaxValue = maxOf(maxCompletedValue, maxScheduledValue) + 1

    BloomSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Title row with navigation controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.completed_weekly_habits_statistic),
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Only show navigation controls when we have weekly data
            if (uiState.selectedTimeUnit == TimeUnit.WEEK) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    // Previous week button
                    IconButton(
                        onClick = { onEvent(StatisticUiEvent.PreviousWeek) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous week",
                            tint = BloomTheme.colors.textColor.primary
                        )
                    }

                    // Display selected week range if we have one
                    if (uiState.selectedWeekLabel.isNotEmpty()) {
                        Text(
                            text = uiState.selectedWeekLabel,
                            style = BloomTheme.typography.subheading,
                            color = BloomTheme.colors.textColor.primary,
                        )
                    }

                    // Next week button (only enabled if not at current week)
                    IconButton(
                        onClick = { onEvent(StatisticUiEvent.NextWeek) },
                        enabled = uiState.selectedWeekOffset < 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next week",
                            tint = if (uiState.selectedWeekOffset < 0)
                                BloomTheme.colors.textColor.primary
                            else BloomTheme.colors.textColor.secondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            KoalaPlotTheme(
                axis = KoalaPlotTheme.axis.copy(
                    color = Color.Black,
                    minorGridlineStyle = null,
                    majorGridlineStyle = null
                )
            ) {
                XYGraph(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    xAxisModel = CategoryAxisModel(
                        categories = DayOfWeek.entries.map { it.getShortTitle() }
                    ),
                    yAxisModel = rememberIntLinearAxisModel(
                        range = 0..yAxisMaxValue,
                        allowZooming = false,
                        allowPanning = false
                    ),
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
                            DefaultVerticalBar(SolidColor(BloomTheme.colors.primary.copy(alpha = 0.3f)))
                        }
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
                            DefaultVerticalBar(SolidColor(BloomTheme.colors.primary))
                        }
                    )
                }
            }

            // Add legend to explain the bars
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Legend item for scheduled habits
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = BloomTheme.colors.primary.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Scheduled Habits",
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.primary
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Legend item for completed habits
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = BloomTheme.colors.primary,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Completed Habits",
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.primary
                    )
                }
            }
        }
    }
}
