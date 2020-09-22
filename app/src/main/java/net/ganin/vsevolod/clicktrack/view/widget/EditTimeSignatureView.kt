package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
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
        NumberPicker(
            noteCountState,
            modifier = commonModifier
        )
        Text(
            text = "/",
            style = TextStyle(fontSize = 24.sp),
            modifier = commonModifier
        )
        NumberPicker(
            state = noteDurationState,
            modifier = commonModifier
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
    Stack(modifier = Modifier.fillMaxSize()) {
        TimeSignatureView(
            state = mutableStateOf(TimeSignature(4, 4)),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
