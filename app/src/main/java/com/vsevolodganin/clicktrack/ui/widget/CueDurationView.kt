package com.vsevolodganin.clicktrack.ui.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.state.redux.EditCueState
import kotlin.time.Duration

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

@Composable
private fun DurationTypeDropdown(
    value: EditCueState.DurationType,
    onValueChange: (EditCueState.DurationType) -> Unit,
) {
    val toggleState = remember { mutableStateOf(false) }
    val onToggleClick = { toggleState.value = !toggleState.value }

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(
                onClick = onToggleClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
            )
    ) {
        Text(
            text = stringResource(id = value.displayStringResId),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(100.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(16.dp, 16.dp)
                .clickable(
                    onClick = onToggleClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                )
        ) {
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }

    DropdownMenu(
        expanded = toggleState.value,
        onDismissRequest = { toggleState.value = false },
        content = {
            EditCueState.DurationType.values().forEach { durationType ->
                DropdownMenuItem(onClick = {
                    onValueChange(durationType)
                    toggleState.value = false
                }) {
                    Text(text = stringResource(id = durationType.displayStringResId))
                }
            }
        }
    )
}

@Composable
private fun EditBeatsView(
    value: CueDuration.Beats,
    onValueChange: (CueDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        NumberInputField(
            value = value.value,
            onValueChange = { onValueChange(CueDuration.Beats(it)) },
            modifier = modifier
        )
    }
}

@Composable
private fun EditMeasuresView(
    value: CueDuration.Measures,
    onValueChange: (CueDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        NumberInputField(
            value = value.value,
            onValueChange = { onValueChange(CueDuration.Measures(it)) },
            modifier = modifier
        )
    }
}

@Composable
private fun EditTimeView(
    value: CueDuration.Time,
    onValueChange: (CueDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        DurationPicker(
            value = value.value,
            onValueChange = { onValueChange(CueDuration.Time(it)) },
            modifier = modifier
        )
    }
}

@get:StringRes
private val EditCueState.DurationType.displayStringResId: Int
    get() {
        return when (this) {
            EditCueState.DurationType.BEATS -> R.string.cue_duration_beats
            EditCueState.DurationType.MEASURES -> R.string.cue_duration_measures
            EditCueState.DurationType.TIME -> R.string.cue_duration_time
        }
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
    Column {
        CueDurationView(CueDuration.Beats(999999), {}, {})
        CueDurationView(CueDuration.Measures(999999), {}, {})
        CueDurationView(CueDuration.Time(Duration.minutes(1)), {}, {})
    }
}
