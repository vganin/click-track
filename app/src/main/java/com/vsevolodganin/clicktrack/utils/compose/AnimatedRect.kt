package com.vsevolodganin.clicktrack.utils.compose

import android.annotation.SuppressLint
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.ExponentialDecay
import androidx.compose.animation.core.FloatDecayAnimationSpec
import androidx.compose.animation.core.OnAnimationEnd
import androidx.compose.animation.core.fling
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

class AnimatedRect(
    private val left: AnimatedFloat,
    private val top: AnimatedFloat,
    private val right: AnimatedFloat,
    private val bottom: AnimatedFloat,
    val bounds: Rect,
) {
    val value: Rect
        get() {
            return Rect(left.value, top.value, right.value, bottom.value)
        }

    fun snapTo(
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

    fun translate(offset: Offset) {
        translate(offset.x, offset.y)
    }

    fun translate(x: Float, y: Float) {
        forbidToDeform()
        arrayOf(left, right).forEach { it.snapTo(it.value + x) }
        arrayOf(top, bottom).forEach { it.snapTo(it.value + y) }
    }

    fun fling(
        startingVelocity: Offset,
        decay: FloatDecayAnimationSpec = ExponentialDecay(),
        onEnd: OnAnimationEnd? = null,
    ) {
        forbidToDeform()
        left.fling(startingVelocity.x, decay, onEnd)
        top.fling(startingVelocity.y, decay)
        right.fling(startingVelocity.x, decay)
        bottom.fling(startingVelocity.y, decay)
    }

    private fun forbidToDeform() {
        val snapshot = value
        left.setBounds(min = bounds.left, max = bounds.right - snapshot.width)
        top.setBounds(min = bounds.top, max = bounds.bottom - snapshot.height)
        right.setBounds(min = bounds.left + snapshot.width, max = bounds.right)
        bottom.setBounds(min = bounds.top + snapshot.height, max = bounds.bottom)
    }

    private fun allowToDeform() {
        val snapshot = value
        left.setBounds(min = bounds.left, max = snapshot.right)
        top.setBounds(min = bounds.top, max = snapshot.bottom)
        right.setBounds(min = snapshot.left, max = bounds.right)
        bottom.setBounds(min = snapshot.top, max = bounds.bottom)
    }
}

@SuppressLint("ComposableNaming") // This imitates constructor so shouldn't start from upper case
@Composable
fun AnimatedRect(bounds: Rect) = AnimatedRect(
    left = animatedFloat(bounds.left),
    top = animatedFloat(bounds.top),
    right = animatedFloat(bounds.right),
    bottom = animatedFloat(bounds.bottom),
    bounds = bounds
)
