package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SimpleSpacer(width: Dp = Dp.Unspecified, height: Dp = Dp.Unspecified) {
    Spacer(
        modifier = Modifier
            .width(width)
            .height(height),
    )
}
