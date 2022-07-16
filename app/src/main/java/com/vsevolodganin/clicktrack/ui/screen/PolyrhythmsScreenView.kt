package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.redux.action.PlayerAction
import com.vsevolodganin.clicktrack.redux.action.PolyrhythmsAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.PolyrhythmsUiState
import com.vsevolodganin.clicktrack.ui.piece.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.PlayStopButton
import com.vsevolodganin.clicktrack.ui.piece.PolyrhythmCircle
import com.vsevolodganin.clicktrack.utils.compose.AnimatableFloat
import com.vsevolodganin.clicktrack.utils.compose.FULL_ANGLE_DEGREES
import com.vsevolodganin.clicktrack.utils.compose.widthByText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Composable
fun PolyrhythmsScreenView(
    state: PolyrhythmsUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.polyrhythms_title, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            PlayStopButton(
                isPlaying = state.isPlaying,
                onToggle = {
                    val action = if (state.isPlaying) {
                        PlayerAction.StopPlay
                    } else {
                        PlayerAction.StartPlayPolyrhythm
                    }
                    dispatch(action)
                }
            )
        },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }
}

@Composable
private fun Content(state: PolyrhythmsUiState, dispatch: Dispatch) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            NumberChooser(
                value = state.twoLayerPolyrhythm.layer1,
                onValueChoose = {
                    dispatch(PolyrhythmsAction.EditLayer1(it))
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            NumberChooser(
                value = state.twoLayerPolyrhythm.layer2,
                onValueChoose = {
                    dispatch(PolyrhythmsAction.EditLayer2(it))
                },
            )
        }

        PolyrhythmCircleWrapper(
            layer1 = state.twoLayerPolyrhythm.layer1,
            layer2 = state.twoLayerPolyrhythm.layer2,
            progress = state.playableProgress,
            totalDuration = state.twoLayerPolyrhythm.durationInTime,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        )
    }
}

@Composable
private fun NumberChooser(
    value: Int,
    onValueChoose: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = { onValueChoose(value - 1) },
            modifier = Modifier.align(CenterVertically),
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = null)
        }
        val textStyle = MaterialTheme.typography.h5
        Text(
            text = value.toString(),
            modifier = Modifier
                .align(CenterVertically)
                .widthByText("99", textStyle),
            style = textStyle,
            textAlign = TextAlign.Center,
        )
        IconButton(
            onClick = { onValueChoose(value + 1) },
            modifier = Modifier.align(CenterVertically),
        ) {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun PolyrhythmCircleWrapper(
    layer1: Int,
    layer2: Int,
    progress: PlayProgress?,
    totalDuration: Duration,
    modifier: Modifier,
) {
    val progressAngle = progressAngle(progress, totalDuration)?.asState()

    PolyrhythmCircle(
        outerDotNumber = layer1,
        innerDotNumber = layer2,
        modifier = modifier,
        progressAngle = progressAngle?.value,
        progressVelocity = (FULL_ANGLE_DEGREES / totalDuration.toDouble(DurationUnit.SECONDS)).toFloat()
    )
}

@Composable
private fun progressAngle(
    progress: PlayProgress?,
    totalDuration: Duration,
): AnimatableFloat? {
    progress ?: return null

    val animatableProgressAngle = remember {
        Animatable(0f).apply {
            updateBounds(0f, FULL_ANGLE_DEGREES)
        }
    }

    var cachedProgress by remember { mutableStateOf(progress) }

    LaunchedEffect(progress) {
        val progressTimePosition = progress.position + progress.emissionTime.elapsedNow()
        val progressAnglePosition = progressTimePosition.toAngle(totalDuration)
        val animationDuration = totalDuration - progressTimePosition

        if (progress.position <= cachedProgress.position) {
            cachedProgress = progress
            animatableProgressAngle.snapTo(progressAnglePosition)
        }

        animatableProgressAngle.animateTo(
            targetValue = FULL_ANGLE_DEGREES,
            animationSpec = tween(
                durationMillis = animationDuration.coerceAtLeast(Duration.ZERO).inWholeMilliseconds.toInt(),
                easing = LinearEasing
            )
        )
    }

    return animatableProgressAngle
}

private fun Duration.toAngle(totalDuration: Duration): Float {
    return if (totalDuration == Duration.ZERO) {
        0f
    } else {
        (this / totalDuration * FULL_ANGLE_DEGREES).toFloat()
    }
}

@Preview
@Composable
private fun Preview() {
    PolyrhythmsScreenView(
        state = PolyrhythmsUiState(
            twoLayerPolyrhythm = TwoLayerPolyrhythm(
                bpm = 120.bpm,
                layer1 = 3,
                layer2 = 2
            ),
            isPlaying = true,
            playableProgress = PlayProgress(100.milliseconds)
        ),
    )
}
