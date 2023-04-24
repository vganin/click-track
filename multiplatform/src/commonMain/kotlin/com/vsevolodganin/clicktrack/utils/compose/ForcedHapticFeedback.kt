package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.Composable

// FIXME(https://issuetracker.google.com/issues/171394805)
@Composable
expect fun ForcedHapticFeedback(content: @Composable () -> Unit)
