package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// FIXME(https://issuetracker.google.com/issues/180031493): `updateBounds` is not working properly now.
class AnimatableViewport(
    val bounds: Rect,
) {
    private val left = Animatable(bounds.left)
    private val top = Animatable(bounds.top)
    private val right = Animatable(bounds.right)
    private val bottom = Animatable(bounds.bottom)

    val value: Rect
        get() = Rect(left = left.value, top = top.value, right = right.value, bottom = bottom.value)

    suspend fun snapTo(
        newLeft: Float,
        newTop: Float,
        newRight: Float,
        newBottom: Float,
    ) {
        allowToDeform()
        left.snapTo(newLeft)
        top.snapTo(newTop)
        right.snapTo(newRight)
        bottom.snapTo(newBottom)
    }

    suspend fun translate(offset: Offset) {
        translate(offset.x, offset.y)
    }

    suspend fun translate(x: Float, y: Float) {
        forbidToDeform()
        arrayOf(left, right).forEach { it.snapTo(it.value + x) }
        arrayOf(top, bottom).forEach { it.snapTo(it.value + y) }
    }

    suspend fun animateDecay(
        initialVelocity: Offset,
        animationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
    ) = coroutineScope {
        forbidToDeform()
        launch { left.animateDecay(initialVelocity.x, animationSpec) }
        launch { top.animateDecay(initialVelocity.y, animationSpec) }
        launch { right.animateDecay(initialVelocity.x, animationSpec) }
        launch { bottom.animateDecay(initialVelocity.y, animationSpec) }
    }

    private fun forbidToDeform() {
        val snapshot = value
        left.updateBounds(lowerBound = bounds.left, upperBound = (bounds.right - snapshot.width).coerceAtLeast(bounds.left))
        top.updateBounds(lowerBound = bounds.top, upperBound = (bounds.bottom - snapshot.height).coerceAtLeast(bounds.top))
        right.updateBounds(lowerBound = (bounds.left + snapshot.width).coerceAtMost(bounds.right), upperBound = bounds.right)
        bottom.updateBounds(lowerBound = (bounds.top + snapshot.height).coerceAtMost(bounds.bottom), upperBound = bounds.bottom)
    }

    private fun allowToDeform() {
        val snapshot = value
        left.updateBounds(lowerBound = bounds.left, upperBound = snapshot.right)
        top.updateBounds(lowerBound = bounds.top, upperBound = snapshot.bottom)
        right.updateBounds(lowerBound = snapshot.left, upperBound = bounds.right)
        bottom.updateBounds(lowerBound = snapshot.top, upperBound = bounds.bottom)
    }
}
