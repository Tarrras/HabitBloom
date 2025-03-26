package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.best_completion_streak
import habitbloom.composeapp.generated.resources.best_streak
import habitbloom.composeapp.generated.resources.current_completion_streak
import habitbloom.composeapp.generated.resources.current_streak
import habitbloom.composeapp.generated.resources.days_plural
import habitbloom.composeapp.generated.resources.habit_records
import habitbloom.composeapp.generated.resources.ic_overall_rate
import habitbloom.composeapp.generated.resources.ic_total_successfully_completed
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
        BloomSurface(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                        icon = painterResource(Res.drawable.current_completion_streak),
                        title = pluralStringResource(
                            Res.plurals.days_plural,
                            uiState.currentStreak,
                            uiState.currentStreak
                        ),
                        subtitle = stringResource(Res.string.current_streak)
                    )
                    VerticalDivider(modifier = Modifier.fillMaxHeight())
                    UserHabitProgressCell(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(Res.drawable.best_completion_streak),
                        title = pluralStringResource(
                            Res.plurals.days_plural,
                            uiState.bestStreak,
                            uiState.bestStreak
                        ),
                        subtitle = stringResource(Res.string.best_streak)
                    )
                }
                HorizontalDivider(modifier = Modifier.fillMaxHeight())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min)
                ) {
                    UserHabitProgressCell(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(Res.drawable.ic_total_successfully_completed),
                        title = pluralStringResource(
                            Res.plurals.days_plural,
                            uiState.totalDone,
                            uiState.totalDone
                        ),
                        subtitle = stringResource(Res.string.total_done)
                    )
                    VerticalDivider(modifier = Modifier.fillMaxHeight())
                    UserHabitProgressCell(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(Res.drawable.ic_overall_rate),
                        title = "${uiState.overallRate.toString(0)} %",
                        subtitle = stringResource(Res.string.overall_rate)
                    )
                }
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = icon,
            contentDescription = subtitle,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}