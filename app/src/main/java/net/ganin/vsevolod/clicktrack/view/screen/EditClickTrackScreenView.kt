package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.EditClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrack
import net.ganin.vsevolod.clicktrack.view.widget.EditCueWithDurationView
import kotlin.time.minutes

@Composable
fun EditClickTrackScreenView(
    state: EditClickTrackScreenState,
    dispatch: Dispatch = {}
) {
    val nameState = remember { mutableStateOf(state.clickTrack.value.name) }
    val loopState = remember { mutableStateOf(state.clickTrack.value.loop) }
    val cuesState = remember { state.clickTrack.value.cues.map { mutableStateOf(it) }.toMutableStateList() }

    Scaffold(
        topBar = { EditClickTrackScreenTopBar() },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { cuesState += mutableStateOf(state.defaultCue) }) {
                Icon(asset = vectorResource(id = R.drawable.ic_add_24))
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        EditClickTrackScreenContent(nameState, loopState, cuesState)
    }

    val name = nameState.value
    val loop = loopState.value
    val cues = cuesState.map { it.value }
    onCommit(name, loop, cues) {
        GlobalScope.launch(Dispatchers.Main) {
            dispatch(
                UpdateClickTrack(
                    clickTrack = state.clickTrack.copy(
                        value = ClickTrack(
                            name = name,
                            cues = cues,
                            loop = loop,
                        )
                    )
                )
            )
        }
    }
}

@Composable
private fun EditClickTrackScreenContent(
    nameState: MutableState<String>,
    loopState: MutableState<Boolean>,
    cuesState: List<MutableState<CueWithDuration>>,
) {
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

        scrollState.scrollTo(scrollState.maxValue)
    }
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
            clickTrack = ClickTrackWithId(
                id = 0,
                value = ClickTrack(
                    name = "Good click track",
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
