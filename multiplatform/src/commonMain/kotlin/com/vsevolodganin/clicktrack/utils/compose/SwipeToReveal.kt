package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

enum class RevealValue {
    Hidden,
    Revealed,
}

class RevealState internal constructor(
    internal val internalState: AnchoredDraggableState<RevealValue>,
) {
    val offset: Float get() = internalState.offset
    val targetValue: RevealValue get() = internalState.targetValue
}

@Composable
fun SwipeToReveal(
    state: RevealState,
    revealOffset: Dp,
    modifier: Modifier = Modifier,
    revealed: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val revealOffsetPx = with(LocalDensity.current) { revealOffset.toPx() }

    LaunchedEffect(revealOffsetPx) {
        state.internalState.updateAnchors(
            DraggableAnchors {
                RevealValue.Hidden at 0f
                RevealValue.Revealed at -revealOffsetPx
            },
        )
    }

    Box(
        modifier = modifier
            .anchoredDraggable(
                state = state.internalState,
                orientation = Orientation.Horizontal,
            ),
    ) {
        revealed()
        Box(
            modifier = Modifier.offset { IntOffset(state.offset.roundToInt(), 0) },
            content = { content() },
        )
    }
}

@Composable
fun rememberRevealState(initialValue: RevealValue): RevealState {
    val draggableState = remember(initialValue) {
        AnchoredDraggableState(initialValue, DraggableAnchors { RevealValue.Hidden at 0f })
    }
    return remember(draggableState) { RevealState(draggableState) }
}
