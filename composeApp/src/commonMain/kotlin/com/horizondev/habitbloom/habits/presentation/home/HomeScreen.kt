package com.horizondev.habitbloom.habits.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.presentation.habitDetails.HabitDetailsScreen
import com.horizondev.habitbloom.habits.presentation.home.components.AllHabitsCompletedMessage
import com.horizondev.habitbloom.habits.presentation.home.components.DailyHabitProgressWidget
import com.horizondev.habitbloom.habits.presentation.home.components.EmptyHabitsForTimeOfDayPlaceholder
import com.horizondev.habitbloom.habits.presentation.home.components.TimeOfDaySwitcher
import com.horizondev.habitbloom.habits.presentation.home.components.UserHabitItem
import com.horizondev.habitbloom.utils.collectAsEffect
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(modifier: Modifier = Modifier, screeModel: HomeScreenModel) {
    val uiState by screeModel.state.collectAsState()
    val navigator = LocalNavigator.currentOrThrow.parent

    screeModel.uiIntent.collectAsEffect { uiIntent ->
        when (uiIntent) {
            is HomeScreenUiIntent.OpenHabitDetails -> {
                navigator?.push(HabitDetailsScreen(uiIntent.userHabitId))
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        handleUiEvent = screeModel::handleUiEvent
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeScreenUiState,
    handleUiEvent: (HomeScreenUiEvent) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        toolbar(modifier = Modifier.fillMaxWidth())
        dailySummary(uiState = uiState)
        timeOfDaySwitcher(
            uiState = uiState,
            onTimeOfDayChanged = {
                handleUiEvent(HomeScreenUiEvent.SelectTimeOfDay(it))
            }
        )
        habitsList(
            uiState = uiState,
            onHabitStatusChanged = { id, isCompleted ->
                handleUiEvent(HomeScreenUiEvent.ChangeHabitCompletionStatus(id, isCompleted))
            },
            onHabitClicked = {
                handleUiEvent(HomeScreenUiEvent.OpenHabitDetails(it))
            }
        )
    }
}

private fun LazyListScope.toolbar(modifier: Modifier = Modifier) {
    item(key = "toolbar") {
        Text(
            text = stringResource(Res.string.app_name),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private fun LazyListScope.dailySummary(
    uiState: HomeScreenUiState
) {
    item(key = "daily_summary") {
        Spacer(modifier = Modifier.height(24.dp))
        DailyHabitProgressWidget(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            completedHabitsCount = uiState.completedHabitsCount,
            habitsCount = uiState.habitsCount
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun LazyListScope.timeOfDaySwitcher(
    uiState: HomeScreenUiState,
    onTimeOfDayChanged: (TimeOfDay) -> Unit
) {
    item(key = "time_of_day_switcher") {
        Spacer(modifier = Modifier.height(24.dp))
        TimeOfDaySwitcher(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            selectedTimeOfDay = uiState.selectedTimeOfDay,
            onTimeChanged = onTimeOfDayChanged
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.habitsList(
    uiState: HomeScreenUiState,
    onHabitStatusChanged: (Long, Boolean) -> Unit,
    onHabitClicked: (Long) -> Unit
) {
    if (uiState.userHabits.isEmpty()) {
        item(key = "no_habits_placeholder") {
            EmptyHabitsForTimeOfDayPlaceholder(
                selectTimeOfDay = uiState.selectedTimeOfDay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    } else {
        item(key = "list_padding") {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            AnimatedVisibility(
                visible = uiState.userCompletedAllHabitsForTimeOfDay,
                modifier = Modifier.padding(bottom = 24.dp),
                enter = slideInVertically() + fadeIn(),
                exit = fadeOut()
            ) {
                AllHabitsCompletedMessage(
                    timeOfDay = uiState.selectedTimeOfDay,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            }
        }
        items(uiState.userHabits, key = { it.id }) {
            UserHabitItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = 16.dp),
                habitInfo = it,
                onCompletionStatusChanged = onHabitStatusChanged,
                onClick = { onHabitClicked(it.userHabitId) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
