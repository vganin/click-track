package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import kotlin.math.atan2

class RadialDragObserver(
    private val center: Offset,
    private val onRadialDrag: (Float) -> Unit
) : DragObserver {

    private var downPosition: Offset? = null

    override fun onStart(downPosition: Offset) {
        this.downPosition = downPosition
    }

    override fun onDrag(dragDistance: Offset): Offset {
        val downPosition = downPosition ?: return Offset.Zero
        val draggedToPosition = downPosition + dragDistance

        val angleDiff = angleBetween(downPosition - center, draggedToPosition - center)
        onRadialDrag.invoke(angleDiff)

        this.downPosition = draggedToPosition

        return dragDistance
    }

    private fun angleBetween(from: Offset, to: Offset): Float {
        val dot = from.x * to.x + from.y * to.y
        val det = from.x * to.y - from.y * to.x
        return atan2(det, dot).toDegrees()
    }
}
