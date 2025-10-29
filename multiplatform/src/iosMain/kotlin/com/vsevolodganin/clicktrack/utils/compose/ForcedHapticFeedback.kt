package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.Composable

@Composable
actual fun ForcedHapticFeedback(content: @Composable () -> Unit) = content()
