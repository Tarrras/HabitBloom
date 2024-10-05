package com.horizondev.habitbloom.statistic

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
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnitOptionPicker
import com.horizondev.habitbloom.core.designComponents.text.ToolbarTitleText
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.statistic.components.NoCompletedHabitsPlaceholder
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.completed_habit_statistic
import habitbloom.composeapp.generated.resources.completed_n_times
import habitbloom.composeapp.generated.resources.habit_statistic
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatisticScreen(modifier: Modifier = Modifier, screeModel: StatisticScreenModel) {
    val uiState by screeModel.state.collectAsState()

    StatisticScreenContent(
        uiState = uiState,
        handleUiEvent = screeModel::handleUiEvent
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
    CompletedHabitsChartCard(
        modifier = Modifier.fillMaxWidth(),
        uiState = uiState,
        handleUiEvent = handleUiEvent
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun CompletedHabitsChartCard(
    modifier: Modifier = Modifier,
    uiState: StatisticUiState,
    handleUiEvent: (StatisticUiEvent) -> Unit
) {
    val pieChartValues = remember(uiState.completeHabitsCountData) {
        uiState.completeHabitsCountData.values.map { it.toFloat() }
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
                labelConnector = {}
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimeOfDay.entries.forEach { timeOfDay ->
                    val completed = uiState.completeHabitsCountData[timeOfDay] ?: 0
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

@Composable
fun TimeOfDay.getChartColor(): Color {
    return when (this) {
        TimeOfDay.Morning -> Color(0xFFFFD54F)
        TimeOfDay.Afternoon -> Color(0xFFFF7043)
        TimeOfDay.Evening -> Color(0xFF7986CB)
    }
}