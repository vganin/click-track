package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.metronomeClickTrack
import com.vsevolodganin.clicktrack.state.redux.action.MetronomeAction
import com.vsevolodganin.clicktrack.state.redux.action.MetronomeAction.CloseOptions
import com.vsevolodganin.clicktrack.state.redux.action.MetronomeAction.OpenOptions
import com.vsevolodganin.clicktrack.state.redux.action.MetronomeAction.SetPattern
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.action.PlayerAction
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.MetronomeUiState
import com.vsevolodganin.clicktrack.ui.widget.BpmWheel
import com.vsevolodganin.clicktrack.ui.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.ui.widget.ClickTrackView
import com.vsevolodganin.clicktrack.ui.widget.PlayStopButton
import com.vsevolodganin.clicktrack.ui.widget.SubdivisionsChooser

@Composable
fun MetronomeScreenView(
    state: MetronomeUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    BackdropScaffold(
        appBar = { AppBar(dispatch) },
        backLayerContent = {
            Options(state, dispatch)
        },
        frontLayerContent = {
            Content(state, dispatch)
        },
        scaffoldState = backdropState(state, dispatch),
        modifier = modifier,
        // FIXME(https://issuetracker.google.com/issues/190893491)
        peekHeight = BackdropScaffoldDefaults.PeekHeight + 1.dp,
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
    state: MetronomeUiState,
    dispatch: Dispatch,
) {
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
                clickTrack = state.clickTrack.value,
                drawAllBeatsMarks = true,
                drawTextMarks = false,
                progress = state.progress,
                defaultLineWidth = with(LocalDensity.current) { 1f.dp.toPx() }
            )
        }

        val bpm = state.clickTrack.value.cues.first().bpm

        Text(
            text = bpm.value.toString(),
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

        Box(
            modifier = Modifier
                .constrainAs(bpmWheel) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                }
        ) {
            BpmWheel(
                value = bpm,
                onValueChange = { dispatch(MetronomeAction.ChangeBpm(it)) },
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
            PlayStopButton(
                isPlaying = state.isPlaying,
                onToggle = {
                    val action = if (state.isPlaying) {
                        PlayerAction.StopPlay
                    } else {
                        PlayerAction.StartPlay(state.clickTrack.id)
                    }
                    dispatch(action)
                },
                modifier = Modifier.align(Alignment.Center),
            )
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
    state: MetronomeUiState,
    dispatch: Dispatch,
) {
    SubdivisionsChooser(
        cue = state.clickTrack.value.cues.first(),
        onSubdivisionChoose = {
            dispatch(SetPattern(it))
            dispatch(CloseOptions)
        },
        modifier = Modifier.padding(8.dp),
        alwaysExpanded = true,
    )
}

@Composable
private fun backdropState(screenState: MetronomeUiState, dispatch: Dispatch): BackdropScaffoldState {
    val backdropValue = if (screenState.areOptionsExpanded) BackdropValue.Revealed else BackdropValue.Concealed
    return rememberBackdropScaffoldState(initialValue = backdropValue, confirmStateChange = { newDrawerValue ->
        when (newDrawerValue) {
            BackdropValue.Concealed -> dispatch(CloseOptions)
            BackdropValue.Revealed -> dispatch(OpenOptions)
        }
        false
    }).apply {
        LaunchedEffect(backdropValue) {
            when (backdropValue) {
                BackdropValue.Concealed -> conceal()
                BackdropValue.Revealed -> reveal()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MetronomeScreenView(
        MetronomeUiState(
            clickTrack = metronomeClickTrack(
                name = stringResource(R.string.metronome),
                bpm = 90.bpm,
                pattern = NotePattern.QUINTUPLET_X2,
            ),
            progress = ClickTrackProgress(0.1),
            isPlaying = false,
            areOptionsExpanded = true,
        )
    )
}