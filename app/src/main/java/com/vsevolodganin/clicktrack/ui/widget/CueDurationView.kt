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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.utils.compose.observableMutableStateOf
import kotlin.time.Duration

@Composable
fun CueDurationView(
    state: MutableState<CueDuration>,
    modifier: Modifier = Modifier,
    defaultBeatsDuration: () -> CueDuration.Beats = { CueDuration.Beats(4) },
    defaultMeasuresDuration: () -> CueDuration.Measures = { CueDuration.Measures(1) },
    defaultTimeDuration: () -> CueDuration.Time = { CueDuration.Time(Duration.minutes(1)) },
) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        val onDurationChange: (CueDuration) -> Unit = { state.value = it }

        val beatsDurationState = remember(CueDurationType.BEATS) {
            state.value.let {
                if (it is CueDuration.Beats) {
                    observableMutableStateOf(it)
                } else {
                    observableMutableStateOf(defaultBeatsDuration())
                }
            }.observe(onDurationChange)
        }
        val measuresDurationState = remember(CueDurationType.MEASURES) {
            state.value.let {
                if (it is CueDuration.Measures) {
                    observableMutableStateOf(it)
                } else {
                    observableMutableStateOf(defaultMeasuresDuration())
                }
            }.observe(onDurationChange)
        }
        val timeDurationState = remember(CueDurationType.TIME) {
            state.value.let {
                if (it is CueDuration.Time) {
                    observableMutableStateOf(it)
                } else {
                    observableMutableStateOf(defaultTimeDuration())
                }
            }.observe(onDurationChange)
        }

        val durationTypeState = remember {
            observableMutableStateOf(state.value.type).observe {
                state.value = when (it) {
                    CueDurationType.BEATS -> beatsDurationState.value
                    CueDurationType.MEASURES -> measuresDurationState.value
                    CueDurationType.TIME -> timeDurationState.value
                }
            }
        }

        DurationTypeDropdown(state = durationTypeState)

        Spacer(modifier = Modifier.width(8.dp))

        val commonCueDurationModifier = Modifier
            .align(Alignment.CenterVertically)
            .fillMaxWidth()

        when (durationTypeState.value) {
            CueDurationType.BEATS -> {
                EditBeatsView(
                    state = beatsDurationState,
                    modifier = commonCueDurationModifier
                )
            }
            CueDurationType.MEASURES -> {
                EditMeasuresView(
                    state = measuresDurationState,
                    modifier = commonCueDurationModifier
                )
            }
            CueDurationType.TIME -> {
                EditTimeView(
                    state = timeDurationState,
                    modifier = commonCueDurationModifier
                )
            }
        }
    }
}

@Composable
private fun DurationTypeDropdown(state: MutableState<CueDurationType>) {
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
            text = stringResource(id = state.value.displayStringResId),
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
            CueDurationType.values().forEach { durationType ->
                DropdownMenuItem(onClick = {
                    state.value = durationType
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
    state: MutableState<CueDuration.Beats>,
    modifier: Modifier = Modifier,
) {
    val beatsNumberState: MutableState<Int> = remember {
        observableMutableStateOf(state.value.value).observe {
            state.value = CueDuration.Beats(it)
        }
    }

    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        NumberInputField(beatsNumberState, modifier)
    }
}

@Composable
private fun EditMeasuresView(
    state: MutableState<CueDuration.Measures>,
    modifier: Modifier = Modifier,
) {
    val beatsNumberState: MutableState<Int> = remember {
        observableMutableStateOf(state.value.value).observe {
            state.value = CueDuration.Measures(it)
        }
    }

    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        NumberInputField(beatsNumberState, modifier)
    }
}

@Composable
private fun EditTimeView(
    state: MutableState<CueDuration.Time>,
    modifier: Modifier = Modifier,
) {
    val durationState: MutableState<Duration> = remember {
        observableMutableStateOf(state.value.value).observe {
            state.value = CueDuration.Time(it)
        }
    }

    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        DurationPicker(durationState, modifier)
    }
}

private enum class CueDurationType {
    BEATS,
    MEASURES,
    TIME,
}

@get:StringRes
private val CueDurationType.displayStringResId: Int
    get() {
        return when (this) {
            CueDurationType.BEATS -> R.string.cue_duration_beats
            CueDurationType.MEASURES -> R.string.cue_duration_measures
            CueDurationType.TIME -> R.string.cue_duration_time
        }
    }

private val CueDuration.type: CueDurationType
    get() {
        return when (this) {
            is CueDuration.Beats -> CueDurationType.BEATS
            is CueDuration.Measures -> CueDurationType.MEASURES
            is CueDuration.Time -> CueDurationType.TIME
        }
    }

@Preview
@Composable
private fun Preview() {
    Column {
        CueDurationView(state = remember { mutableStateOf(CueDuration.Beats(999999)) })
        CueDurationView(state = remember { mutableStateOf(CueDuration.Measures(999999)) })
        CueDurationView(state = remember { mutableStateOf(CueDuration.Time(Duration.minutes(1))) })
    }
}