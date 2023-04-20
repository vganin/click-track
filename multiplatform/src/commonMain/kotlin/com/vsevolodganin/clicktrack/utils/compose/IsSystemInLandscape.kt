package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
@ReadOnlyComposable
expect fun isSystemInLandscape(): Boolean
