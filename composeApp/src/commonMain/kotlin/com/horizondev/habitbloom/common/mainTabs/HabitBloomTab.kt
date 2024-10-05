package com.horizondev.habitbloom.common.mainTabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.horizondev.habitbloom.calendar.CalendarScreen
import com.horizondev.habitbloom.calendar.CalendarScreenModel
import com.horizondev.habitbloom.habits.presentation.home.HomeScreen
import com.horizondev.habitbloom.habits.presentation.home.HomeScreenModel
import com.horizondev.habitbloom.profile.presentation.ProfileScreen
import com.horizondev.habitbloom.profile.presentation.ProfileScreenModel
import com.horizondev.habitbloom.statistic.StatisticScreen
import com.horizondev.habitbloom.statistic.StatisticScreenModel
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_calendar_outlined
import habitbloom.composeapp.generated.resources.ic_chart_proportion_outlined
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
            val screenModel = getScreenModel<HomeScreenModel>()
            HomeScreen(screeModel = screenModel)
        }
    }

    internal data object StatisticTab : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = toString()
                val icon = painterResource(Res.drawable.ic_chart_proportion_outlined)

                return remember {
                    TabOptions(
                        index = 1u,
                        title = title,
                        icon = icon,
                    )
                }
            }

        @Composable
        override fun Content() {
            val screenModel = getScreenModel<StatisticScreenModel>()
            StatisticScreen(screeModel = screenModel)
        }
    }

    internal data object CalendarTab : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = toString()
                val icon = painterResource(Res.drawable.ic_calendar_outlined)

                return remember {
                    TabOptions(
                        index = 2u,
                        title = title,
                        icon = icon,
                    )
                }
            }

        @Composable
        override fun Content() {
            val screenModel = getScreenModel<CalendarScreenModel>()
            CalendarScreen(screeModel = screenModel)
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
                        index = 3u,
                        title = title,
                        icon = icon,
                    )
                }
            }

        @Composable
        override fun Content() {
            val screenModel = getScreenModel<ProfileScreenModel>()
            ProfileScreen(screeModel = screenModel)
        }
    }

}