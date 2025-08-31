package com.horizondev.habitbloom.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.horizondev.habitbloom.common.navigation.BottomNavItem
import com.horizondev.habitbloom.common.navigation.getBottomNavItems
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.navigation.CommonNavigator
import com.horizondev.habitbloom.core.navigation.NavigationComponent
import com.horizondev.habitbloom.screens.calendar.CalendarScreen
import com.horizondev.habitbloom.screens.calendar.CalendarViewModel
import com.horizondev.habitbloom.screens.garden.GardenFlowGlobalNavEntryPoint
import com.horizondev.habitbloom.screens.garden.gardenNestedFlowGraph
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.AddHabitFlowGlobalNavEntryPoint
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.addHabitFlowGraph
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.CreatePersonalHabitFlowRoute
import com.horizondev.habitbloom.screens.habits.presentation.createHabit.createPersonalHabitFlowGraph
import com.horizondev.habitbloom.screens.habits.presentation.habitDetails.HabitDetailsDestination
import com.horizondev.habitbloom.screens.habits.presentation.habitDetails.HabitDetailsScreen
import com.horizondev.habitbloom.screens.habits.presentation.home.HomeScreen
import com.horizondev.habitbloom.screens.habits.presentation.home.HomeViewModel
import com.horizondev.habitbloom.screens.settings.presentation.SettingsScreen
import com.horizondev.habitbloom.screens.settings.presentation.SettingsViewModel
import com.horizondev.habitbloom.screens.statistic.StatisticScreen
import com.horizondev.habitbloom.screens.statistic.StatisticViewModel
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.flower_garden_round_icon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main screen with bottom navigation tabs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToOnboarding: () -> Unit = {}
) {
    val navController = rememberNavController()
    val commonNavigator: CommonNavigator = koinInject()

    val navItems = getBottomNavItems()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomNavigation = navItems.any {
        currentDestination?.route?.contains(
            it.route::class.qualifiedName.toString()
        ) == true
    }

    Scaffold(
        containerColor = BloomTheme.colors.background,
        content = { innerPadding ->

            NavigationComponent(
                modifier = Modifier.padding(
                    when {
                        showBottomNavigation -> {
                            PaddingValues(bottom = innerPadding.calculateBottomPadding())
                        }

                        else -> PaddingValues(0.dp)
                    }
                ),
                navController = navController,
                navigator = commonNavigator,
                startDestination = BottomNavItem.Home
            ) {
                composable<BottomNavItem.Home> {
                    val viewModel = koinViewModel<HomeViewModel>()
                    HomeScreen(
                        viewModel = viewModel,
                        navigateToHabitDetails = { userHabitId ->
                            navController.navigate(HabitDetailsDestination(userHabitId))
                        },
                        navigateToAddHabit = {
                            navController.navigate(AddHabitFlowGlobalNavEntryPoint)
                        }
                    )
                }

                composable<BottomNavItem.Statistics> {
                    val viewModel = koinViewModel<StatisticViewModel>()
                    StatisticScreen(
                        viewModel = viewModel,
                        onNavigateToAddHabit = {
                            navController.navigate(AddHabitFlowGlobalNavEntryPoint)
                        }
                    )
                }

                composable<BottomNavItem.Calendar> {
                    val viewModel = koinViewModel<CalendarViewModel>()
                    CalendarScreen(viewModel = viewModel)
                }

                composable<BottomNavItem.Settings> {
                    val viewModel = koinViewModel<SettingsViewModel>()
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToOnboarding = onNavigateToOnboarding
                    )
                }

                composable<HabitDetailsDestination> { entry ->
                    val data = entry.toRoute<HabitDetailsDestination>()

                    HabitDetailsScreen(userHabitId = data.habitId, popBackStack = {
                        navController.popBackStack()
                    })
                }

                addHabitFlowGraph(
                    navController = navController,
                    onNavigateToCreateCustomHabit = { categoryId ->
                        navController.navigate(
                            CreatePersonalHabitFlowRoute.CreateHabit(
                                categoryId ?: ""
                            )
                        )
                    }
                )

                gardenNestedFlowGraph(
                    navController = navController
                )

                createPersonalHabitFlowGraph(
                    navController = navController
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (showBottomNavigation) {
                FloatingActionButton(
                    modifier = Modifier.size(48.dp).offset(y = 42.dp),
                    containerColor = BloomTheme.colors.primary,
                    onClick = {
                        navController.navigate(GardenFlowGlobalNavEntryPoint)
                    },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                    ),
                    shape = CircleShape,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.flower_garden_round_icon),
                        contentDescription = "Add Habit",
                        tint = BloomTheme.colors.surface,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNavigation,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }
                )
            ) {
                BottomNavigation(
                    backgroundColor = BloomTheme.colors.surface,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navItems.forEach { navItem ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.hasRoute(navItem.route::class)
                            } == true

                            BottomNavigationItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(navItem.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        navController.graph.findStartDestination().route?.let {
                                            popUpTo(it) {
                                                saveState = true
                                            }
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when navigating back to a tab
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(
                                            resource = if (isSelected) {
                                                navItem.filledIconRes
                                            } else {
                                                navItem.outlinedIconRes
                                            }
                                        ),
                                        contentDescription = navItem.name,
                                        tint = if (isSelected) {
                                            BloomTheme.colors.primary
                                        } else {
                                            BloomTheme.colors.disabled
                                        },
                                    )
                                },
                            )
                        }
                    }
                }
            }
        },
    )
}