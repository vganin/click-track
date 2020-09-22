package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm

@Composable
fun EditCueView(
    state: MutableState<Cue>,
    modifier: Modifier = Modifier
) {
    val bpmState = remember { mutableStateOf(state.value.bpm) }
    val timeSignatureState = remember { mutableStateOf(state.value.timeSignature) }

    Row(modifier = modifier) {
        val commonModifier = Modifier.fillMaxHeight().align(Alignment.CenterVertically)
        TimeSignatureView(state = timeSignatureState, modifier = commonModifier)
        Spacer(modifier = Modifier.width(8.dp))
        BpmWheel(state = bpmState, modifier = commonModifier)
    }

    state.value = Cue(
        bpm = bpmState.value,
        timeSignature = timeSignatureState.value
    )
}

@Preview
@Composable
fun PreviewEditCueView() {
    Stack(modifier = Modifier.fillMaxSize()) {
        EditCueView(
            state = mutableStateOf(
                Cue(
                    bpm = 60.bpm,
                    timeSignature = TimeSignature(3, 4)
                )
            ),
            modifier = Modifier.align(Alignment.Center).height(100.dp)
        )
    }
}
