package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

enum class RevealValue {
    Hidden,
    Revealed,
}

class RevealState(initial: RevealValue) {
    var targetValue by mutableStateOf(initial)
    var offsetPx by mutableStateOf(0f)
}

@Composable
fun rememberRevealState(initial: RevealValue = RevealValue.Hidden): RevealState = remember { RevealState(initial) }

@Composable
fun SwipeToReveal(
    state: RevealState,
    revealOffset: Dp,
    modifier: Modifier = Modifier,
    revealed: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val revealOffsetPx = with(LocalDensity.current) { revealOffset.toPx() }

    Box(
        modifier = modifier.pointerInput(revealOffsetPx) {
            detectDragGestures(
                onDragEnd = {
                    // Snap based on which side the offset is closer to
                    state.targetValue = if (state.offsetPx <= -revealOffsetPx / 2f) RevealValue.Revealed else RevealValue.Hidden
                    state.offsetPx = if (state.targetValue == RevealValue.Revealed) -revealOffsetPx else 0f
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val newOffset = (state.offsetPx + dragAmount.x)
                    state.offsetPx = newOffset.coerceIn(-revealOffsetPx, 0f)
                },
            )
        },
    ) {
        revealed()
        Box(
            modifier = Modifier.offset { IntOffset(state.offsetPx.roundToInt(), 0) },
            content = { content() },
        )
    }
}
