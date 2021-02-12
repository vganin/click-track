package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf

@Composable
fun TimeSignatureView(
    state: MutableState<TimeSignature>,
    modifier: Modifier = Modifier
) {
    val noteCountState = remember { observableMutableStateOf(state.value.noteCount) }
    val noteDurationState = remember { observableMutableStateOf(state.value.noteDuration) }

    LaunchedEffect(Unit) {
        fun update() {
            state.value = TimeSignature(
                noteCount = noteCountState.value,
                noteDuration = noteDurationState.value
            )
        }
        noteCountState.observe { update() }
        noteDurationState.observe { update() }
    }

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
