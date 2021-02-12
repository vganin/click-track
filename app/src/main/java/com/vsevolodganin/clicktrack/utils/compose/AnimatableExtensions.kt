package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.foundation.animation.FlingConfig

suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    flingConfig: FlingConfig,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val decay = flingConfig.decayAnimation.generateDecayAnimationSpec<Float>()
    val targetValue = decay.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = flingConfig.adjustTarget(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget.target,
            animationSpec = adjustedTarget.animation,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = decay,
            block = block,
        )
    }
}
