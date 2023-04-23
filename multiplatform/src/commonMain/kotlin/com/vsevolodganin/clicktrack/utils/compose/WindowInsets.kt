package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Modifier

expect val WindowInsets.Companion.navigationBars: WindowInsets
expect val WindowInsets.Companion.statusBars: WindowInsets

expect fun Modifier.navigationBarsPadding(): Modifier
