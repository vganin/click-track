package net.ganin.vsevolod.clicktrack.view

import androidx.compose.animation.OffsetPropKey
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.Icon
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.graphics.vector.VectorAssetBuilder
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.view.PlayStopIconState.PLAY
import net.ganin.vsevolod.clicktrack.view.PlayStopIconState.STOP

@Composable
fun PlayStopView(
    modifier: Modifier = Modifier,
    isPlayingState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onPlayToggle: (Boolean) -> Unit = { isPlayingState.value = !isPlayingState.value }
) {
    var isPlaying by isPlayingState
    FloatingActionButton(
        onClick = {
            isPlaying = !isPlaying
            onPlayToggle(isPlaying)
        },
        modifier = modifier,
        icon = { PlayStopIcon(isPlaying) }
    )
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
        asset = iconAsset(
            transition[pointA],
            transition[pointB],
            transition[pointC],
            transition[pointD],
        )
    )
}

@Composable
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
            fill = SolidColor(Color.Black)
        )
        .build()
}

private fun PathBuilder.moveTo(point: Offset) = moveTo(point.x, point.y)
private fun PathBuilder.lineTo(point: Offset) = lineTo(point.x, point.y)

@Preview
@Composable
fun PreviewPlayStopView(
) {
    PlayStopView()
}