package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import java.util.Locale
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
    DropdownMenu(
        toggle = {
            Row(modifier = modifier.clickable(onClick = {
                toggleState.value = !toggleState.value
            })) {
                Arrow(direction = ArrowDirection.DOWN)
                Text(text = state.value.displayText.capitalize(Locale.US))
            }
        },
        expanded = toggleState.value,
        onDismissRequest = { toggleState.value = false },
    ) {
        CueDurationType.values().forEach { durationType ->
            val selectedState = remember { mutableStateOf(state.value == durationType) }
            Text(
                text = "As ${durationType.displayText}",
                modifier = Modifier.selectable(
                    selected = selectedState.value,
                    onClick = {
                        state.value = durationType
                        toggleState.value = false
                    }
                )
            )
        }
    }
}

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
            text = "${beatsCountState.value} beats",
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
            text = "${durationState.value.inMinutes} minutes",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Button(onClick = { durationState.value += 10.seconds }) {
            Arrow(ArrowDirection.RIGHT)
        }
    }

    state.value = CueDuration.Time(SerializableDuration(durationState.value))
}

private enum class CueDurationType(val displayText: String) {
    BEATS("beats"),
    TIME("time"),
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
