package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.TimeSignature

@Composable
fun TimeSignatureView(
    state: MutableState<TimeSignature>,
    modifier: Modifier = Modifier
) {
    val noteCountState = remember { mutableStateOf(state.value.noteCount) }
    val noteDurationState = remember { mutableStateOf(state.value.noteDuration) }

    Row(modifier = modifier) {
        val commonModifier = Modifier.align(Alignment.CenterVertically)
        val commonTextStyle = MaterialTheme.typography.subtitle1
        val commonNumberRange = 1..64
        NumberPicker(
            state = noteCountState,
            modifier = commonModifier,
            textStyle = commonTextStyle,
            range = commonNumberRange,
        )
        Text(
            text = "/",
            style = commonTextStyle,
            modifier = commonModifier,
        )
        NumberPicker(
            state = noteDurationState,
            modifier = commonModifier,
            textStyle = commonTextStyle,
            range = commonNumberRange,
        )
    }

    state.value = TimeSignature(
        noteCount = noteCountState.value,
        noteDuration = noteDurationState.value
    )
}

@Preview
@Composable
fun PreviewTimeSignatureView() {
    Box(modifier = Modifier.fillMaxSize()) {
        TimeSignatureView(
            state = mutableStateOf(TimeSignature(4, 4)),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
