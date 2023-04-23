package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Modifier

actual val WindowInsets.Companion.navigationBars: WindowInsets get() = WindowInsets(0)

actual fun Modifier.navigationBarsPadding(): Modifier = this
