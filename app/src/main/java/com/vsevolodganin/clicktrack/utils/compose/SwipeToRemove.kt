package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.gestures.draggable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun Modifier.swipeToRemove(
    constraints: Constraints,
    onDelete: () -> Unit,
): Modifier = composed {
    val width = constraints.maxWidth.toFloat()
    val draggable = remember { mutableStateOf(true) }
    val positionOffset = remember {
        Animatable(0f).apply {
            updateBounds(-width, 0f)
        }
    }
    val heightExpandRatio = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    this
        .draggable(
            enabled = draggable.value,
            orientation = Orientation.Horizontal,
            onDrag = { delta ->
                coroutineScope.launch {
                    positionOffset.snapTo((positionOffset.value + delta).coerceAtMost(0f))
                }
            },
            onDragStopped = { velocity ->
                coroutineScope.launch {
                    val config = FlingConfig(anchors = listOf(-width, 0f))
                    val endValue = positionOffset.fling(velocity, config).endState.value
                    if (endValue.absoluteValue > 0) {
                        draggable.value = false
                        heightExpandRatio.animateTo(0f, spring())
                        onDelete()
                    } else {
                        draggable.value = true
                    }
                }
            }
        )
        .offset(x = { positionOffset.value.roundToInt() })
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val height = (placeable.height * heightExpandRatio.value).roundToInt()
            layout(placeable.width, height) {
                placeable.place(0, 0)
            }
        }
}
