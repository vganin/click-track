package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.BackspaceCommand
import androidx.compose.ui.text.input.CommitTextCommand
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.view.utils.focusableBorder
import java.text.DecimalFormat
import kotlin.time.Duration
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlin.time.seconds

@Composable
fun DurationPicker(
    state: MutableState<Duration>,
    modifier: Modifier = Modifier,
) {
    /** Converts CharSequence in format "hhmmss" to Duration */
    fun CharSequence.toDuration(): Duration {
        val hoursSequence = subSequence(0, 2)
        val minutesSequence = subSequence(2, 4)
        val secondsSequence = subSequence(4, 6)
        val sequenceToTimeMultiplier = listOf(
            hoursSequence to SECONDS_PER_MINUTE * MINUTES_PER_HOUR,
            minutesSequence to SECONDS_PER_MINUTE,
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

    val inputService = LocalTextInputService.current!!
    var textInputSession: TextInputSession? by remember { mutableStateOf(null) }
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

    Row(
        modifier = modifier
            .focusableBorder()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (isFocused == focusState.isFocused) {
                    return@onFocusChanged
                }

                isFocused = focusState.isFocused

                if (isFocused && textInputSession == null) {
                    textInputSession = inputService.startInput(
                        value = TextFieldValue(),
                        imeOptions = ImeOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        onEditCommand = { operations ->
                            operations.forEach { operation ->
                                when (operation) {
                                    is BackspaceCommand -> removeDigit()
                                    is CommitTextCommand -> operation.text.forEach(::enterDigit)
                                }
                            }
                        },
                        onImeActionPerformed = { action ->
                            if (action == ImeAction.Done) {
                                focusRequester.freeFocus()
                            }
                        }
                    )
                } else if (!isFocused && textInputSession != null) {
                    textInputSession?.let(inputService::stopInput)
                    textInputSession = null
                }
            }
            .focusModifier()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusRequester.requestFocus()
                }
            }
            .padding(8.dp)
    ) {
        val baseModifier = Modifier.align(Alignment.CenterVertically)

        Text(
            text = formatInternalState(),
            modifier = baseModifier.weight(1.0f),
            style = TextStyle(textAlign = TextAlign.Center)
        )

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = baseModifier
                .size(16.dp, 16.dp)
                .clickable(
                    onClick = { state.value = Duration.ZERO },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false)
                )
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }
    }
}

private val twoDigitsFormat = DecimalFormat("00")
private fun Int.twoDigits() = twoDigitsFormat.format(this)

@Preview
@Composable
fun PreviewDurationPicker() {
    val sharedState = remember { mutableStateOf(1.hours + 2.minutes + 3.seconds + 4.milliseconds) }
    Column {
        DurationPicker(sharedState)
        DurationPicker(sharedState)
        DurationPicker(mutableStateOf(1.minutes + 2.seconds + 3.milliseconds))
        DurationPicker(mutableStateOf(1.seconds + 2.milliseconds))
        DurationPicker(mutableStateOf(1.milliseconds))
    }
}

private const val SECONDS_PER_MINUTE = 60L
private const val MINUTES_PER_HOUR = 60L
