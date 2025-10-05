package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.AlertDialog
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.general_cancel
import clicktrack.multiplatform.generated.resources.general_ok
import clicktrack.multiplatform.generated.resources.time_signature_edit_dialog_title
import clicktrack.multiplatform.generated.resources.time_signature_note_count_header
import clicktrack.multiplatform.generated.resources.time_signature_note_value_eighth
import clicktrack.multiplatform.generated.resources.time_signature_note_value_half
import clicktrack.multiplatform.generated.resources.time_signature_note_value_header
import clicktrack.multiplatform.generated.resources.time_signature_note_value_quarter
import clicktrack.multiplatform.generated.resources.time_signature_note_value_sixteenth
import clicktrack.multiplatform.generated.resources.time_signature_note_value_thirty_second
import clicktrack.multiplatform.generated.resources.time_signature_note_value_whole
import clicktrack.multiplatform.generated.resources.time_signature_reset_to_four_four
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimeSignatureEditDialog(
    value: TimeSignature,
    onValueChange: (TimeSignature) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var localValue by remember { mutableStateOf(value) }
        .apply { this.value = value }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(Res.string.time_signature_edit_dialog_title),
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            TimeSignatureViewDialogContent(
                value = localValue,
                onValueChange = { localValue = it },
                modifier = Modifier.height(200.dp),
            )
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { localValue = TimeSignature(4, 4) }) {
                        Text(stringResource(Res.string.time_signature_reset_to_four_four).uppercase())
                    }

                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(Res.string.general_cancel).uppercase())
                    }

                    TextButton(
                        onClick = {
                            onValueChange(localValue)
                            onDismissRequest()
                        },
                    ) {
                        Text(stringResource(Res.string.general_ok).uppercase())
                    }
                }
            }
        },
    )
}

@Composable
private fun TimeSignatureViewDialogContent(
    value: TimeSignature,
    onValueChange: (TimeSignature) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
        NoteCountColumn(
            value = value.noteCount,
            onValueChange = {
                onValueChange(value.copy(noteCount = it))
            },
        )
        NoteValueColumn(
            value = value.noteValue,
            onValueChange = {
                onValueChange(value.copy(noteValue = it))
            },
        )
    }
}

@Composable
private fun NoteCountColumn(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.time_signature_note_count_header),
            style = MaterialTheme.typography.subtitle1,
        )

        val noteCounts = remember { (1..32).toList() }
        WheelPicker(
            selectedIndex = noteCounts
                .indexOfFirst { it == value }
                .coerceIn(noteCounts.indices),
            items = noteCounts,
            onItemSelect = { _, item -> onValueChange(item) },
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp),
        ) { index, item, isSelected, closenessToSelection ->
            WheelItem(
                item = item.toString(),
                isSelected = isSelected,
                closenessToSelection = closenessToSelection,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun NoteValueColumn(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.time_signature_note_value_header),
            style = MaterialTheme.typography.subtitle1,
        )

        val possibleNoteValues = remember {
            listOf(
                1 to Res.string.time_signature_note_value_whole,
                2 to Res.string.time_signature_note_value_half,
                4 to Res.string.time_signature_note_value_quarter,
                8 to Res.string.time_signature_note_value_eighth,
                16 to Res.string.time_signature_note_value_sixteenth,
                32 to Res.string.time_signature_note_value_thirty_second,
            )
        }

        WheelPicker(
            selectedIndex = possibleNoteValues
                .indexOfFirst { it.first == value }
                .coerceIn(possibleNoteValues.indices),
            items = possibleNoteValues.map { it.second },
            onItemSelect = { index, _ -> onValueChange(possibleNoteValues[index].first) },
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp),
        ) { index, item, isSelected, closenessToSelection ->
            WheelItem(
                item = stringResource(item),
                isSelected = isSelected,
                closenessToSelection = closenessToSelection,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun LazyItemScope.WheelItem(
    item: String,
    isSelected: Boolean,
    closenessToSelection: Float,
    textAlign: TextAlign,
) {
    val easedClosenessToSelection = EaseOutQuad.transform(closenessToSelection)
    val scale = lerp(0.3f, 1f, easedClosenessToSelection)
    val color by animateColorAsState(if (isSelected) MaterialTheme.colors.primary else LocalContentColor.current)

    Text(
        text = item,
        modifier = Modifier
            .fillParentMaxWidth()
            .graphicsLayer {
                transformOrigin = if (textAlign == TextAlign.Start) {
                    TransformOrigin(0f, 0.5f)
                } else {
                    TransformOrigin(1f, 0.5f)
                }
                scaleX = scale
                scaleY = scale
            }
            .alpha(easedClosenessToSelection),
        color = color,
        textAlign = textAlign,
        style = MaterialTheme.typography.subtitle1,
    )
}

@Preview
@Composable
private fun Preview() = ClickTrackTheme {
    TimeSignatureEditDialog(
        value = TimeSignature(4, 4),
        onValueChange = {},
        onDismissRequest = {},
    )
}
