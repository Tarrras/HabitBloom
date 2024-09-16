package com.horizondev.habitbloom.common.mainTabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.horizondev.habitbloom.habits.presentation.home.HomeScreen
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_home_outlined
import habitbloom.composeapp.generated.resources.ic_profile_outlined
import org.jetbrains.compose.resources.painterResource


sealed class HabitBloomTab {
    internal data object HomeTab : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = toString()
                val icon = painterResource(Res.drawable.ic_home_outlined)

                return remember {
                    TabOptions(
                        index = 0u,
                        title = title,
                        icon = icon,
                    )
                }
            }

        @Composable
        override fun Content() {
            HomeScreen()
        }
    }

    internal data object ProfileTab : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = toString()
                val icon = painterResource(Res.drawable.ic_profile_outlined)

                return remember {
                    TabOptions(
                        index = 0u,
                        title = title,
                        icon = icon,
                    )
                }
            }

        @Composable
        override fun Content() {
            HomeScreen()
        }
    }
}