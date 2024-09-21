package com.horizondev.habitbloom.core.designComponents.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import kotlinx.coroutines.delay

private val circeSize = 32.dp

@Composable
fun BloomStepper(
    modifier: Modifier = Modifier,
    items: List<String>,
    currentItemIndex: Int,
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(currentItemIndex) {
        delay(300)
        //considering horizontal lines as row items
        scrollState.animateScrollToItem(currentItemIndex * 2)
    }

    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPaddingValues,
        state = scrollState
    ) {
        itemsIndexed(items, key = { _, item -> item }) { index, item ->
            val status = when {
                index == currentItemIndex -> StepperItemStatus.Selected
                index > currentItemIndex -> StepperItemStatus.Default
                else -> StepperItemStatus.Completed
            }

            BloomStepperStep(
                title = item, index = index + 1, status = status
            )
            if (index != items.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
                StepperHorizontalDivider(
                    isFilled = status == StepperItemStatus.Completed
                )
            }
        }
    }
}

@Composable
private fun BloomStepperStep(
    modifier: Modifier = Modifier,
    title: String,
    index: Int,
    status: StepperItemStatus
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (status) {
            StepperItemStatus.Completed -> {
                StepperCompletedPageBox()
            }

            else -> StepperPageNumberBox(index = index, status = status)
        }
        StepperPageTitle(status = status, title = title)
    }
}

@Composable
private fun StepperPageNumberBox(
    modifier: Modifier = Modifier,
    status: StepperItemStatus,
    index: Int
) {
    val color = when (status) {
        StepperItemStatus.Selected -> BloomTheme.colors.textColor.primary
        else -> BloomTheme.colors.textColor.secondary
    }
    Box(
        modifier = modifier
            .size(circeSize)
            .border(
                width = 1.dp,
                color = color,
                shape = CircleShape
            )
    ) {
        Text(
            style = BloomTheme.typography.small.copy(
                fontWeight = when (status) {
                    StepperItemStatus.Selected -> FontWeight.SemiBold
                    else -> FontWeight.Normal
                }
            ),
            color = color,
            modifier = Modifier.align(Alignment.Center),
            text = index.toString()
        )
    }
}

@Composable
private fun StepperCompletedPageBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(circeSize)
            .background(color = BloomTheme.colors.primary, shape = CircleShape)
    ) {
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(12.dp),
            tint = BloomTheme.colors.textColor.white
        )
    }
}

@Composable
private fun StepperPageTitle(
    modifier: Modifier = Modifier,
    status: StepperItemStatus,
    title: String
) {
    val color = when (status) {
        StepperItemStatus.Default -> BloomTheme.colors.textColor.secondary
        else -> BloomTheme.colors.textColor.primary
    }
    Text(
        style = BloomTheme.typography.small.copy(
            fontWeight = when (status) {
                StepperItemStatus.Selected -> FontWeight.SemiBold
                else -> FontWeight.Normal
            }
        ),
        color = color,
        text = title,
        modifier = modifier
    )
}

@Composable
private fun StepperHorizontalDivider(
    modifier: Modifier = Modifier,
    isFilled: Boolean = false
) {
    HorizontalDivider(
        modifier = modifier.width(40.dp),
        thickness = 2.dp,
        color = when (isFilled) {
            true -> BloomTheme.colors.primary
            false -> BloomTheme.colors.disabled
        }
    )
}

enum class StepperItemStatus {
    Default,
    Selected,
    Completed
}