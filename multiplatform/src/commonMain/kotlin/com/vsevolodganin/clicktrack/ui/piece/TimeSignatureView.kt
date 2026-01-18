package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme

@Composable
fun TimeSignatureView(state: MutableState<TimeSignature>, modifier: Modifier = Modifier) {
    TimeSignatureView(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
    )
}

@Composable
fun TimeSignatureView(value: TimeSignature, onValueChange: (TimeSignature) -> Unit, modifier: Modifier = Modifier) {
    var isEditDialogOpened by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .selectableBorder(isSelected = false)
            .clickable { isEditDialogOpened = !isEditDialogOpened }
            .padding(8.dp),
    ) {
        Text(text = "${value.noteCount}/${value.noteValue}")
    }

    if (isEditDialogOpened) {
        TimeSignatureEditDialog(
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { isEditDialogOpened = false },
        )
    }
}

@Preview
@Composable
private fun Preview() = ClickTrackTheme {
    TimeSignatureView(
        state = remember { mutableStateOf(TimeSignature(4, 4)) },
    )
}
