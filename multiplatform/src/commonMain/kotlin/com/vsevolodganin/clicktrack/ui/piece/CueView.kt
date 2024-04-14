package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.edit.EditCueState
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.utils.compose.Preview
import com.vsevolodganin.clicktrack.utils.compose.SimpleSpacer
import dev.icerock.moko.resources.compose.stringResource

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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DragIndicator,
                contentDescription = null,
                modifier = dragHandleModifier
            )

            Text(
                text = stringResource(MR.strings.cue_position, value.displayPosition),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.h5
            )

            TextField(
                value = value.name,
                onValueChange = onNameChange,
                placeholder = {
                    Text(stringResource(MR.strings.cue_name_hint))
                },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { expanded = !expanded }) {
                ExpandableChevron(isExpanded = expanded)
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimpleSpacer(height = 8.dp)

                CueDurationView(
                    value = value.duration,
                    onValueChange = onDurationChange,
                    onTypeChange = onDurationTypeChange,
                )

                Row {
                    TimeSignatureView(
                        value = value.timeSignature,
                        onValueChange = onTimeSignatureChange,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    BpmInputField(
                        value = value.bpm,
                        onValueChange = onBpmChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically),
                        isError = EditCueState.Error.BPM in value.errors
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
            errors = setOf(EditCueState.Error.BPM)
        ),
        onNameChange = {},
        onBpmChange = {},
        onTimeSignatureChange = {},
        onDurationChange = {},
        onDurationTypeChange = {},
        onPatternChange = {},
    )
}
