package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.view.widget.PlayStopIconState.PLAY
import com.vsevolodganin.clicktrack.view.widget.PlayStopIconState.STOP

@Composable
fun PlayStopButton(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ClickTrackFloatingActionButton(
        onClick = onToggle,
        modifier = modifier,
    ) {
        PlayStopIcon(isPlaying)
    }
}

private enum class PlayStopIconState {
    PLAY, STOP
}

@Composable
private fun PlayStopIcon(isPlaying: Boolean) {
    val transition = updateTransition(targetState = if (isPlaying) STOP else PLAY)

    @Composable
    fun Transition<PlayStopIconState>.animatePoint(
        targetValueByState: @Composable (state: PlayStopIconState) -> Offset,
    ): State<Offset> {
        return animateOffset(
            transitionSpec = {
                spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            },
            targetValueByState = targetValueByState
        )
    }

    val pointA by transition.animatePoint { state ->
        when (state) {
            PLAY -> Offset(8f, 5f)
            STOP -> Offset(6f, 6f)
        }
    }
    val pointB by transition.animatePoint { state ->
        when (state) {
            PLAY -> Offset(19f, 12f)
            STOP -> Offset(18f, 6f)
        }
    }
    val pointC by transition.animatePoint { state ->
        when (state) {
            PLAY -> Offset(19f, 12f)
            STOP -> Offset(18f, 18f)
        }
    }
    val pointD by transition.animatePoint { state ->
        when (state) {
            PLAY -> Offset(8f, 19f)
            STOP -> Offset(6f, 18f)
        }
    }

    Icon(
        imageVector = iconAsset(
            pointA,
            pointB,
            pointC,
            pointD,
        ),
        contentDescription = null
    )
}

private fun iconAsset(a: Offset, b: Offset, c: Offset, d: Offset): ImageVector {
    return ImageVector.Builder(defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
        .addPath(
            pathData = PathBuilder()
                .moveTo(a)
                .lineTo(b)
                .lineTo(c)
                .lineTo(d)
                .close()
                .getNodes(),
            fill = SolidColor(Color.White)
        )
        .build()
}

private fun PathBuilder.moveTo(point: Offset) = moveTo(point.x, point.y)
private fun PathBuilder.lineTo(point: Offset) = lineTo(point.x, point.y)

@Preview
@Composable
private fun Preview() {
    PlayStopButton(
        isPlaying = false,
        onToggle = {},
    )
}
