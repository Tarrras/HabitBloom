package com.horizondev.habitbloom.core.designComponents.switcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun <T> BloomTabSwitcher(
    modifier: Modifier = Modifier,
    selectedItem: T,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    title: @Composable (T) -> String
) {
    val selectedTabIndex = items.indexOf(selectedItem)
    TabRow(
        modifier = modifier.clip(
            shape = RoundedCornerShape(12.dp)
        ),
        divider = {},
        selectedTabIndex = selectedTabIndex,
        indicator = @Composable { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .fillMaxHeight()
                        .padding(all = 4.dp)
                        .background(
                            color = BloomTheme.colors.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        },
        containerColor = BloomTheme.colors.primary.copy(alpha = 0.2f)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            Text(
                text = title(item),
                style = BloomTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                color = when (isSelected) {
                    true -> BloomTheme.colors.textColor.primary
                    false -> BloomTheme.colors.textColor.secondary
                },
                modifier = Modifier.zIndex(2f).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onItemSelected(item)
                }.padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

