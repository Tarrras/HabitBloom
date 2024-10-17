package com.horizondev.habitbloom.core.designComponents.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class BloomSnackbarVisuals(
    override val message: String,
    override val actionLabel: String?,
    override val withDismissAction: Boolean,
    override val duration: SnackbarDuration
) : SnackbarVisuals {

    constructor(
        message: String,
        state: BloomSnackbarState,
        withDismissAction: Boolean,
        duration: SnackbarDuration
    ) : this(
        message = message,
        actionLabel = state.toString(),
        withDismissAction = withDismissAction,
        duration = duration
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BloomSnackbarVisuals

        if (message != other.message) return false
        if (actionLabel != other.actionLabel) return false
        if (withDismissAction != other.withDismissAction) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + actionLabel.hashCode()
        result = 31 * result + withDismissAction.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}