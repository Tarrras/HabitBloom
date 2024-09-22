package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.inputText.BloomSearchTextField
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowScreen
import com.horizondev.habitbloom.habits.presentation.components.HabitListItem
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.choose_habit_to_acquire
import habitbloom.composeapp.generated.resources.search_habit
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class AddHabitChoiceScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val hostModel = navigator.getNavigatorScreenModel<AddHabitFlowHostModel>()

        val screenModel = getScreenModel<AddHabitChoiceScreenModel>() {
            parametersOf(hostModel.getNewHabitInfo().timeOfDay)
        }
        val uiState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            hostModel.updatedFlowPage(AddHabitFlowScreen.CHOOSE_HABIT)
        }

        AddHabitChoiceScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent
        )
    }
}

@Composable
fun AddHabitChoiceScreenContent(
    uiState: AddHabitChoiceUiState,
    handleUiEvent: (AddHabitChoiceUiEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        title(
            inputSearch = uiState.searchInput,
            onSearchChanged = {
                handleUiEvent(AddHabitChoiceUiEvent.PerformSearch(it))
            }
        )
        habits(
            habits = uiState.habits,
            onHabitClicked = {}
        )
    }
}

private fun LazyListScope.title(
    modifier: Modifier = Modifier,
    inputSearch: String,
    onSearchChanged: (String) -> Unit
) {
    item(key = "toolbar") {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.choose_habit_to_acquire),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(24.dp))
        BloomSearchTextField(
            value = inputSearch,
            onValueChange = onSearchChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholderText = stringResource(Res.string.search_habit)
        )
        Spacer(modifier = Modifier.height(24.dp))

    }
}

private fun LazyListScope.habits(
    habits: List<HabitInfo>,
    onHabitClicked: (HabitInfo) -> Unit
) {
    items(habits, key = { it.id }) {
        HabitListItem(
            modifier = Modifier.fillMaxWidth(),
            habitInfo = it,
            onClick = {
                onHabitClicked(it)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}