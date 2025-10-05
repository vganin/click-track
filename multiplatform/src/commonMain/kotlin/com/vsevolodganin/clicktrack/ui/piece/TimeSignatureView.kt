package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.model.TimeSignature
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimeSignatureView(state: MutableState<TimeSignature>, modifier: Modifier = Modifier) {
    TimeSignatureView(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimeSignatureView(value: TimeSignature, onValueChange: (TimeSignature) -> Unit, modifier: Modifier = Modifier) {
    var isEditDialogOpened by rememberSaveable { mutableStateOf(false) }

    Chip(onClick = { isEditDialogOpened = !isEditDialogOpened }, modifier = modifier) {
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
private fun Preview() {
    TimeSignatureView(
        state = remember { mutableStateOf(TimeSignature(4, 4)) },
    )
}
