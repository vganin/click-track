package com.vsevolodganin.clicktrack.view.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.BuiltinClickSounds
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.actions.MetronomeActions
import com.vsevolodganin.clicktrack.state.actions.NavigateBack
import com.vsevolodganin.clicktrack.state.actions.StartPlay
import com.vsevolodganin.clicktrack.state.actions.StopPlay
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import com.vsevolodganin.clicktrack.view.widget.BpmWheel
import com.vsevolodganin.clicktrack.view.widget.ClickTrackView
import com.vsevolodganin.clicktrack.view.widget.PlayStopButton

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
    var progress by remember { mutableStateOf(state.progress ?: 0f) }
    val bpmState = observableMutableStateOf(state.bpm).observe { bpm ->
        dispatch(MetronomeActions.ChangeBpm(bpm, progress))
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
                progress = state.progress,
                onProgressChanged = { progress = it }
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
                val action = if (state.isPlaying) StopPlay else StartPlay(metronomeClickTrack, progress = 0f)
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
                Cue(
                    bpm = bpm,
                    timeSignature = TimeSignature(4, 4),
                    duration = CueDuration.Beats(4),
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
            progress = 0.13f,
            isPlaying = false,
        )
    )
}
