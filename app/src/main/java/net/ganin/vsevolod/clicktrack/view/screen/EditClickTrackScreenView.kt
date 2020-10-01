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
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
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
import net.ganin.vsevolod.clicktrack.view.widget.EditCueWithDurationView
import kotlin.time.minutes

@Composable
fun EditClickTrackScreenView(
    state: MutableState<ClickTrack>,
    defaultCue: () -> CueWithDuration = {
        CueWithDuration(
            duration = CueDuration.Beats(4),
            cue = Cue(60.bpm, TimeSignature(4, 4))
        )
    },
    modifier: Modifier = Modifier
) {
    val loopState = remember { mutableStateOf(state.value.loop) }
    val cuesState = remember { state.value.cues.map { mutableStateOf(it) }.toMutableStateList() }

    val scrollState = rememberScrollState(0f)
    ScrollableColumn(scrollState = scrollState, modifier = modifier) {
        Row {
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
            onClick = { cuesState += mutableStateOf(defaultCue()) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Image(asset = vectorResource(id = R.drawable.ic_add_24))
        }

        scrollState.scrollTo(scrollState.maxValue)
    }

    state.value = ClickTrack(
        cues = cuesState.map { it.value },
        loop = loopState.value
    )
}


@Preview
@Composable
fun PreviewEditClickTrackScreenView() {
    EditClickTrackScreenView(
        state = mutableStateOf(
            ClickTrack(
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
        modifier = Modifier.fillMaxSize()
    )
}
