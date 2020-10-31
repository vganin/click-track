package net.ganin.vsevolod.clicktrack.view.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import kotlin.time.Duration
import kotlin.time.minutes
import kotlin.time.seconds

@Composable
fun EditCueDurationView(
    state: MutableState<CueDuration>,
    defaultBeatsDuration: () -> CueDuration.Beats = { CueDuration.Beats(4) },
    defaultTimeDuration: () -> CueDuration.Time = { CueDuration.Time(SerializableDuration(1.minutes)) },
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        val durationTypeState = remember { mutableStateOf(state.value.type) }

        DurationTypeDropdown(
            state = durationTypeState,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        val beatsDurationState = remember(CueDurationType.BEATS) {
            state.value.let {
                if (it is CueDuration.Beats) {
                    mutableStateOf(it)
                } else {
                    mutableStateOf(defaultBeatsDuration())
                }
            }
        }
        val timeDurationState = remember(CueDurationType.TIME) {
            state.value.let {
                if (it is CueDuration.Time) {
                    mutableStateOf(it)
                } else {
                    mutableStateOf(defaultTimeDuration())
                }
            }
        }

        when (durationTypeState.value) {
            CueDurationType.BEATS -> {
                EditBeatsView(beatsDurationState)
                state.value = beatsDurationState.value
            }
            CueDurationType.TIME -> {
                EditTimeView(timeDurationState)
                state.value = timeDurationState.value
            }
        }
    }
}

@Composable
private fun DurationTypeDropdown(
    state: MutableState<CueDurationType>,
    modifier: Modifier,
) {
    val toggleState = remember { mutableStateOf(false) }
    val onToggleClick = { toggleState.value = !toggleState.value }

    DropdownMenu(
        toggleModifier = modifier
            .clickable(
                onClick = onToggleClick,
                indication = RippleIndication()
            )
            .width(DROPDOWN_TOGGLE_WIDTH),
        toggle = {
            Row(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(id = state.value.displayStringResId),
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
                IconButton(onClick = onToggleClick) {
                    Icon(Icons.Default.ArrowDropDown)
                }
            }
        },
        expanded = toggleState.value,
        onDismissRequest = { toggleState.value = false },
        dropdownModifier = Modifier.width(DROPDOWN_TOGGLE_WIDTH),
        dropdownOffset = Position(-DROPDOWN_TOGGLE_WIDTH, 0.dp),
        dropdownContent = {
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

private val DROPDOWN_TOGGLE_WIDTH = 140.dp

@Composable
private fun EditBeatsView(
    state: MutableState<CueDuration.Beats>,
    modifier: Modifier = Modifier
) {
    val beatsCountState: MutableState<Int> = remember { mutableStateOf(state.value.value) }

    Row(modifier = modifier) {
        Button(onClick = { beatsCountState.value -= 1 }) {
            Arrow(ArrowDirection.LEFT)
        }
        Text(
            text = beatsCountState.value.toString(),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Button(onClick = { beatsCountState.value += 1 }) {
            Arrow(ArrowDirection.RIGHT)
        }
    }

    state.value = CueDuration.Beats(beatsCountState.value)
}

@Composable
private fun EditTimeView(
    state: MutableState<CueDuration.Time>,
    modifier: Modifier = Modifier
) {
    val durationState: MutableState<Duration> = remember { mutableStateOf(state.value.value.value) }

    Row(modifier = modifier) {
        Button(onClick = { durationState.value -= 10.seconds }) {
            Arrow(ArrowDirection.LEFT)
        }
        Text(
            text = durationState.value.inMinutes.toString(),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Button(onClick = { durationState.value += 10.seconds }) {
            Arrow(ArrowDirection.RIGHT)
        }
    }

    state.value = CueDuration.Time(SerializableDuration(durationState.value))
}

private enum class CueDurationType {
    BEATS,
    TIME,
}

@get:StringRes
private val CueDurationType.displayStringResId: Int
    get() {
        return when (this) {
            CueDurationType.BEATS -> R.string.cue_duration_beats
            CueDurationType.TIME -> R.string.cue_duration_minutes
        }
    }

private val CueDuration.type: CueDurationType
    get() {
        return when (this) {
            is CueDuration.Beats -> CueDurationType.BEATS
            is CueDuration.Time -> CueDurationType.TIME
        }
    }

@Preview
@Composable
fun PreviewEditCueDurationView() {
    Column(modifier = Modifier.fillMaxSize()) {
        EditCueDurationView(mutableStateOf(CueDuration.Beats(4)))
        EditCueDurationView(mutableStateOf(CueDuration.Time(SerializableDuration(1.minutes))))
    }
}
