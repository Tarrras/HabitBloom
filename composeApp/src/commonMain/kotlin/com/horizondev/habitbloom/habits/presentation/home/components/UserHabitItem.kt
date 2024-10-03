package com.horizondev.habitbloom.habits.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.checkbox.BloomCheckBox
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_cup
import habitbloom.composeapp.generated.resources.streak_days
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource

@Composable
fun UserHabitItem(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitRecordFullInfo,
    onCompletionStatusChanged: (Long, Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    val isCompleted = habitInfo.isCompleted
    val daysStreak = habitInfo.daysStreak

    val titleColor by animateColorAsState(
        if (isCompleted) BloomTheme.colors.primary
        else BloomTheme.colors.textColor.primary
    )
    BloomCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BloomNetworkImage(
                    iconUrl = habitInfo.iconUrl,
                    size = 56.dp,
                    contentDescription = habitInfo.name,
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

            if (daysStreak != 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .background(
                            color = BloomTheme.colors.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pluralStringResource(
                            resource = Res.plurals.streak_days,
                            quantity = daysStreak,
                            daysStreak
                        ),
                        style = BloomTheme.typography.body.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = BloomTheme.colors.textColor.white
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Image(
                        painter = painterResource(Res.drawable.ic_cup),
                        contentDescription = "cup",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}