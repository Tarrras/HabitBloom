package com.horizondev.habitbloom.habits.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.HabitInfo

@Composable
fun HabitListItem(
    modifier: Modifier = Modifier,
    habitInfo: HabitInfo,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    BloomCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BloomNetworkImage(
                iconUrl = habitInfo.iconUrl,
                contentDescription = habitInfo.name
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitInfo.name,
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = habitInfo.description,
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )
            }

            if (habitInfo.isCustomHabit && onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete custom habit",
                        tint = BloomTheme.colors.error
                    )
                }
            }
        }
    }
}