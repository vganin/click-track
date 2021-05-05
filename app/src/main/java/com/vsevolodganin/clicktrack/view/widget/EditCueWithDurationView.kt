package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.SerializableDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import kotlin.time.minutes

@Composable
fun EditCueWithDurationView(
    state: MutableState<Cue>,
    modifier: Modifier = Modifier,
) {
    val bpmState = remember { observableMutableStateOf(state.value.bpm) }
    val timeSignatureState = remember { observableMutableStateOf(state.value.timeSignature) }
    val durationState = remember { observableMutableStateOf(state.value.duration) }

    LaunchedEffect(Unit) {
        fun update() {
            state.value = state.value.copy(
                bpm = bpmState.value,
                timeSignature = timeSignatureState.value,
                duration = durationState.value
            )
        }
        bpmState.observe { update() }
        timeSignatureState.observe { update() }
        durationState.observe { update() }
    }

    Column(modifier = modifier.padding(8.dp)) {
        Row {
            val viewModifier = Modifier.align(Alignment.CenterVertically)
            val spacerModifier = Modifier.width(16.dp)

            EditCueDurationView(state = durationState, modifier = viewModifier.height(IntrinsicSize.Min))

            Spacer(modifier = spacerModifier)

            TimeSignatureView(state = timeSignatureState, modifier = viewModifier)

            Spacer(modifier = spacerModifier)

            BpmWheel(
                state = bpmState,
                modifier = viewModifier,
            ) {
                Text(
                    text = bpmState.value.value.toString(),
                    style = MaterialTheme.typography.h6,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SubdivisionsChooser(
            cue = state.value,
            onSubdivisionChoose = {
                state.value = state.value.copy(pattern = it)
            }
        )
    }
}

@Preview
@Composable
fun PreviewEditCueWithDurationView() {
    EditCueWithDurationView(
        state = remember {
            mutableStateOf(
                Cue(
                    bpm = 999.bpm,
                    timeSignature = TimeSignature(3, 4),
                    duration = CueDuration.Time(SerializableDuration(1.minutes))
                )
            )
        },
    )
}
