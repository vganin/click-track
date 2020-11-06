package net.ganin.vsevolod.clicktrack.view.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.CoreTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focusObserver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import kotlin.time.Duration
import kotlin.time.minutes

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

        val commonCueDurationModifier = Modifier
            .align(Alignment.CenterVertically)
            .width(DURATION_FIELD_WIDTH)

        when (durationTypeState.value) {
            CueDurationType.BEATS -> {
                EditBeatsView(
                    state = beatsDurationState,
                    modifier = commonCueDurationModifier
                )
                state.value = beatsDurationState.value
            }
            CueDurationType.TIME -> {
                EditTimeView(
                    state = timeDurationState,
                    modifier = commonCueDurationModifier
                )
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

@Composable
private fun EditBeatsView(
    state: MutableState<CueDuration.Beats>,
    modifier: Modifier = Modifier
) {
    val beatsCountState: MutableState<Int> = remember { mutableStateOf(state.value.value) }

    var text by remember { mutableStateOf(beatsCountState.value.toString()) }
    var selection by remember { mutableStateOf(TextRange.Zero) }
    var composition by remember { mutableStateOf<TextRange?>(null) }

    fun TextRange.constrain(minimumValue: Int, maximumValue: Int): TextRange {
        val newStart = start.coerceIn(minimumValue, maximumValue)
        val newEnd = end.coerceIn(minimumValue, maximumValue)
        if (newStart != start || newEnd != end) {
            return TextRange(newStart, newEnd)
        }
        return this
    }

    val textFieldValue = TextFieldValue(
        text = text,
        selection = selection.constrain(0, text.length),
        composition = composition?.constrain(0, text.length)
    )

    var isFocused by remember { mutableStateOf(false) }

    CoreTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val newText = newValue.text
            val newInt = when {
                newText.isEmpty() -> 0
                newText.length in 1..6 -> newText.toIntOrNull()
                else -> null
            } ?: return@CoreTextField

            text = newText
            selection = newValue.selection
            composition = newValue.composition
            beatsCountState.value = newInt
        },
        cursorColor = AmbientContentColor.current,
        modifier = modifier
            .focusableBorder()
            .focusObserver {
                if (isFocused == it.isFocused) {
                    return@focusObserver
                }

                isFocused = it.isFocused

                if (isFocused) {
                    // FIXME: Need to have a better way to set selection on the next frame
                    // because onValueChange rewrites our effort
                    GlobalScope.launch(Dispatchers.Main) {
                        selection = TextRange(0, text.length)
                    }
                } else {
                    text = beatsCountState.value.toString()
                }
            }
            .padding(8.dp),
        keyboardType = KeyboardType.Number,
        textStyle = MaterialTheme.typography.subtitle1.copy(textAlign = TextAlign.Center),
        softWrap = false,
        maxLines = 1
    )

    state.value = CueDuration.Beats(beatsCountState.value)
}

@Composable
private fun EditTimeView(
    state: MutableState<CueDuration.Time>,
    modifier: Modifier = Modifier
) {
    val durationState: MutableState<Duration> = remember { mutableStateOf(state.value.value.value) }

    Box(modifier = modifier) {
        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
            DurationPicker(state = durationState)
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
            CueDurationType.TIME -> R.string.cue_duration_time
        }
    }

private val CueDuration.type: CueDurationType
    get() {
        return when (this) {
            is CueDuration.Beats -> CueDurationType.BEATS
            is CueDuration.Time -> CueDurationType.TIME
        }
    }

private val DROPDOWN_TOGGLE_WIDTH = 110.dp
private val DURATION_FIELD_WIDTH = 140.dp

@Preview
@Composable
fun PreviewEditCueDurationView() {
    Column(modifier = Modifier.fillMaxSize()) {
        EditCueDurationView(mutableStateOf(CueDuration.Beats(999999)))
        EditCueDurationView(mutableStateOf(CueDuration.Time(SerializableDuration(1.minutes))))
    }
}
