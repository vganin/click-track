package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable

@Composable
@NonRestartableComposable
expect fun KeepScreenOn()
