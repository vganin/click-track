package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun PaddingValues.withFabPadding(): PaddingValues {
    return copy(bottom = calculateBottomPadding() + DEFAULT_FAB_PADDING)
}

private val DEFAULT_FAB_PADDING = 80.dp
