package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focusObserver
import androidx.compose.ui.focusRequester
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.platform.TextInputServiceAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.BackspaceKeyEditOp
import androidx.compose.ui.text.input.CommitTextEditOp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.InputSessionToken
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.MinutesPerHour
import androidx.compose.ui.unit.SecondsPerMinute
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.R
import java.text.DecimalFormat
import kotlin.time.Duration
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlin.time.seconds

@Composable
fun DurationPicker(
    state: MutableState<Duration>,
) {
    /** Converts CharSequence in format "hhmmss" to Duration */
    fun CharSequence.toDuration(): Duration {
        val hoursSequence = subSequence(0, 2)
        val minutesSequence = subSequence(2, 4)
        val secondsSequence = subSequence(4, 6)
        val sequenceToTimeMultiplier = listOf(
            hoursSequence to SecondsPerMinute * MinutesPerHour,
            minutesSequence to SecondsPerMinute,
            secondsSequence to 1L,
        )

        val radix = 10
        var seconds = 0L
        for ((sequence, timeMultiplier) in sequenceToTimeMultiplier) {
            var baseMultiplier = 1
            for (char in sequence.reversed()) {
                val int = char.toInt() - '0'.toInt()
                seconds += int * baseMultiplier * timeMultiplier
                baseMultiplier *= radix
            }
        }

        return seconds.seconds
    }

    /** Converts Duration to String in format "hhmmss" */
    fun Duration.asString(): String {
        return toComponents { hours, minutes, seconds, _ ->
            "${hours.coerceAtMost(99).twoDigits()}${minutes.twoDigits()}${seconds.twoDigits()}"
        }
    }

    val internalStringState = remember { mutableStateOf(state.value.asString()) }

    if (internalStringState.value.toDuration() != state.value) {
        internalStringState.value = state.value.asString()
    }

    fun updateInternalStringState(newInternalStringState: String) {
        internalStringState.value = newInternalStringState
        state.value = newInternalStringState.toDuration()
    }

    fun enterDigit(char: Char) {
        if (char.isDigit() && internalStringState.value.first() == '0') {
            updateInternalStringState(internalStringState.value.drop(1) + char)
        }
    }

    fun removeDigit() {
        updateInternalStringState('0' + internalStringState.value.dropLast(1))
    }

    val inputService = TextInputServiceAmbient.current!!
    var inputSessionToken: InputSessionToken? by remember { mutableStateOf(null) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()

    @Composable
    fun formatInternalState(): String {
        val hoursString = stringResource(R.string.duration_picker_hours)
        val minutesString = stringResource(R.string.duration_picker_minutes)
        val secondsString = stringResource(R.string.duration_picker_seconds)

        return StringBuilder().apply {
            append(internalStringState.value.subSequence(0, 2))
            append(hoursString)
            append(' ')
            append(internalStringState.value.subSequence(2, 4))
            append(minutesString)
            append(' ')
            append(internalStringState.value.subSequence(4, 6))
            append(secondsString)
        }.toString()
    }

    val activeColor = MaterialTheme.colors.primary
    val inactiveColor = MaterialTheme.colors.onSurface
    val borderColor = if (isFocused) activeColor else inactiveColor

    Row(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusObserver { focusState ->
                if (isFocused == focusState.isFocused) {
                    return@focusObserver
                }

                isFocused = focusState.isFocused

                if (isFocused && inputSessionToken == null) {
                    inputSessionToken = inputService.startInput(
                        value = TextFieldValue(),
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        keyboardOptions = KeyboardOptions(),
                        onEditCommand = { operations ->
                            operations.forEach { operation ->
                                when (operation) {
                                    is BackspaceKeyEditOp -> removeDigit()
                                    is CommitTextEditOp -> operation.text.forEach(::enterDigit)
                                }
                            }
                        },
                        onImeActionPerformed = { action ->
                            if (action == ImeAction.Done) {
                                focusRequester.freeFocus()
                            }
                        }
                    )
                } else if (!isFocused && inputSessionToken != null) {
                    inputSessionToken?.let(inputService::stopInput)
                    inputSessionToken = null
                }
            }
            .focus()
            .tapGestureFilter { focusRequester.requestFocus() }
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = MaterialTheme.shapes.small,
            )
            .padding(8.dp)
    ) {
        val baseModifier = Modifier.align(Alignment.CenterVertically)

        Text(
            text = formatInternalState(),
            modifier = baseModifier,
        )

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = baseModifier
                .size(16.dp, 16.dp)
                .clickable(
                    onClick = { state.value = Duration.ZERO },
                    indication = RippleIndication(bounded = false)
                )
        ) {
            Icon(asset = Icons.Default.Close)
        }
    }
}

private val twoDigitsFormat = DecimalFormat("00")
private fun Int.twoDigits() = twoDigitsFormat.format(this)

@Preview
@Composable
fun PreviewTimePicker() {
    val sharedState = remember { mutableStateOf(1.hours + 2.minutes + 3.seconds + 4.milliseconds) }
    Column {
        DurationPicker(sharedState)
        DurationPicker(sharedState)
        DurationPicker(mutableStateOf(1.minutes + 2.seconds + 3.milliseconds))
        DurationPicker(mutableStateOf(1.seconds + 2.milliseconds))
        DurationPicker(mutableStateOf(1.milliseconds))
    }
}
