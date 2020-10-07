package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.EditClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.actions.SaveClickTrack
import net.ganin.vsevolod.clicktrack.view.widget.EditCueWithDurationView
import kotlin.time.minutes

@Composable
fun EditClickTrackScreenView(
    state: EditClickTrackScreenState,
    dispatch: Dispatch = {}
) {
    Scaffold(
        topBar = { EditClickTrackScreenTopBar() },
        modifier = Modifier.fillMaxSize(),
    ) {
        EditClickTrackScreenContent(state, dispatch)
    }
}

@Composable
fun EditClickTrackScreenContent(
    state: EditClickTrackScreenState,
    dispatch: Dispatch = {}
) {
    val nameState = remember { mutableStateOf(state.clickTrack.name) }
    val loopState = remember { mutableStateOf(state.clickTrack.clickTrack.loop) }
    val cuesState = remember { state.clickTrack.clickTrack.cues.map { mutableStateOf(it) }.toMutableStateList() }

    val scrollState = rememberScrollState(0f)
    ScrollableColumn(scrollState = scrollState, modifier = Modifier.fillMaxSize()) {
        Row {
            TextField(value = nameState.value, onValueChange = { nameState.value = it })
            Text(text = "Should loop")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = loopState.value, onCheckedChange = {
                loopState.value = !loopState.value
            })
        }

        cuesState.forEach { cueWithDurationState ->
            EditCueWithDurationView(state = cueWithDurationState)
        }

        FloatingActionButton(
            onClick = { cuesState += mutableStateOf(state.defaultCue) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Image(asset = vectorResource(id = R.drawable.ic_add_24))
        }

        scrollState.scrollTo(scrollState.maxValue)
    }

    // Intentionally save on every re-composition
    dispatch(
        SaveClickTrack(
            clickTrack = ClickTrackWithMeta(
                name = nameState.value,
                clickTrack = ClickTrack(
                    cues = cuesState.map { it.value },
                    loop = loopState.value
                )
            )
        )
    )
}

@Composable
private fun EditClickTrackScreenTopBar() {
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.edit_click_track))
    })
}

@Preview
@Composable
fun PreviewEditClickTrackScreenView() {
    EditClickTrackScreenView(
        state = EditClickTrackScreenState(
            clickTrack = ClickTrackWithMeta(
                name = "Good click track",
                clickTrack = ClickTrack(
                    cues = listOf(
                        CueWithDuration(
                            cue = Cue(
                                bpm = 60.bpm,
                                timeSignature = TimeSignature(3, 4)
                            ),
                            duration = CueDuration.Beats(4),
                        ),
                        CueWithDuration(
                            cue = Cue(
                                bpm = 120.bpm,
                                timeSignature = TimeSignature(5, 4)
                            ),
                            duration = CueDuration.Time(SerializableDuration(1.minutes)),
                        ),
                    ),
                    loop = true,
                )
            )
        ),
    )
}
