package com.horizondev.habitbloom.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.horizondev.habitbloom.calendar.CalendarScreen
import com.horizondev.habitbloom.calendar.CalendarViewModel
import com.horizondev.habitbloom.common.navigation.BottomNavItem
import com.horizondev.habitbloom.common.navigation.getBottomNavItems
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.navigation.CommonNavigator
import com.horizondev.habitbloom.core.navigation.NavigationComponent
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowGlobalNavEntryPoint
import com.horizondev.habitbloom.habits.presentation.addHabit.addHabitFlowGraph
import com.horizondev.habitbloom.habits.presentation.createHabit.CreatePersonalHabitFlowRoute
import com.horizondev.habitbloom.habits.presentation.createHabit.createPersonalHabitFlowGraph
import com.horizondev.habitbloom.habits.presentation.home.HomeScreen
import com.horizondev.habitbloom.habits.presentation.home.HomeViewModel
import com.horizondev.habitbloom.profile.presentation.ProfileScreen
import com.horizondev.habitbloom.profile.presentation.ProfileViewModel
import com.horizondev.habitbloom.statistic.StatisticScreen
import com.horizondev.habitbloom.statistic.StatisticViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main screen with bottom navigation tabs and a FAB for habit addition.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val commonNavigator: CommonNavigator = koinInject()

    Scaffold(
        containerColor = BloomTheme.colors.background,
        content = { innerPadding ->

            NavigationComponent(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                navigator = commonNavigator,
                startDestination = BottomNavItem.Home
            ) {
                composable<BottomNavItem.Home> {
                    val viewModel = koinViewModel<HomeViewModel>()
                    HomeScreen(viewModel = viewModel)
                }

                composable<BottomNavItem.Statistics> {
                    val viewModel = koinViewModel<StatisticViewModel>()
                    StatisticScreen(viewModel = viewModel)
                }

                composable<BottomNavItem.Calendar> {
                    val viewModel = koinViewModel<CalendarViewModel>()
                    CalendarScreen(viewModel = viewModel)
                }

                composable<BottomNavItem.Profile> {
                    val viewModel = koinViewModel<ProfileViewModel>()
                    ProfileScreen(viewModel = viewModel)
                }

                addHabitFlowGraph(
                    navController = navController,
                    onNavigateToCreateCustomHabit = { timeOfDay ->
                        navController.navigate(CreatePersonalHabitFlowRoute.CreateHabit(timeOfDay))
                    }
                )

                createPersonalHabitFlowGraph(
                    navController = navController
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(48.dp).offset(y = 42.dp),
                containerColor = BloomTheme.colors.primary,
                onClick = {
                    navController.navigate(AddHabitFlowGlobalNavEntryPoint)
                },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                ),
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Habit",
                    tint = BloomTheme.colors.surface,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = BloomTheme.colors.surface,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navItems = getBottomNavItems()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    navItems.forEach { navItem ->
                        BottomNavigationItem(
                            selected = currentRoute == navItem.route,
                            onClick = {
                                navController.navigate(navItem.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
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
                                        resource = DrawableResource(
                                            if (currentRoute == navItem.route) {
                                                navItem.filledIconRes
                                            } else {
                                                navItem.outlinedIconRes
                                            }
                                        )
                                    ),
                                    contentDescription = navItem.title,
                                    tint = if (currentRoute == navItem.route) {
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
        },
    )
}