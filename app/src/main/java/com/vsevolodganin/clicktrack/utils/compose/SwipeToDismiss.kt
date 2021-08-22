package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.utils.compose.BackgroundState.DISMISSED
import com.vsevolodganin.clicktrack.utils.compose.BackgroundState.INITIAL
import com.vsevolodganin.clicktrack.utils.compose.BackgroundState.STARTED

@Composable
fun SwipeToDismiss(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundPaddingValues: PaddingValues = PaddingValues(),
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(
        animationSpec = tween(
            durationMillis = 500,
        )
    ) + slideOutVertically(
        targetOffsetY = { -it / 10 }
    ),
    content: @Composable () -> Unit,
) {
    // FIXME: Using `remember` instead of `rememberDismissState` because of some bug with remembering dismissed state by new items
    val dismissState = remember { DismissState(Default) { true } }
    val visibleState = remember { MutableTransitionState(true) }

    visibleState.targetState = !dismissState.isDismissed(DismissDirection.EndToStart)

    DisposableEffect(visibleState.currentState) {
        if (!visibleState.currentState) {
            onDismiss()
        }
        onDispose {}
    }

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            background = { Background(dismissState, backgroundPaddingValues) },
            dismissContent = { content() }
        )
    }
}

private enum class BackgroundState {
    INITIAL, STARTED, DISMISSED
}

@Composable
private fun Background(state: DismissState, paddingValues: PaddingValues) {
    val density = LocalDensity.current
    val rippleTheme = LocalRippleTheme.current
    val rippleColor = rippleTheme.defaultColor()
    val rippleAlpha = rippleTheme.rippleAlpha().pressedAlpha
    val rippleMaxRadius = with(density) { 75.dp.toPx() }
    val dismissingColor = colorResource(R.color.swipe_dismiss_background)
    val startedThreshold = with(density) { (-64).dp.toPx() }
    val internalState by derivedStateOf {
        when (state.currentValue) {
            Default -> when {
                state.offset.value > startedThreshold -> INITIAL
                else -> STARTED
            }
            else -> DISMISSED
        }
    }

    val color by animateColorAsState(
        when (internalState) {
            INITIAL -> Color.Transparent
            STARTED -> dismissingColor
            DISMISSED -> dismissingColor
        }
    )
    val scale by animateFloatAsState(
        when (internalState) {
            INITIAL -> 0.75f
            STARTED -> 1f
            DISMISSED -> 1f
        }
    )
    val rippleRadius by animateFloatAsState(
        targetValue = when (internalState) {
            INITIAL -> 0f
            STARTED -> rippleMaxRadius
            DISMISSED -> 0f
        },
        animationSpec = when (internalState) {
            INITIAL -> tween(
                durationMillis = 100,
                easing = LinearEasing,
            )
            STARTED -> tween(
                durationMillis = 300,
                easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f),
            )
            DISMISSED -> tween(
                durationMillis = 50,
                easing = LinearEasing,
            )
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color)
            .clipToBounds()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier
                .scale(scale)
                .drawWithContent {
                    drawContent()
                    drawCircle(
                        color = rippleColor.copy(alpha = rippleAlpha),
                        radius = rippleRadius
                    )
                }
        )
    }
}
