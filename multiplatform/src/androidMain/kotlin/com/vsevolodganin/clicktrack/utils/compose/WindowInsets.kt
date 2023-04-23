package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual val WindowInsets.Companion.navigationBars: WindowInsets
    @Composable get() = navigationBars

actual fun Modifier.navigationBarsPadding(): Modifier = navigationBarsPadding()
