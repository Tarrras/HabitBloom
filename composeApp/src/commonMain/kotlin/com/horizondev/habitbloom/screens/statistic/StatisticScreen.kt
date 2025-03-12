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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
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
import com.horizondev.habitbloom.utils.getChartColor
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.completed_habit_statistic
import habitbloom.composeapp.generated.resources.completed_n_times
import habitbloom.composeapp.generated.resources.completed_weekly_habits_statistic
import habitbloom.composeapp.generated.resources.habit_statistic
import habitbloom.composeapp.generated.resources.no_completed_habits_in_this_unit_title
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberIntLinearAxisModel
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

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
    val pieChartValues = remember(uiState.completeHabitsByTimeOfDay) {
        uiState.completeHabitsByTimeOfDay.values.map { it.toFloat() }
    }


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
                options = TimeUnit.entries,
                selectedOption = uiState.selectedTimeUnit,
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
                        val color = when (index) {
                            TimeOfDay.Morning.ordinal -> TimeOfDay.Morning
                            TimeOfDay.Afternoon.ordinal -> TimeOfDay.Afternoon
                            else -> TimeOfDay.Evening
                        }.getChartColor()

                        DefaultSlice(color = color)
                    },
                    maxPieDiameter = 250.dp,
                    minPieDiameter = 250.dp,
                    labelConnector = {}
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
                                .size(24.dp)
                                .background(
                                    color = timeOfDay.getChartColor(),
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
                            text = stringResource(Res.string.completed_n_times, completed),
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
) {
    val pieChartValues = uiState.completedHabitsThisWeek
    val yAxisMaxValue = pieChartValues.values.maxOrNull()?.takeIf { it > 10 } ?: 10

    BloomSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.completed_weekly_habits_statistic),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            KoalaPlotTheme(
                axis = KoalaPlotTheme.axis.copy(
                    color = Color.Black,
                    minorGridlineStyle = null
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
                    panZoomEnabled = false
                ) {
                    LinePlot(
                        data = pieChartValues.map { (key, value) ->
                            Point(key.getShortTitle(), value)
                        },
                        lineStyle = LineStyle(
                            SolidColor(BloomTheme.colors.primary),
                            strokeWidth = 2.dp
                        )
                    )
                }
            }
        }
    }
}
