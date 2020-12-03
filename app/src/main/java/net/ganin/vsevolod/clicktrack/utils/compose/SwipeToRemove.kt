package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.animation.animatedFloat
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.unit.Constraints
import kotlin.math.absoluteValue

fun Modifier.swipeToRemove(
    constraints: Constraints,
    onDelete: () -> Unit
): Modifier = composed {
    val width = constraints.maxWidth.toFloat()

    val draggable = remember { mutableStateOf(true) }

    val positionOffset = animatedFloat(0f).apply {
        setBounds(-width, 0f)
    }

    this
        .draggable(
            enabled = draggable.value,
            orientation = Orientation.Horizontal,
            onDrag = { delta ->
                positionOffset.snapTo((positionOffset.value + delta).coerceAtMost(0f))
            },
            onDragStopped = { velocity ->
                val config = FlingConfig(anchors = listOf(-width, 0f))
                positionOffset.fling(velocity, config) { _, endValue, _ ->
                    if (endValue.absoluteValue > 0) {
                        draggable.value = false
                        onDelete()
                    } else {
                        draggable.value = true
                    }
                }
            }
        )
        .offset(x = { positionOffset.value })
}
