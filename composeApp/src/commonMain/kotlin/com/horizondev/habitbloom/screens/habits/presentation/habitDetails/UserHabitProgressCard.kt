package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.best_streak
import habitbloom.composeapp.generated.resources.current_streak
import habitbloom.composeapp.generated.resources.days_plural
import habitbloom.composeapp.generated.resources.habit_records
import habitbloom.composeapp.generated.resources.ic_lucid_chart_column
import habitbloom.composeapp.generated.resources.ic_lucid_circle_check_big
import habitbloom.composeapp.generated.resources.ic_lucid_flame
import habitbloom.composeapp.generated.resources.ic_lucid_trophy
import habitbloom.composeapp.generated.resources.overall_rate
import habitbloom.composeapp.generated.resources.total_done
import io.github.koalaplot.core.util.toString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun UserHabitProgressCard(
    modifier: Modifier = Modifier,
    uiState: UserHabitProgressUiState?
) {
    if (uiState != null) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.habit_records),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                UserHabitProgressCell(
                    modifier = Modifier.weight(1f),
                    icon = painterResource(Res.drawable.ic_lucid_flame),
                    title = pluralStringResource(
                        Res.plurals.days_plural,
                        uiState.currentStreak,
                        uiState.currentStreak
                    ),
                    subtitle = stringResource(Res.string.current_streak)
                )

                Spacer(modifier = Modifier.width(16.dp))

                UserHabitProgressCell(
                    modifier = Modifier.weight(1f),
                    icon = painterResource(Res.drawable.ic_lucid_trophy),
                    title = pluralStringResource(
                        Res.plurals.days_plural,
                        uiState.bestStreak,
                        uiState.bestStreak
                    ),
                    subtitle = stringResource(Res.string.best_streak)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                UserHabitProgressCell(
                    modifier = Modifier.weight(1f),
                    icon = painterResource(Res.drawable.ic_lucid_circle_check_big),
                    title = pluralStringResource(
                        Res.plurals.days_plural,
                        uiState.totalDone,
                        uiState.totalDone
                    ),
                    subtitle = stringResource(Res.string.total_done)
                )

                Spacer(modifier = Modifier.width(16.dp))

                UserHabitProgressCell(
                    modifier = Modifier.weight(1f),
                    icon = painterResource(Res.drawable.ic_lucid_chart_column),
                    title = "${uiState.overallRate.toString(0)} %",
                    subtitle = stringResource(Res.string.overall_rate)
                )
            }
        }
    }
}

@Composable
fun UserHabitProgressCell(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    subtitle: String
) {
    BloomSurface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = icon,
                    contentDescription = subtitle,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subtitle,
                    style = BloomTheme.typography.labelMedium,
                    color = BloomTheme.colors.mutedForeground
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.textColor.primary
            )
        }
    }
}