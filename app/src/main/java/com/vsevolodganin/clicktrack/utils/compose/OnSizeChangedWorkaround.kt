package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntSize

// FIXME(https://issuetracker.google.com/issues/177562900): Replace wit regular `onSizeChanged`
inline fun Modifier.onSizeChangedPaddingIncluded(
    crossinline onSizeChanged: (IntSize) -> Unit,
) = composed {
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        onSizeChanged(IntSize(placeable.width, placeable.height))
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}
