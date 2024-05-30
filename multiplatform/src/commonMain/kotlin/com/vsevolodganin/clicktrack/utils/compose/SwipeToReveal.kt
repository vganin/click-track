@file:Suppress("DEPRECATION") // TODO: Migrate to AnchoredDraggable

package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

enum class RevealValue {
    Hidden,
    Revealed,
}

@Composable
fun SwipeToReveal(
    state: SwipeableState<RevealValue>,
    revealOffset: Dp,
    modifier: Modifier = Modifier,
    revealed: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val revealOffsetPx = with(LocalDensity.current) { revealOffset.toPx() }
    val anchors = mapOf(
        0f to RevealValue.Hidden,
        -revealOffsetPx to RevealValue.Revealed,
    )

    Box(
        modifier = modifier.swipeable(
            state = state,
            anchors = anchors,
            orientation = Orientation.Horizontal,
        ),
    ) {
        revealed()
        Box(
            modifier = Modifier.offset { IntOffset(state.offset.value.roundToInt(), 0) },
            content = { content() },
        )
    }
}
