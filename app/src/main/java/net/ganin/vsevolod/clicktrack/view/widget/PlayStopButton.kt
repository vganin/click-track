package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.animation.OffsetPropKey
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.graphics.vector.VectorAssetBuilder
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.view.widget.PlayStopIconState.PLAY
import net.ganin.vsevolod.clicktrack.view.widget.PlayStopIconState.STOP

@Composable
fun PlayStopButton(
    isPlaying: Boolean = false,
    onToggle: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
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
    val pointA = remember { OffsetPropKey() }
    val pointB = remember { OffsetPropKey() }
    val pointC = remember { OffsetPropKey() }
    val pointD = remember { OffsetPropKey() }

    val transition = transition(
        definition = remember {
            transitionDefinition {
                state(STOP) {
                    this[pointA] = Offset(6f, 6f)
                    this[pointB] = Offset(18f, 6f)
                    this[pointC] = Offset(18f, 18f)
                    this[pointD] = Offset(6f, 18f)
                }
                state(PLAY) {
                    this[pointA] = Offset(8f, 5f)
                    this[pointB] = Offset(19f, 12f)
                    this[pointC] = Offset(19f, 12f)
                    this[pointD] = Offset(8f, 19f)
                }
                transition {
                    arrayOf(pointA, pointB).forEach {
                        it using spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    }
                }
            }
        },
        toState = if (isPlaying) STOP else PLAY,
    )

    Icon(
        iconAsset(
            transition[pointA],
            transition[pointB],
            transition[pointC],
            transition[pointD],
        )
    )
}

private fun iconAsset(a: Offset, b: Offset, c: Offset, d: Offset): VectorAsset {
    return VectorAssetBuilder(defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
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
fun PreviewPlayStopButton() {
    PlayStopButton()
}
