package com.vsevolodganin.clicktrack.ui.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object CommonCardElevation {
    val Normal = 1.dp
    val Pressed = 4.dp
}

@Composable
fun commonCardElevation(isDragging: Boolean): Dp {
    return with(CommonCardElevation) {
        animateDpAsState(if (isDragging) Pressed else Normal)
    }.value
}
