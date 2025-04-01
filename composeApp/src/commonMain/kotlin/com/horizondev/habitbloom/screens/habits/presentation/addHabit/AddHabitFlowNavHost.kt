package com.horizondev.habitbloom.screens.habits.presentation.addHabit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designComponents.stepper.BloomStepper
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.navigation.CommonNavigator
import com.horizondev.habitbloom.core.navigation.NavigationComponent
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_new_habit
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * NavHost for the Add Habit flow using type-safe navigation.
 */
@Composable
fun AddHabitFlowNavHost(
    modifier: Modifier = Modifier,
    navigator: CommonNavigator = koinInject(),
    onFinishFlow: () -> Unit,
    onNavigateToCreateCustomHabit: (TimeOfDay?) -> Unit
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }

    // Create shared ViewModel for the entire flow
    val viewModel = koinViewModel<AddHabitFlowViewModel>()

    // Observe the state
    val flowState by viewModel.state.collectAsState()

    // Handle UI intents
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                AddHabitFlowUiIntent.ExitFlow -> {
                    onFinishFlow()
                }

                is AddHabitFlowUiIntent.ShowShackbar -> {
                    coroutineScope.launch {
                        snackBarState.showSnackbar(uiIntent.visuals)
                    }
                }
            }
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = remember(backStackEntry) {
        backStackEntry?.destination?.getAddHabitRouteInfo()
    }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            when {
                currentRoute?.step != null -> {
                    AddHabitFlowHostTopBar(
                        currentPageIndex = currentRoute.step.ordinal,
                        onBackPressed = if (currentRoute.step.ordinal in 1..<AddHabitFlowScreenStep.entries.lastIndex) {
                            { navController.popBackStack() }
                        } else null,
                        onClearPressed = { onFinishFlow() }
                    )
                }
            }
        },
        content = { paddingValues ->
            // Navigation host with type-safe routes
            NavigationComponent(
                modifier = Modifier.padding(
                    if (currentRoute?.step != null) {
                        paddingValues
                    } else PaddingValues(0.dp)
                ),
                navController = navController,
                navigator = navigator,
                startDestination = AddHabitFlowGlobalNavEntryPoint
            ) {
                // Add all routes from our sealed class
                createHabitNestedFlowGraph(
                    navController = navController,
                    viewModel = viewModel,
                    onNavigateToCreateCustomHabit = { timeOfDay ->
                        onNavigateToCreateCustomHabit(timeOfDay)
                    }
                )
            }
        },
        containerColor = BloomTheme.colors.background,
        snackbarHost = {
            BloomSnackbarHost(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                snackBarState = snackBarState
            )
        }
    )
}

@Composable
fun AddHabitFlowHostTopBar(
    modifier: Modifier = Modifier,
    currentPageIndex: Int,
    onBackPressed: (() -> Unit)? = null,
    onClearPressed: () -> Unit,
) {
    Column(modifier = modifier.statusBarsPadding().fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            onBackPressed?.let {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = BloomTheme.colors.textColor.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(24.dp)
                        .align(Alignment.TopStart)
                        .clickable { onBackPressed() },
                    contentDescription = "back"
                )
            }
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
                    .clickable { onClearPressed() },
                contentDescription = "close"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        BloomStepper(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            items = AddHabitFlowScreenStep.entries.map { it.getTitle() },
            currentItemIndex = currentPageIndex
        )
    }
}