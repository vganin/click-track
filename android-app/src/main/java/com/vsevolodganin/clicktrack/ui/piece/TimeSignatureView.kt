package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vsevolodganin.clicktrack.model.TimeSignature

@Composable
fun TimeSignatureView(
    state: MutableState<TimeSignature>,
    modifier: Modifier = Modifier,
) {
    TimeSignatureView(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
    )
}

@Composable
fun TimeSignatureView(
    value: TimeSignature,
    onValueChange: (TimeSignature) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        val commonModifier = Modifier.align(Alignment.CenterVertically)
        val commonTextStyle = MaterialTheme.typography.subtitle1
        val commonNumberRange = 1..64
        NumberPicker(
            value = value.noteCount,
            onValueChange = { onValueChange(value.copy(noteCount = it)) },
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
            value = value.noteValue,
            onValueChange = { onValueChange(value.copy(noteValue = it)) },
            modifier = commonModifier,
            textStyle = commonTextStyle,
            range = commonNumberRange,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.fillMaxSize()) {
        TimeSignatureView(
            state = remember { mutableStateOf(TimeSignature(4, 4)) },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
