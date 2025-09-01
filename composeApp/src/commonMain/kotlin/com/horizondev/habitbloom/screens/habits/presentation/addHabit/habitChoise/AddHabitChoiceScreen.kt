package com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.inputText.BloomSearchTextField
import com.horizondev.habitbloom.core.designComponents.list.NoResultsPlaceholders
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.presentation.components.HabitListItem
import com.horizondev.habitbloom.utils.parseHexColor
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_your_own_personal_habit_to_start_tracking
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.category_empty_description
import habitbloom.composeapp.generated.resources.category_empty_title
import habitbloom.composeapp.generated.resources.choose_habit_to_acquire
import habitbloom.composeapp.generated.resources.choose_ready_or_create_own
import habitbloom.composeapp.generated.resources.clear_search
import habitbloom.composeapp.generated.resources.create_personal_habit
import habitbloom.composeapp.generated.resources.create_personal_habit_subtitle
import habitbloom.composeapp.generated.resources.delete
import habitbloom.composeapp.generated.resources.delete_custom_habit_description
import habitbloom.composeapp.generated.resources.delete_custom_habit_question
import habitbloom.composeapp.generated.resources.found_count
import habitbloom.composeapp.generated.resources.no_habits_found
import habitbloom.composeapp.generated.resources.no_results_for_query
import habitbloom.composeapp.generated.resources.nothing_found
import habitbloom.composeapp.generated.resources.or_choose_ready_count
import habitbloom.composeapp.generated.resources.search_habit
import habitbloom.composeapp.generated.resources.tip
import habitbloom.composeapp.generated.resources.tip_suggestion_generic
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


/**
 * The first screen in the Add Habit flow where users select a habit.
 */
@Composable
fun AddHabitChoiceScreen(
    showSnackbar: (BloomSnackbarVisuals) -> Unit,
    onHabitSelected: (HabitInfo) -> Unit,
    onCreateCustomHabit: () -> Unit,
    onBack: () -> Unit,
) {
    // Create ViewModel using Koin
    val viewModel = koinViewModel<AddHabitChoiceViewModel>()

    // Collect state and setup UI
    val uiState by viewModel.state.collectAsState()

    // Handle UI intents (navigation and messages)
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                is AddHabitChoiceUiIntent.ShowSnackbar -> {
                    showSnackbar(uiIntent.visuals)
                }

                is AddHabitChoiceUiIntent.NavigateNext -> {
                    onHabitSelected(uiIntent.info)
                }

                is AddHabitChoiceUiIntent.NavigateToCreateCustomHabit -> {
                    onCreateCustomHabit()
                }

                AddHabitChoiceUiIntent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    val currentState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(currentState) {
        if (currentState == Lifecycle.State.RESUMED) {
            viewModel.handleUiEvent(AddHabitChoiceUiEvent.RefreshPage)
        }
    }

    AddHabitChoiceScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
        currentCategory = uiState.currentCategory,
        onCreateCustomHabit = onCreateCustomHabit
    )
}

