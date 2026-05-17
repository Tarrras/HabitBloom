package com.horizondev.habitbloom.screens.statistic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnit
import com.horizondev.habitbloom.core.designComponents.pickers.TimeUnitOptionPicker
import com.horizondev.habitbloom.core.designComponents.pickers.getTitle
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.statistic.components.NoCompletedHabitsPlaceholder
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.average_completion
import habitbloom.composeapp.generated.resources.best_completion_streak
import habitbloom.composeapp.generated.resources.best_habit
import habitbloom.composeapp.generated.resources.best_time_of_day
import habitbloom.composeapp.generated.resources.completed_habits_count
import habitbloom.composeapp.generated.resources.completion_percent
import habitbloom.composeapp.generated.resources.days_count
import habitbloom.composeapp.generated.resources.general_statistics
import habitbloom.composeapp.generated.resources.great_work
import habitbloom.composeapp.generated.resources.habit_statistic
import habitbloom.composeapp.generated.resources.ic_chart_proportion_filled
import habitbloom.composeapp.generated.resources.ic_lucid_chart_column
import habitbloom.composeapp.generated.resources.ic_lucid_clock
import habitbloom.composeapp.generated.resources.ic_overall_rate
import habitbloom.composeapp.generated.resources.ic_solid_water_drop
import habitbloom.composeapp.generated.resources.ic_total_successfully_completed
import habitbloom.composeapp.generated.resources.keep_it_up
import habitbloom.composeapp.generated.resources.longest_streak
import habitbloom.composeapp.generated.resources.no_data_available
import habitbloom.composeapp.generated.resources.period_dynamics
import habitbloom.composeapp.generated.resources.statistics_subtitle
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.AreaPlot2
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.floor

@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel,
    onNavigateToAddHabit: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is StatisticUiIntent.OpenHabitDetails -> Unit
            is StatisticUiIntent.NavigateToAddHabit -> onNavigateToAddHabit()
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomTheme.colors.background)
    ) {
        if (uiState.isLoading.not()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 21.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.statusBarsPadding())
                Spacer(modifier = Modifier.height(28.dp))

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
    StatisticsHeader()

    Spacer(modifier = Modifier.height(18.dp))

    TimeUnitOptionPicker(
        modifier = Modifier.fillMaxWidth(),
        selectedOption = uiState.selectedTimeUnit,
        options = TimeUnit.entries,
        onOptionSelected = {
            handleUiEvent(StatisticUiEvent.SelectTimeUnit(it))
        }
    )

    Spacer(modifier = Modifier.height(18.dp))

    HabitDynamicsCard(uiState = uiState)
    Spacer(modifier = Modifier.height(18.dp))
    BestTimeOfDayCard(
        summary = uiState.summary,
        periodLabel = uiState.selectedPeriodLabel
    )
    Spacer(modifier = Modifier.height(18.dp))
    GeneralStatisticsCard(
        summary = uiState.summary,
        periodLabel = uiState.selectedPeriodLabel
    )
    Spacer(modifier = Modifier.height(18.dp))
    BestHabitCard(
        summary = uiState.summary,
        periodLabel = uiState.selectedPeriodLabel
    )
    Spacer(modifier = Modifier.height(18.dp))
    EncouragementCard()
    Spacer(modifier = Modifier.height(54.dp))
}

