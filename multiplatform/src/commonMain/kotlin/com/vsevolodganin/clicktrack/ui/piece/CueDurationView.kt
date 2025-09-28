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
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.model.CueDuration
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.minutes

@Composable
fun CueDurationView(
    value: CueDuration,
    onValueChange: (CueDuration) -> Unit,
    onTypeChange: (CueDuration.Type) -> Unit,
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
                        modifier = commonCueDurationModifier,
                    )
                }

                is CueDuration.Measures -> {
                    EditMeasuresView(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = commonCueDurationModifier,
                    )
                }

                is CueDuration.Time -> {
                    EditTimeView(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = commonCueDurationModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun DurationTypeDropdown(value: CueDuration.Type, onValueChange: (CueDuration.Type) -> Unit) {
    DropdownSelector(
        items = CueDuration.Type.entries,
        selectedValue = value,
        onSelect = { selectedValue ->
            onValueChange(selectedValue)
        },
        toString = { it.stringResource() },
        modifier = Modifier.width(140.dp),
    )
}

@Composable
private fun EditBeatsView(value: CueDuration.Beats, onValueChange: (CueDuration) -> Unit, modifier: Modifier = Modifier) {
    NumberInputField(
        value = value.value,
        onValueChange = { onValueChange(CueDuration.Beats(it)) },
        modifier = modifier,
    )
}

@Composable
private fun EditMeasuresView(value: CueDuration.Measures, onValueChange: (CueDuration) -> Unit, modifier: Modifier = Modifier) {
    NumberInputField(
        value = value.value,
        onValueChange = { onValueChange(CueDuration.Measures(it)) },
        modifier = modifier,
    )
}

@Composable
private fun EditTimeView(value: CueDuration.Time, onValueChange: (CueDuration) -> Unit, modifier: Modifier = Modifier) {
    DurationPicker(
        value = value.value,
        onValueChange = { onValueChange(CueDuration.Time(it)) },
        modifier = modifier,
    )
}

@Composable
private fun CueDuration.Type.stringResource(): String {
    return when (this) {
        CueDuration.Type.BEATS -> Res.string.cue_duration_beats
        CueDuration.Type.MEASURES -> Res.string.cue_duration_measures
        CueDuration.Type.TIME -> Res.string.cue_duration_time
    }.let { stringResource(it) }
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
