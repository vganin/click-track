package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.utils.compose.observableMutableStateOf
import kotlin.time.minutes

@Composable
fun EditCueWithDurationView(
    state: MutableState<CueWithDuration>,
    modifier: Modifier = Modifier
) {
    val bpmState = remember { observableMutableStateOf(state.value.cue.bpm) }
    val timeSignatureState = remember { observableMutableStateOf(state.value.cue.timeSignature) }
    val durationState = remember { observableMutableStateOf(state.value.duration) }

    onActive {
        fun update() {
            state.value = CueWithDuration(
                cue = Cue(
                    bpm = bpmState.value,
                    timeSignature = timeSignatureState.value
                ),
                duration = durationState.value
            )
        }
        bpmState.observe { update() }
        timeSignatureState.observe { update() }
        durationState.observe { update() }
    }

    Row(modifier = modifier.padding(8.dp)) {
        val viewModifier = Modifier.align(Alignment.CenterVertically)
        val spacerModifier = Modifier.width(16.dp)

        EditCueDurationView(state = durationState, modifier = viewModifier)
        Spacer(modifier = spacerModifier)
        TimeSignatureView(state = timeSignatureState, modifier = viewModifier)
        Spacer(modifier = spacerModifier)
        BpmWheel(
            state = bpmState,
            modifier = viewModifier,
            textStyle = MaterialTheme.typography.h6
        )
    }
}

@Preview
@Composable
fun PreviewEditCueWithDurationView() {
    EditCueWithDurationView(
        state = mutableStateOf(
            CueWithDuration(
                cue = Cue(
                    bpm = 999.bpm,
                    timeSignature = TimeSignature(3, 4)
                ),
                duration = CueDuration.Time(SerializableDuration(1.minutes))
            )
        ),
    )
}
