package com.horizondev.habitbloom.habits.presentation.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.presentation.home.components.DailyHabitProgressWidget
import com.horizondev.habitbloom.habits.presentation.home.components.TimeOfDaySwitcher
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(modifier: Modifier = Modifier, screeModel: HomeScreenModel) {
    val uiState by screeModel.state.collectAsState()

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
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        toolbar(modifier = Modifier.fillMaxWidth())
        dailySummary(uiState = uiState)
        timeOfDaySwitcher(
            uiState = uiState,
            onTimeOfDayChanged = {
                handleUiEvent(HomeScreenUiEvent.SelectTimeOfDay(it))
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
