package com.horizondev.habitbloom.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.horizondev.habitbloom.common.mainTabs.HabitBloomTab
import com.horizondev.habitbloom.common.mainTabs.TabFilledIcon
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHost

class MainScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(
            tab = HabitBloomTab.HomeTab
        ) {
            val navigator = LocalNavigator.currentOrThrow.parent

            Scaffold(
                containerColor = BloomTheme.colors.background,
                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CurrentScreen()
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier.size(48.dp).offset(y = 42.dp),
                        containerColor = BloomTheme.colors.primary,
                        onClick = {
                            navigator?.push(AddHabitFlowHost())
                        },
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                        ),
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
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
                            TabNavigationItem(HabitBloomTab.HomeTab)
                            TabNavigationItem(HabitBloomTab.ProfileTab)
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val isSelected = tabNavigator.current == tab

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let {
                Icon(
                    painter = if (isSelected) {
                        TabFilledIcon(tab)
                    } else {
                        it
                    },
                    contentDescription = tab.options.title,
                    tint = if (isSelected) {
                        BloomTheme.colors.primary
                    } else {
                        BloomTheme.colors.disabled
                    },
                )
            }
        },
    )
}