package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.lib.BeatsPerMinute
import net.ganin.vsevolod.clicktrack.lib.BuiltinClickSounds
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.model.MetronomeId
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.MetronomeScreenState
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.state.actions.MetronomeActions
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.actions.StartPlay
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.utils.compose.observableMutableStateOf
import net.ganin.vsevolod.clicktrack.view.widget.BpmWheel
import net.ganin.vsevolod.clicktrack.view.widget.ClickTrackView
import net.ganin.vsevolod.clicktrack.view.widget.PlayStopButton
import kotlin.time.milliseconds
import kotlin.time.seconds

@Composable
fun MetronomeScreenView(
    state: MetronomeScreenState?,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { MetronomeScreenViewTopBar(dispatch) },
        modifier = modifier,
    ) {
        if (state != null) {
            MetronomeScreenViewContent(state, dispatch)
        }
    }
}

@Composable
private fun MetronomeScreenViewTopBar(dispatch: Dispatch) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { dispatch(NavigateBack) }) {
                Icon(imageVector = Icons.Default.ArrowBack)
            }
        },
        title = { Text(text = stringResource(id = R.string.metronome)) }
    )
}

@Composable
private fun MetronomeScreenViewContent(
    state: MetronomeScreenState,
    dispatch: Dispatch,
) {
    val bpmState = observableMutableStateOf(state.bpm).observe {
        dispatch(MetronomeActions.ChangeBpm(it))
    }
    val metronomeClickTrack = metronomeClickTrack(bpmState.value)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (clickTrackRef, bpmText, bpmWheel) = createRefs()

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
                playbackTimestamp = state.playbackStamp,
            )
        }

        Text(
            text = bpmState.value.value.toString(),
            style = MaterialTheme.typography.h1,
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
                .preferredSize(200.dp)
                .padding(32.dp)
                .constrainAs(bpmWheel) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            PlayStopButton(state.isPlaying, onToggle = {
                val action = if (state.isPlaying) StopPlay else StartPlay(metronomeClickTrack)
                dispatch(action)
            })
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
                CueWithDuration(
                    duration = CueDuration.Beats(4),
                    cue = Cue(
                        bpm = bpm,
                        timeSignature = TimeSignature(4, 4)
                    ),
                )
            ),
            loop = true,
            sounds = BuiltinClickSounds
        )
    )
}

@Preview
@Composable
fun PreviewMetronomeScreenView() {
    MetronomeScreenView(
        MetronomeScreenState(
            bpm = 90.bpm,
            playbackStamp = PlaybackStamp(
                timestamp = SerializableDuration(500.milliseconds),
                duration = SerializableDuration(4.seconds)
            ),
            isPlaying = false,
        )
    )
}
