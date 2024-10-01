package com.horizondev.habitbloom.habits.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.checkbox.BloomCheckBox
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun UserHabitItem(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitRecordFullInfo,
    onCompletionStatusChanged: (Long, Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    val isCompleted = habitInfo.isCompleted
    val titleColor by animateColorAsState(
        if (isCompleted) BloomTheme.colors.primary
        else BloomTheme.colors.textColor.primary
    )
    BloomCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            KamelImage(
                resource = asyncPainterResource(data = habitInfo.iconUrl),
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentDescription = habitInfo.name,
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitInfo.name,
                    style = BloomTheme.typography.heading,
                    color = titleColor,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = habitInfo.description,
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )
            }

            BloomCheckBox(
                checked = habitInfo.isCompleted, onCheckedChange = { isCompleted ->
                    onCompletionStatusChanged(habitInfo.id, isCompleted)
                }
            )
        }
    }
}