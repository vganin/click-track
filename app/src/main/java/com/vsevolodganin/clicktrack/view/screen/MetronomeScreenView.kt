package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import com.vsevolodganin.clicktrack.view.widget.BpmWheel
import com.vsevolodganin.clicktrack.view.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.view.widget.ClickTrackView
import com.vsevolodganin.clicktrack.view.widget.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.view.widget.PlayStopButton

@Composable
fun MetronomeScreenView(
    state: MetronomeScreenState?,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.metronome, dispatch) },
        modifier = modifier,
    ) {
        if (state != null) {
            Content(state, dispatch)
        }
    }
}

@Composable
private fun Content(
    state: MetronomeScreenState,
    dispatch: Dispatch,
) {
    val bpmState = remember(state.bpm) {
        observableMutableStateOf(state.bpm).observe { bpm ->
            dispatch(MetronomeAction.ChangeBpm(bpm))
        }
    }
    val metronomeClickTrack = metronomeClickTrack(bpmState.value)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (clickTrackRef, bpmText, bpmWheel, bpmMeter) = createRefs()

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(200.dp)
                .constrainAs(clickTrackRef) {},
        ) {
            ClickTrackView(
                clickTrack = metronomeClickTrack.value,
                drawAllBeatsMarks = true,
                drawTextMarks = false,
                progress = state.progress,
                defaultLineWidth = with(LocalDensity.current) { 1f.dp.toPx() }
            )
        }

        Text(
            text = bpmState.value.value.toString(),
            style = MaterialTheme.typography.h1.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 8.sp,
            ),
            modifier = Modifier
                .constrainAs(bpmText) {
                    centerHorizontallyTo(parent)
                    top.linkTo(clickTrackRef.bottom)
                    bottom.linkTo(bpmWheel.top)
                }
        )

        BpmWheel(
            state = bpmState,
            modifier = Modifier
                .size(200.dp)
                .constrainAs(bpmWheel) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                }
        ) {
            PlayStopButton(state.isPlaying, onToggle = {
                val action = if (state.isPlaying) {
                    ClickTrackAction.StopPlay
                } else {
                    ClickTrackAction.StartPlay(metronomeClickTrack)
                }
                dispatch(action)
            })
        }

        ClickTrackFloatingActionButton(
            onClick = { dispatch(MetronomeAction.BpmMeterTap) },
            modifier = Modifier
                .size(64.dp)
                .constrainAs(bpmMeter) {
                    centerVerticallyTo(bpmWheel)
                    start.linkTo(bpmWheel.end)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = stringResource(id = R.string.bpm_meter_tap),
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                )
            )
        }
    }
}

@Composable
private fun metronomeClickTrack(bpm: BeatsPerMinute): ClickTrackWithId {
    return ClickTrackWithId(
        id = MetronomeId,
        value = ClickTrack(
            name = stringResource(R.string.metronome),
            cues = listOf(
                Cue(
                    bpm = bpm,
                    timeSignature = TimeSignature(4, 4),
                    duration = CueDuration.Beats(4),
                )
            ),
            loop = true,
        )
    )
}

@Preview
@Composable
private fun Preview() {
    MetronomeScreenView(
        MetronomeScreenState(
            bpm = 90.bpm,
            progress = 0.13,
            isPlaying = false,
        )
    )
}
