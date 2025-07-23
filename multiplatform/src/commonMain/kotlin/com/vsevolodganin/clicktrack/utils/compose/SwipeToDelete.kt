@file:Suppress("DEPRECATION") // TODO: Migrate to AnchoredDraggable

package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.generated.resources.MR
import dev.icerock.moko.resources.compose.colorResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDelete(onDeleted: () -> Unit, modifier: Modifier = Modifier, contentPadding: Dp = 0.dp, content: @Composable () -> Unit) {
    val revealState = rememberSwipeableState(RevealValue.Hidden)
    val visibleState = remember { MutableTransitionState(true) }

    DisposableEffect(visibleState.currentState) {
        if (!visibleState.currentState) {
            onDeleted()
        }
        onDispose {}
    }

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500),
            shrinkTowards = Alignment.Top,
        ) + slideOutVertically(
            targetOffsetY = { -it / 10 },
        ),
    ) {
        SwipeToReveal(
            state = revealState,
            revealOffset = 96.dp,
            revealed = {
                DeleteLayout(revealState, contentPadding) {
                    visibleState.targetState = false
                }
            },
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BoxScope.DeleteLayout(state: SwipeableState<RevealValue>, padding: Dp, onDelete: () -> Unit) {
    val backgroundColor by animateColorAsState(
        when (state.targetValue) {
            RevealValue.Hidden -> Color.Transparent
            RevealValue.Revealed -> colorResource(MR.colors.delete)
        },
    )

    Box(
        modifier = Modifier
            .matchParentSize()
            // Additional start padding to cope with swipeable overshoot animations
            .padding(top = padding, bottom = padding, start = padding + 20.dp)
            .background(backgroundColor)
            .clipToBounds()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colors.onSecondary,
            )
        }
    }
}
