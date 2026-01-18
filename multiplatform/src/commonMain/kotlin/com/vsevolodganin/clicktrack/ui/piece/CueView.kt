package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.cue_name_hint
import clicktrack.multiplatform.generated.resources.cue_position
import com.vsevolodganin.clicktrack.edit.EditCueState
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import org.jetbrains.compose.resources.stringResource

@Composable
fun CueView(
    value: EditCueState,
    onNameChange: (String) -> Unit,
    onBpmChange: (Int) -> Unit,
    onTimeSignatureChange: (TimeSignature) -> Unit,
    onDurationChange: (CueDuration) -> Unit,
    onDurationTypeChange: (CueDuration.Type) -> Unit,
    onPatternChange: (NotePattern) -> Unit,
    dragHandleModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(8.dp)) {
        var expanded by rememberSaveable { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DragHandle(
                modifier = dragHandleModifier,
            )

            Text(
                text = stringResource(Res.string.cue_position, value.displayPosition),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.headlineMedium,
            )

            TextField(
                value = value.name,
                onValueChange = onNameChange,
                placeholder = {
                    Text(stringResource(Res.string.cue_name_hint))
                },
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = { expanded = !expanded }) {
                ExpandableChevron(isExpanded = expanded)
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CueDurationView(
                    value = value.duration,
                    onValueChange = onDurationChange,
                    onTypeChange = onDurationTypeChange,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TimeSignatureView(
                        value = value.timeSignature,
                        onValueChange = onTimeSignatureChange,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )

                    BpmInputField(
                        value = value.bpm,
                        onValueChange = onBpmChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically),
                        isError = EditCueState.Error.BPM in value.errors,
                    )
                }

                SubdivisionsChooser(
                    pattern = value.pattern,
                    timeSignature = value.timeSignature,
                    onSubdivisionChoose = onPatternChange,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CueView(
        value = EditCueState(
            name = "",
            displayPosition = "1",
            bpm = 999,
            timeSignature = TimeSignature(3, 4),
            activeDurationType = CueDuration.Type.TIME,
            beats = DefaultBeatsDuration,
            measures = DefaultMeasuresDuration,
            time = DefaultTimeDuration,
            pattern = NotePattern.STRAIGHT_X1,
            errors = setOf(EditCueState.Error.BPM),
        ),
        onNameChange = {},
        onBpmChange = {},
        onTimeSignatureChange = {},
        onDurationChange = {},
        onDurationTypeChange = {},
        onPatternChange = {},
    )
}
