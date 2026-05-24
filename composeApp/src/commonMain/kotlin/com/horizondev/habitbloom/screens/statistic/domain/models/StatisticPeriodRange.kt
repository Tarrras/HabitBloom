package com.horizondev.habitbloom.screens.statistic.domain.models

import kotlinx.datetime.LocalDate

data class StatisticPeriodRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)
