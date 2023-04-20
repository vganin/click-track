package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@ReadOnlyComposable
@Composable
actual fun isSystemInLandscape(): Boolean {
    // FIXME: Support landscape
    return false
}
