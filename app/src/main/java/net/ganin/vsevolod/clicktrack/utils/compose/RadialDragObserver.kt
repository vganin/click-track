package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver

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

        val angleDiff = (downPosition - center).vectorAngle - (draggedToPosition - center).vectorAngle
        onRadialDrag.invoke(angleDiff)

        return dragDistance
    }
}
