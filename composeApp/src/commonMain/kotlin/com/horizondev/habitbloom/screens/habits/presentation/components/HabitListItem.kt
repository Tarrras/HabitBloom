package com.horizondev.habitbloom.screens.habits.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo

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
            Box(
                modifier = Modifier.background(
                    shape = RoundedCornerShape(16.dp),
                    color = BloomTheme.colors.cardSecondary
                ).padding(all = 6.dp)
            ) {
                BloomNetworkImage(
                    iconUrl = habitInfo.iconUrl,
                    contentDescription = habitInfo.name,
                    shape = RectangleShape,
                    size = 24.dp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitInfo.name,
                    style = BloomTheme.typography.titleMedium,
                    color = BloomTheme.colors.textColor.primary
                )
                habitInfo.description.takeIf { it.isNotEmpty() }?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = BloomTheme.typography.bodyMedium,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
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