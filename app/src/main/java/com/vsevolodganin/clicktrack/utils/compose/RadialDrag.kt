package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import kotlin.math.atan2

suspend fun PointerInputScope.detectRadialDragGesture(
    center: Offset,
    onRadialDrag: (Float) -> Unit,
) {
    detectDragGestures(
        onDrag = { change, _ ->
            change.consumeAllChanges()
            val angleDiff = angleBetween(change.previousPosition - center, change.position - center)
            onRadialDrag.invoke(angleDiff)
        },
    )
}

private fun angleBetween(from: Offset, to: Offset): Float {
    val dot = from.x * to.x + from.y * to.y
    val det = from.x * to.y - from.y * to.x
    return atan2(det, dot).toDegrees()
}
