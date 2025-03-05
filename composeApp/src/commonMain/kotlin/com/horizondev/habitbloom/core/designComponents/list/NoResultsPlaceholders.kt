package com.horizondev.habitbloom.core.designComponents.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.no_results_found
import org.jetbrains.compose.resources.painterResource

@Composable
fun NoResultsPlaceholders(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
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
        Text(
            text = title,
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        BloomPrimaryFilledButton(
            text = buttonText,
            onClick = onButtonClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

// Keep the old version for backward compatibility
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