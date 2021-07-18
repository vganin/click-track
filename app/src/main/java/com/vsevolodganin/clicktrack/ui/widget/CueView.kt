package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.state.redux.EditCueState
import com.vsevolodganin.clicktrack.ui.model.EditCueUiState
import kotlin.time.Duration

@Composable
fun CueView(
    value: EditCueUiState,
    displayPosition: Int,
    onNameChange: (String) -> Unit,
    onBpmChange: (Int) -> Unit,
    onTimeSignatureChange: (TimeSignature) -> Unit,
    onDurationChange: (CueDuration) -> Unit,
    onDurationTypeChange: (EditCueState.DurationType) -> Unit,
    onPatternChange: (NotePattern) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(8.dp)) {
        Row {
            Text(
                text = stringResource(R.string.cue_position, displayPosition),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = value.name,
                onValueChange = onNameChange,
                placeholder = {
                    Text(stringResource(R.string.cue_name_hint))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            CueDurationView(
                value = value.duration,
                onValueChange = onDurationChange,
                onTypeChange = onDurationTypeChange,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(IntrinsicSize.Min)
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            TimeSignatureView(
                value = value.timeSignature,
                onValueChange = onTimeSignatureChange,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(16.dp))

            val bpmSuffix = stringResource(R.string.cue_bpm_suffix)
            NumberInputField(
                value = value.bpm,
                onValueChange = onBpmChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                isError = EditCueState.Error.BPM in value.errors,
                maxDigitsCount = 3,
                visualTransformation = { inputText ->
                    TransformedText(
                        text = inputText + AnnotatedString(bpmSuffix),
                        offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int = offset
                            override fun transformedToOriginal(offset: Int): Int = offset.coerceIn(0..inputText.length)
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SubdivisionsChooser(
            pattern = value.pattern,
            timeSignature = value.timeSignature,
            onSubdivisionChoose = onPatternChange,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CueView(
        value = EditCueUiState(
            name = "",
            bpm = 999,
            timeSignature = TimeSignature(3, 4),
            duration = CueDuration.Time(Duration.minutes(1)),
            pattern = NotePattern.STRAIGHT_X1,
            errors = setOf(EditCueState.Error.BPM)
        ),
        displayPosition = 1,
        onNameChange = {},
        onBpmChange = {},
        onTimeSignatureChange = {},
        onDurationChange = {},
        onDurationTypeChange = {},
        onPatternChange = {},
    )
}
