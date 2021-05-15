package com.vsevolodganin.clicktrack.view.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Dispatch
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction.ChangePattern
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction.CloseOptions
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction.OpenOptions
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import com.vsevolodganin.clicktrack.view.widget.BpmWheel
import com.vsevolodganin.clicktrack.view.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.view.widget.ClickTrackView
import com.vsevolodganin.clicktrack.view.widget.PlayStopButton
import com.vsevolodganin.clicktrack.view.widget.SubdivisionsChooser

@Composable
fun MetronomeScreenView(
    state: MetronomeScreenState?,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    BackdropScaffold(
        appBar = { AppBar(dispatch) },
        backLayerContent = {
            if (state != null) {
                Options(state, dispatch)
            }
        },
        frontLayerContent = {
            if (state != null) {
                Content(state, dispatch)
            }
        },
        scaffoldState = backdropState(state, dispatch),
        modifier = modifier,
    )
}

@Composable
private fun AppBar(dispatch: Dispatch) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { dispatch(NavigationAction.Back) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = { Text(text = stringResource(R.string.metronome)) },
        actions = {
            IconButton(onClick = { dispatch(MetronomeAction.ToggleOptions) }) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null)
            }
        }
    )
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
    val metronomeClickTrack = metronomeClickTrack(bpmState.value, state.pattern)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (clickTrackRef, bpmText, bpmWheel, bpmMeter) = createRefs()

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(200.dp)
                .constrainAs(clickTrackRef) {},
            elevation = 8.dp,
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
private fun Options(
    state: MetronomeScreenState,
    dispatch: Dispatch,
) {
    SubdivisionsChooser(
        cue = metronomeCue(bpm = state.bpm, pattern = state.pattern),
        onSubdivisionChoose = {
            dispatch(ChangePattern(it))
            dispatch(CloseOptions)
        },
        modifier = Modifier.padding(8.dp),
        alwaysExpanded = true,
    )
}

@Composable
private fun metronomeClickTrack(bpm: BeatsPerMinute, pattern: NotePattern): ClickTrackWithId {
    val name = stringResource(R.string.metronome)
    val cue = metronomeCue(bpm, pattern)
    return remember(cue) {
        ClickTrackWithId(
            id = MetronomeId,
            value = ClickTrack(
                name = name,
                cues = listOf(cue),
                loop = true,
            )
        )
    }
}

@Composable
private fun metronomeCue(bpm: BeatsPerMinute, pattern: NotePattern): Cue = remember(bpm, pattern) {
    Cue(
        bpm = bpm,
        pattern = pattern,
        timeSignature = TimeSignature(4, 4),
        duration = CueDuration.Beats(4),
    )
}

@Composable
private fun backdropState(screenState: MetronomeScreenState?, dispatch: Dispatch): BackdropScaffoldState {
    val backdropValue = if (screenState?.areOptionsExpanded == true) BackdropValue.Revealed else BackdropValue.Concealed
    return rememberBackdropScaffoldState(
        initialValue = backdropValue,
        confirmStateChange = remember {
            { newDrawerValue ->
                when (newDrawerValue) {
                    BackdropValue.Concealed -> dispatch(CloseOptions)
                    BackdropValue.Revealed -> dispatch(OpenOptions)
                }
                true
            }
        }
    ).apply {
        LaunchedEffect(backdropValue) {
            try {
                when (backdropValue) {
                    BackdropValue.Concealed -> conceal()
                    BackdropValue.Revealed -> reveal()
                }
            } catch (e: IllegalArgumentException) {
                // FIXME: Ignoring spurious exception `java.lang.IllegalArgumentException: State androidx.compose.material.DrawerState@6294caf is not attached to a component. Have you passed state object to a component?`
            }
        }

        // FIXME(https://issuetracker.google.com/issues/181387076): Synchronizing manually internal backdrop state with external for now
        LaunchedEffect(currentValue) {
            if (backdropValue != currentValue && !isAnimationRunning) {
                when (currentValue) {
                    BackdropValue.Concealed -> dispatch(CloseOptions)
                    BackdropValue.Revealed -> dispatch(OpenOptions)
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MetronomeScreenView(
        MetronomeScreenState(
            bpm = 90.bpm,
            pattern = NotePattern.QUINTUPLET_X2,
            progress = 0.1,
            isPlaying = false,
            areOptionsExpanded = true,
        )
    )
}
