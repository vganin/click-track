package net.ganin.vsevolod.clicktrack.view.screen

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
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
import net.ganin.vsevolod.clicktrack.state.actions.StoreUpdateClickTrack
import net.ganin.vsevolod.clicktrack.utils.compose.ObservableMutableState
import net.ganin.vsevolod.clicktrack.utils.compose.observableMutableStateOf
import net.ganin.vsevolod.clicktrack.utils.compose.swipeToRemove
import net.ganin.vsevolod.clicktrack.utils.compose.toObservableMutableStateList
import net.ganin.vsevolod.clicktrack.view.common.Constants.FAB_SIZE_WITH_PADDINGS
import net.ganin.vsevolod.clicktrack.view.widget.EditCueWithDurationView
import kotlin.time.minutes

@Composable
fun EditClickTrackScreenView(
    state: EditClickTrackScreenState,
    dispatch: Dispatch = Dispatch {}
) {
    val nameState = remember { observableMutableStateOf(state.clickTrack.value.name) }
    val loopState = remember { observableMutableStateOf(state.clickTrack.value.loop) }
    val cuesState = remember { state.clickTrack.value.cues.map(::observableMutableStateOf).toObservableMutableStateList() }

    fun update() {
        dispatch(
            StoreUpdateClickTrack(
                clickTrack = state.clickTrack.copy(
                    value = ClickTrack(
                        name = nameState.value,
                        loop = loopState.value,
                        cues = cuesState.map(ObservableMutableState<CueWithDuration>::value),
                    )
                )
            )
        )
    }
    onActive {
        nameState.observe { update() }
        loopState.observe { update() }
        cuesState.observe { update() }
        cuesState.forEach { it.observe { update() } }
    }

    Scaffold(
        topBar = { EditClickTrackScreenTopBar() },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { cuesState += observableMutableStateOf(state.defaultCue).observe { update() } }) {
                Icon(Icons.Default.Add)
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        EditClickTrackScreenContent(nameState, state.isErrorInName, loopState, cuesState)
    }
}

@Composable
private fun EditClickTrackScreenContent(
    nameState: MutableState<String>,
    isErrorInName: Boolean,
    loopState: MutableState<Boolean>,
    cuesState: MutableList<out MutableState<CueWithDuration>>,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = nameState.value,
                onValueChange = { nameState.value = it },
                placeholder = { Text(text = stringResource(R.string.click_track_name_hint)) },
                textStyle = MaterialTheme.typography.h6,
                isErrorValue = isErrorInName
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).align(Alignment.Center)) {
                    Text(text = stringResource(R.string.repeat))
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(checked = loopState.value, onCheckedChange = {
                        loopState.value = !loopState.value
                    })
                }
            }
        }

        itemsIndexed(items = cuesState) { index, cueState ->
            key(index, cueState) {
                WithConstraints {
                    CueListItem(
                        state = cueState,
                        modifier = Modifier.swipeToRemove(constraints = constraints, onDelete = {
                            cuesState.removeAt(index)
                        })
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(FAB_SIZE_WITH_PADDINGS))
        }
    }
}

@Composable
private fun CueListItem(
    state: MutableState<CueWithDuration>,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = 2.dp
    ) {
        EditCueWithDurationView(state)
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
            ),
            isErrorInName = false,
        ),
    )
}
