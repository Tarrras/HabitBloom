package com.horizondev.habitbloom.core.designComponents.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.no_results_found
import org.jetbrains.compose.resources.painterResource

@Composable
fun NoResultsPlaceholders(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.no_results_found),
            contentDescription = "no results found",
            modifier = Modifier.size(96.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(12.dp))
        title()
        Spacer(modifier = Modifier.height(16.dp))
    }
}