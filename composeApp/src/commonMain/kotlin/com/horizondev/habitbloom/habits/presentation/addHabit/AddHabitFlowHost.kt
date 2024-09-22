package com.horizondev.habitbloom.habits.presentation.addHabit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.designComponents.stepper.BloomStepper
import com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayChoiceScreen
import com.horizondev.habitbloom.platform.StatusBarColors
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_new_habit
import org.jetbrains.compose.resources.stringResource

class AddHabitFlowHost : Screen {

    @Composable
    override fun Content() {
        AddHabitFlowHostContent(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun AddHabitFlowHostContent(modifier: Modifier = Modifier) {
    Navigator(screen = AddHabitTimeOfDayChoiceScreen()) { navigator ->
        val hostModel = navigator.getNavigatorScreenModel<AddHabitFlowHostModel>()
        val currentPageIndex by hostModel.flowPageState.collectAsState()

        StatusBarColors(
            statusBarColor = BloomTheme.colors.background,
            navBarColor = BloomTheme.colors.background
        )

        Scaffold(
            modifier = modifier,
            topBar = {
                AddHabitFlowHostTopBar(
                    currentPageIndex = currentPageIndex,
                    onBackPressed = {
                        navigator.pop()
                    },
                    onClearPressed = {
                        navigator.popUntilRoot()
                    }
                )
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    CurrentScreen()
                }
            },
            containerColor = BloomTheme.colors.background
        )
    }
}

@Composable
fun AddHabitFlowHostTopBar(
    modifier: Modifier = Modifier,
    currentPageIndex: Int,
    onBackPressed: () -> Unit,
    onClearPressed: () -> Unit,
) {
    Column(modifier = modifier.statusBarsPadding().fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = BloomTheme.colors.textColor.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .align(Alignment.TopStart)
                    .clickable {
                        onBackPressed()
                    },
                contentDescription = "back"
            )
            Text(
                text = stringResource(Res.string.add_new_habit),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Filled.Close,
                tint = BloomTheme.colors.textColor.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        onClearPressed()
                    },
                contentDescription = "close"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        BloomStepper(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            items = AddHabitFlowScreen.entries.map { it.getTitle() },
            currentItemIndex = currentPageIndex
        )
    }
}