package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun Modifier.swipeToRemove(
    constraints: Constraints,
    onDelete: () -> Unit,
): Modifier = composed {
    val width = constraints.maxWidth.toFloat()

    val draggable = remember { mutableStateOf(true) }

    val positionOffset = animatedFloat(0f).apply {
        setBounds(-width, 0f)
    }

    val heightExpandRatio = animatedFloat(initVal = 1f)

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
                        heightExpandRatio.animateTo(0f, spring()) { _, _ ->
                            onDelete()
                        }
                    } else {
                        draggable.value = true
                    }
                }
            }
        )
        .offset(x = { positionOffset.value })
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val height = (placeable.height * heightExpandRatio.value).roundToInt()
            layout(placeable.width, height) {
                placeable.place(0, 0)
            }
        }
}
