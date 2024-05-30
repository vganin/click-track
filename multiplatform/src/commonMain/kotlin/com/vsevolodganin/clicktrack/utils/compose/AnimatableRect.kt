package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AnimatableRect(val bounds: Rect) {
    private val _left = Animatable(bounds.left)
    private val _top = Animatable(bounds.top)
    private val _right = Animatable(bounds.right)
    private val _bottom = Animatable(bounds.bottom)

    val left: Float
        get() = _left.value
    val top: Float
        get() = _top.value
    val right: Float
        get() = _right.value
    val bottom: Float
        get() = _bottom.value
    val width: Float
        get() = right - left
    val height: Float
        get() = bottom - top
    val value: Rect
        get() = Rect(left = left, top = top, right = right, bottom = bottom)

    suspend fun animateTo(
        newLeft: Float = left,
        newTop: Float = top,
        newRight: Float = right,
        newBottom: Float = bottom,
    ) = coroutineScope {
        allowToDeform()
        launch { _left.animateTo(newLeft) }
        launch { _top.animateTo(newTop) }
        launch { _right.animateTo(newRight) }
        launch { _bottom.animateTo(newBottom) }
    }

    suspend fun snapTo(
        newLeft: Float = left,
        newTop: Float = top,
        newRight: Float = right,
        newBottom: Float = bottom,
    ) = coroutineScope {
        allowToDeform()
        launch { _left.snapTo(newLeft) }
        launch { _top.snapTo(newTop) }
        launch { _right.snapTo(newRight) }
        launch { _bottom.snapTo(newBottom) }
    }

    suspend fun animateDecay(
        initialVelocity: Offset,
        animationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
        maintainWidth: Float = width,
        maintainHeight: Float = height,
    ) = coroutineScope {
        forbidToDeform(maintainWidth, maintainHeight)
        launch { _left.animateDecay(initialVelocity.x, animationSpec) }
        launch { _top.animateDecay(initialVelocity.y, animationSpec) }
        launch { _right.animateDecay(initialVelocity.x, animationSpec) }
        launch { _bottom.animateDecay(initialVelocity.y, animationSpec) }
    }

    private fun forbidToDeform(
        width: Float,
        height: Float,
    ) {
        _left.updateBounds(lowerBound = bounds.left, upperBound = (bounds.right - width).coerceAtLeast(bounds.left))
        _top.updateBounds(lowerBound = bounds.top, upperBound = (bounds.bottom - height).coerceAtLeast(bounds.top))
        _right.updateBounds(lowerBound = (bounds.left + width).coerceAtMost(bounds.right), upperBound = bounds.right)
        _bottom.updateBounds(lowerBound = (bounds.top + height).coerceAtMost(bounds.bottom), upperBound = bounds.bottom)
    }

    private fun allowToDeform() {
        _left.updateBounds(lowerBound = bounds.left, upperBound = bounds.right)
        _top.updateBounds(lowerBound = bounds.top, upperBound = bounds.bottom)
        _right.updateBounds(lowerBound = bounds.left, upperBound = bounds.right)
        _bottom.updateBounds(lowerBound = bounds.top, upperBound = bounds.bottom)
    }
}