@Composable
fun AddHabitChoiceScreenContent(
    uiState: AddHabitChoiceUiState,
    handleUiEvent: (AddHabitChoiceUiEvent) -> Unit,
    currentCategory: HabitCategoryData?,
    onCreateCustomHabit: () -> Unit,
) {
    // UI Content
    Box {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.choose_habit_to_acquire),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.choose_ready_or_create_own),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
            )

            Spacer(modifier = Modifier.height(24.dp))
            if (uiState.isLoading.not()) {
                BloomSearchTextField(
                    value = uiState.searchInput,
                    onValueChange = {
                        handleUiEvent(AddHabitChoiceUiEvent.UpdateSearchInput(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholderText = stringResource(Res.string.search_habit)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Category-specific empty placeholder when no habits and search is empty
                val categoryDraft = currentCategory
                if (categoryDraft != null && uiState.habits.isEmpty() && uiState.searchInput.isBlank()) {
                    CategoryNoHabitsPlaceholder(
                        category = categoryDraft,
                        onCreateCustomHabit = onCreateCustomHabit
                    )
                } else {
                    // Create personal habit CTA card (dashed)
                    CreatePersonalHabitCard(
                        onClick = { handleUiEvent(AddHabitChoiceUiEvent.CreateCustomHabit) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val headerText = if (uiState.searchInput.isBlank())
                        stringResource(Res.string.or_choose_ready_count, uiState.habits.size)
                    else stringResource(Res.string.found_count, uiState.habits.size)

                    SectionHeader(text = headerText)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (uiState.habits.isNotEmpty()) {
                        HabitsList(
                            habits = uiState.habits,
                            onHabitClicked = {
                                handleUiEvent(AddHabitChoiceUiEvent.SelectHabit(it))
                            },
                            onHabitDelete = {
                                handleUiEvent(AddHabitChoiceUiEvent.DeleteHabit(it))
                            }
                        )
                    } else {
                        if (uiState.searchInput.isBlank()) {
                            NoResultsPlaceholders(
                                modifier = Modifier.fillMaxWidth(),
                                title = stringResource(Res.string.no_habits_found),
                                description = stringResource(Res.string.add_your_own_personal_habit_to_start_tracking),
                                buttonText = stringResource(Res.string.create_personal_habit),
                                onButtonClick = {
                                    handleUiEvent(AddHabitChoiceUiEvent.CreateCustomHabit)
                                }
                            )
                        } else {
                            HabitSearchNoResultsPlaceholder(
                                query = uiState.searchInput,
                                onClearSearch = {
                                    handleUiEvent(AddHabitChoiceUiEvent.UpdateSearchInput(""))
                                }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.isLoading) {
            BloomLoadingAnimation(
                modifier = Modifier.align(Alignment.Center).size(150.dp),
            )
        }

        // Delete confirmation dialog
        DeleteCustomHabitDialog(
            showDialog = uiState.showDeleteDialog,
            habitName = uiState.habitToDelete?.name ?: "",
            onConfirm = { handleUiEvent(AddHabitChoiceUiEvent.ConfirmDeleteHabit) },
            onDismiss = { handleUiEvent(AddHabitChoiceUiEvent.CancelDeleteHabit) }
        )
    }
}

@Composable
private fun CreatePersonalHabitCard(
    borderColor: Color = BloomTheme.colors.border,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(BloomTheme.colors.glassBackground)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                )
                drawRoundRect(
                    color = borderColor,
                    size = this.size,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                    style = stroke
                )
            }
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BloomTheme.colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add",
                    tint = BloomTheme.colors.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.create_personal_habit),
                    style = BloomTheme.typography.titleMedium,
                    color = BloomTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.create_personal_habit_subtitle),
                    style = BloomTheme.typography.bodyMedium,
                    color = BloomTheme.colors.textColor.secondary
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = BloomTheme.typography.body.copy(fontWeight = FontWeight.Medium),
            color = BloomTheme.colors.textColor.secondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HabitSearchNoResultsPlaceholder(
    query: String,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(BloomTheme.colors.glassBackgroundStrong),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = BloomTheme.colors.mutedForeground,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.nothing_found),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.no_results_for_query, query),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        BloomPrimaryOutlinedButton(
            text = stringResource(Res.string.clear_search),
            onClick = onClearSearch
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CategoryNoHabitsPlaceholder(
    category: HabitCategoryData,
    onCreateCustomHabit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Icon tile with subtle gradient background using category colors
        val start = category.backgroundColorHexFirst.parseHexColor().copy(alpha = 0.25f)
        val end = category.backgroundColorHexSecond.parseHexColor().copy(alpha = 0.25f)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Brush.linearGradient(listOf(start, end))),
            contentAlignment = Alignment.Center
        ) {
            // Use network image for the category icon
            BloomNetworkImage(
                size = 32.dp,
                iconUrl = category.iconUrl,
                contentDescription = category.title,
                shape = RectangleShape
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.category_empty_title),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.category_empty_description, category.title),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))
        BloomPrimaryFilledButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.create_personal_habit),
            onClick = onCreateCustomHabit
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Tip card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BloomTheme.colors.glassBackground)
                .border(
                    width = 1.dp,
                    color = BloomTheme.colors.glassBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BloomTheme.colors.warning.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💡")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.tip),
                        style = BloomTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = BloomTheme.colors.accentForeground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.tip_suggestion_generic),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }
        }
    }
}

/**
 * Displays the list of habits.
 */
@Composable
private fun HabitsList(
    habits: List<HabitInfo>,
    onHabitClicked: (HabitInfo) -> Unit,
    onHabitDelete: (HabitInfo) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        habits(
            habits = habits,
            onHabitClicked = onHabitClicked,
            onHabitDelete = onHabitDelete
        )
    }

    // Removed the floating "create personal habit" button that appears when scrolling
}

@Composable
private fun DeleteCustomHabitDialog(
    showDialog: Boolean,
    habitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        BloomAlertDialog(
            isShown = true,
            onDismiss = onDismiss
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.delete_custom_habit_question),
                    color = BloomTheme.colors.textColor.primary,
                    style = BloomTheme.typography.heading,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(Res.string.delete_custom_habit_description),
                    color = BloomTheme.colors.textColor.primary,
                    style = BloomTheme.typography.body,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
                BloomPrimaryFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.delete),
                    onClick = onConfirm,
                )
                Spacer(modifier = Modifier.height(12.dp))
                BloomPrimaryOutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.cancel),
                    onClick = onDismiss,
                )
            }
        }
    }
}

private fun LazyListScope.habits(
    habits: List<HabitInfo>,
    onHabitClicked: (HabitInfo) -> Unit,
    onHabitDelete: (HabitInfo) -> Unit
) {
    items(habits, key = { it.id }) {
        HabitListItem(
            modifier = Modifier.fillMaxWidth(),
            habitInfo = it,
            onClick = {
                onHabitClicked(it)
            },
            onDelete = if (it.isCustomHabit) {
                { onHabitDelete(it) }
            } else null
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}