@Composable
private fun StatisticsHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(Res.drawable.ic_chart_proportion_filled),
                contentDescription = null,
                tint = BloomTheme.colors.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.habit_statistic),
                style = BloomTheme.typography.headlineLarge.copy(fontSize = 21.sp),
                color = BloomTheme.colors.foreground,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.statistics_subtitle),
            style = BloomTheme.typography.bodyMedium,
            color = BloomTheme.colors.mutedForeground,
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun HabitDynamicsCard(uiState: StatisticUiState) {
    StatisticCard {
        SectionHeader(
            icon = painterResource(Res.drawable.ic_lucid_chart_column),
            title = uiState.selectedPeriodLabel.ifBlank { uiState.selectedTimeUnit.getTitle() },
            subtitle = stringResource(Res.string.period_dynamics)
        )

        Spacer(modifier = Modifier.height(18.dp))

        val chartData = uiState.formattedChartData
        val categories = chartData.getCategories()
        val completedData = chartData.getCompletedData()
        val scheduledData = chartData.getScheduledData()
        val points = remember(categories, completedData, scheduledData) {
            categories.mapIndexed { index, category ->
                val scheduled = scheduledData[category] ?: 0
                val completed = completedData[category] ?: 0
                val rate = if (scheduled == 0) 0f else completed.toFloat() / scheduled * 100f
                Point(index.toFloat(), rate.coerceIn(0f, 100f))
            }
        }

        if (points.isNotEmpty()) {
            val revealProgress = remember(points) { Animatable(0f) }
            LaunchedEffect(points) {
                revealProgress.snapTo(0f)
                revealProgress.animateTo(
                    targetValue = points.lastIndex.toFloat(),
                    animationSpec = tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            val visiblePoints = remember(points, revealProgress.value) {
                points.revealUntil(revealProgress.value)
            }

            KoalaPlotTheme(
                axis = KoalaPlotTheme.axis.copy(
                    color = Color.Transparent,
                    majorGridlineStyle = null,
                    minorGridlineStyle = null
                )
            ) {
                XYGraph(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(146.dp),
                    xAxisModel = rememberFloatLinearAxisModel(
                        range = 0f..points.last().x.coerceAtLeast(1f)
                    ),
                    yAxisModel = rememberFloatLinearAxisModel(range = 0f..100f),
                    xAxisTitle = {},
                    yAxisTitle = {},
                    xAxisLabels = { _: Float -> },
                    yAxisLabels = { _: Float -> },
                    horizontalMajorGridLineStyle = null,
                    horizontalMinorGridLineStyle = null,
                    verticalMajorGridLineStyle = null,
                    verticalMinorGridLineStyle = null
                ) {
                    AreaPlot2(
                        data = visiblePoints,
                        areaBaseline = AreaBaseline.HorizontalLine(0f),
                        areaStyle = AreaStyle(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BloomTheme.colors.primary.copy(alpha = 0.28f),
                                    BloomTheme.colors.primary.copy(alpha = 0.03f)
                                )
                            )
                        ),
                        lineStyle = LineStyle(
                            brush = SolidColor(BloomTheme.colors.primary),
                            strokeWidth = 3.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChartDateLabel(text = chartData.getStartLabel())
                ChartDateLabel(text = chartData.getEndLabel())
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(146.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_data_available),
                    style = BloomTheme.typography.bodyMedium,
                    color = BloomTheme.colors.mutedForeground,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun List<Point<Float, Float>>.revealUntil(progress: Float): List<Point<Float, Float>> {
    if (isEmpty()) return emptyList()
    if (size == 1 || progress <= 0f) return listOf(first())
    if (progress >= lastIndex) return this

    val lowerIndex = floor(progress).toInt().coerceIn(0, lastIndex)
    val upperIndex = (lowerIndex + 1).coerceAtMost(lastIndex)
    val fraction = progress - lowerIndex
    val lowerPoint = this[lowerIndex]
    val upperPoint = this[upperIndex]
    val interpolatedPoint = Point(
        x = lowerPoint.x + (upperPoint.x - lowerPoint.x) * fraction,
        y = lowerPoint.y + (upperPoint.y - lowerPoint.y) * fraction
    )

    return take(lowerIndex + 1) + interpolatedPoint
}

@Composable
private fun BestTimeOfDayCard(
    summary: StatisticSummary,
    periodLabel: String
) {
    StatisticCard {
        SectionHeader(
            icon = painterResource(Res.drawable.ic_lucid_clock),
            title = stringResource(Res.string.best_time_of_day),
            subtitle = periodLabel
        )

        Spacer(modifier = Modifier.height(18.dp))

        TimeOfDay.entries.forEachIndexed { index, timeOfDay ->
            TimeOfDayProgressRow(
                title = timeOfDay.getTitle(),
                rate = summary.timeOfDayCompletionRates[timeOfDay] ?: 0
            )
            if (index != TimeOfDay.entries.lastIndex) {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun GeneralStatisticsCard(
    summary: StatisticSummary,
    periodLabel: String
) {
    StatisticCard {
        SectionHeader(
            icon = painterResource(Res.drawable.ic_lucid_chart_column),
            title = stringResource(Res.string.general_statistics),
            subtitle = periodLabel
        )

        Spacer(modifier = Modifier.height(18.dp))

        StatisticMetricRow(
            icon = painterResource(Res.drawable.ic_total_successfully_completed),
            iconTint = BloomTheme.colors.success,
            label = stringResource(Res.string.completed_habits_count),
            value = summary.completedHabits.toString()
        )
        Spacer(modifier = Modifier.height(14.dp))
        StatisticMetricRow(
            icon = painterResource(Res.drawable.best_completion_streak),
            iconTint = BloomTheme.colors.warning,
            label = stringResource(Res.string.longest_streak),
            value = stringResource(Res.string.days_count, summary.longestStreak)
        )
        Spacer(modifier = Modifier.height(14.dp))
        StatisticMetricRow(
            icon = painterResource(Res.drawable.ic_overall_rate),
            iconTint = BloomTheme.colors.primary,
            label = stringResource(Res.string.average_completion),
            value = "${summary.averageCompletionRate}%"
        )
    }
}

@Composable
private fun BestHabitCard(
    summary: StatisticSummary,
    periodLabel: String
) {
    StatisticCard(
        modifier = Modifier.background(
            brush = Brush.radialGradient(
                colors = listOf(
                    BloomTheme.colors.primary.copy(alpha = 0.1f),
                    Color.Transparent
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = BloomTheme.colors.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = BloomTheme.colors.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_solid_water_drop),
                    contentDescription = null,
                    tint = BloomTheme.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(Res.string.best_habit),
                style = BloomTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                color = BloomTheme.colors.mutedForeground,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (periodLabel.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = periodLabel,
                    style = BloomTheme.typography.labelSmall,
                    color = BloomTheme.colors.mutedForeground,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedValueText(
                text = summary.bestHabitName.ifBlank { stringResource(Res.string.no_data_available) },
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.foreground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BloomTheme.colors.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_solid_water_drop),
                    contentDescription = null,
                    tint = BloomTheme.colors.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                AnimatedValueText(
                    text = stringResource(
                        Res.string.completion_percent,
                        summary.bestHabitCompletionRate
                    ),
                    style = BloomTheme.typography.labelSmall,
                    color = BloomTheme.colors.primary
                )
            }
        }
    }
}

@Composable
private fun EncouragementCard() {
    StatisticCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌟",
                fontSize = 32.sp,
                lineHeight = 36.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.great_work),
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.foreground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.keep_it_up),
                style = BloomTheme.typography.bodySmall,
                color = BloomTheme.colors.mutedForeground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatisticCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BloomSurface(
        modifier = modifier.fillMaxWidth(),
        color = BloomTheme.colors.glassBackgroundStrong,
        shape = RoundedCornerShape(21.dp),
        shadowElevation = 8.dp,
        border = BorderStroke(
            width = 0.6.dp,
            color = BloomTheme.colors.border.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
                .animateContentSize(),
            content = content
        )
    }
}

@Composable
private fun SectionHeader(
    icon: Painter,
    title: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(39.dp)
                .background(
                    color = BloomTheme.colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = BloomTheme.colors.primary,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = BloomTheme.typography.titleMedium,
                color = BloomTheme.colors.foreground,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                AnimatedValueText(
                    text = subtitle,
                    style = BloomTheme.typography.labelSmall,
                    color = BloomTheme.colors.mutedForeground
                )
            }
        }
    }
}

@Composable
private fun TimeOfDayProgressRow(
    title: String,
    rate: Int
) {
    val animatedRate by animateFloatAsState(
        targetValue = rate.coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 450),
        label = "TimeOfDayProgress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = BloomTheme.typography.labelMedium,
                color = BloomTheme.colors.foreground
            )
            AnimatedValueText(
                text = "$rate%",
                style = BloomTheme.typography.labelMedium,
                color = BloomTheme.colors.primary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(CircleShape)
                .background(BloomTheme.colors.inputBackground.copy(alpha = 0.8f))
                .border(
                    width = 0.6.dp,
                    color = BloomTheme.colors.border.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedRate)
                    .height(9.dp)
                    .clip(CircleShape)
                    .background(BloomTheme.colors.primary)
            )
        }
    }
}

@Composable
private fun StatisticMetricRow(
    icon: Painter,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = BloomTheme.colors.inputBackground.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = iconTint.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = BloomTheme.typography.labelMedium,
                color = BloomTheme.colors.foreground
            )
        }

        AnimatedValueText(
            text = value,
            style = BloomTheme.typography.bodyMedium,
            color = BloomTheme.colors.foreground,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AnimatedValueText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null
) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            (fadeIn(animationSpec = tween(180)) + slideInVertically { it / 3 }) togetherWith
                    (fadeOut(animationSpec = tween(120)) + slideOutVertically { -it / 3 })
        },
        label = "AnimatedValueText"
    ) { targetText ->
        Text(
            modifier = modifier,
            text = targetText,
            style = style,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign
        )
    }
}

@Composable
private fun ChartDateLabel(text: String) {
    Text(
        text = text,
        style = BloomTheme.typography.labelSmall,
        color = BloomTheme.colors.mutedForeground.copy(alpha = 0.6f)
    )
}
