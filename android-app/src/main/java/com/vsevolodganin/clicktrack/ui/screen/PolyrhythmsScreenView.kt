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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsState
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModel
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.piece.PlayStopButton
import com.vsevolodganin.clicktrack.ui.piece.PolyrhythmCircle
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.utils.compose.AnimatableFloat
import com.vsevolodganin.clicktrack.utils.compose.FULL_ANGLE_DEGREES
import com.vsevolodganin.clicktrack.utils.compose.widthByText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Composable
fun PolyrhythmsScreenView(
    viewModel: PolyrhythmsViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(R.string.polyrhythms_screen_title)) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            PlayStopButton(
                isPlaying = state?.isPlaying ?: return@Scaffold,
                onToggle = viewModel::onTogglePlay
            )
        },
        modifier = modifier,
    ) {
        Content(viewModel, state ?: return@Scaffold)
    }
}

@Composable
private fun Content(
    viewModel: PolyrhythmsViewModel,
    state: PolyrhythmsState
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            NumberChooser(
                value = state.twoLayerPolyrhythm.layer1,
                onValueChoose = viewModel::onLayer1Change,
            )
            Spacer(modifier = Modifier.weight(1f))
            NumberChooser(
                value = state.twoLayerPolyrhythm.layer2,
                onValueChoose = viewModel::onLayer2Change,
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

    LaunchedEffect(progress) {
        val progressTimePosition = progress.realPosition
        val progressAnglePosition = progressTimePosition.toAngle(totalDuration)
        val animationDuration = totalDuration - progressTimePosition

        animatableProgressAngle.snapTo(progressAnglePosition)

        if (!progress.isPaused) {
            animatableProgressAngle.animateTo(
                targetValue = FULL_ANGLE_DEGREES,
                animationSpec = tween(
                    durationMillis = animationDuration.coerceAtLeast(Duration.ZERO).inWholeMilliseconds.toInt(),
                    easing = LinearEasing
                )
            )
        }
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

@ScreenPreview
@Composable
private fun Preview() = ClickTrackTheme {
    PolyrhythmsScreenView(
        viewModel = object : PolyrhythmsViewModel {
            override val state: StateFlow<PolyrhythmsState?> = MutableStateFlow(
                PolyrhythmsState(
                    twoLayerPolyrhythm = TwoLayerPolyrhythm(
                        bpm = 120.bpm,
                        layer1 = 3,
                        layer2 = 2
                    ),
                    isPlaying = true,
                    playableProgress = PlayProgress(100.milliseconds)
                )
            )

            override fun onBackClick() = Unit
            override fun onTogglePlay() = Unit
            override fun onLayer1Change(value: Int) = Unit
            override fun onLayer2Change(value: Int) = Unit
        }
    )
}
