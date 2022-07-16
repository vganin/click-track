package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.redux.EditCueState
import kotlin.time.Duration.Companion.minutes

@Composable
fun CueDurationView(
    value: CueDuration,
    onValueChange: (CueDuration) -> Unit,
    onTypeChange: (EditCueState.DurationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        DurationTypeDropdown(
            value = value.type,
            onValueChange = onTypeChange,
        )

        Spacer(modifier = Modifier.width(8.dp))

        val commonCueDurationModifier = Modifier
            .align(Alignment.CenterVertically)
            .fillMaxWidth()

        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
            when (value) {
                is CueDuration.Beats -> {
                    EditBeatsView(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = commonCueDurationModifier
                    )
                }
                is CueDuration.Measures -> {
                    EditMeasuresView(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = commonCueDurationModifier
                    )
                }
                is CueDuration.Time -> {
                    EditTimeView(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = commonCueDurationModifier
                    )
                }
            }
        }
    }
}

@Composable
private fun DurationTypeDropdown(
    value: EditCueState.DurationType,
    onValueChange: (EditCueState.DurationType) -> Unit,
) {
    DropdownSelector(
        items = EditCueState.DurationType.values().toList(),
        selectedValue = value,
        onSelect = { selectedValue ->
            onValueChange(selectedValue)
        },
        toString = { it.stringResource() },
        modifier = Modifier.width(140.dp)
    )
}

@Composable
private fun EditBeatsView(
    value: CueDuration.Beats,
    onValueChange: (CueDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberInputField(
        value = value.value,
        onValueChange = { onValueChange(CueDuration.Beats(it)) },
        modifier = modifier
    )
}

@Composable
private fun EditMeasuresView(
    value: CueDuration.Measures,
    onValueChange: (CueDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberInputField(
        value = value.value,
        onValueChange = { onValueChange(CueDuration.Measures(it)) },
        modifier = modifier
    )
}

@Composable
private fun EditTimeView(
    value: CueDuration.Time,
    onValueChange: (CueDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    DurationPicker(
        value = value.value,
        onValueChange = { onValueChange(CueDuration.Time(it)) },
        modifier = modifier
    )
}

@Composable
private fun EditCueState.DurationType.stringResource(): String {
    return when (this) {
        EditCueState.DurationType.BEATS -> R.string.cue_duration_beats
        EditCueState.DurationType.MEASURES -> R.string.cue_duration_measures
        EditCueState.DurationType.TIME -> R.string.cue_duration_time
    }.let { stringResource(it) }
}

private val CueDuration.type: EditCueState.DurationType
    get() {
        return when (this) {
            is CueDuration.Beats -> EditCueState.DurationType.BEATS
            is CueDuration.Measures -> EditCueState.DurationType.MEASURES
            is CueDuration.Time -> EditCueState.DurationType.TIME
        }
    }

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        Column {
            CueDurationView(CueDuration.Beats(999999), {}, {})
            CueDurationView(CueDuration.Measures(999999), {}, {})
            CueDurationView(CueDuration.Time(1.minutes), {}, {})
        }
    }
}
