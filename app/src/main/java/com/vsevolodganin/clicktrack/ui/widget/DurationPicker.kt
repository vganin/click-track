package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.key.Key.Companion.Backspace
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
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
import com.vsevolodganin.clicktrack.ui.utils.focusableBorder
import java.text.DecimalFormat
import kotlin.time.Duration

@Composable
fun DurationPicker(
    state: MutableState<Duration>,
    modifier: Modifier = Modifier,
) {
    DurationPicker(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
    )
}

@Composable
fun DurationPicker(
    value: Duration,
    onValueChange: (Duration) -> Unit,
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
                val int = char.code - '0'.code
                seconds += int * baseMultiplier * timeMultiplier
                baseMultiplier *= radix
            }
        }

        return Duration.seconds(seconds)
    }

    /** Converts Duration to String in format "hhmmss" */
    fun Duration.asString(): String {
        return toComponents { hours, minutes, seconds, _ ->
            "${hours.coerceAtMost(99).twoDigits()}${minutes.twoDigits()}${seconds.twoDigits()}"
        }
    }

    val internalStringState = remember { mutableStateOf(value.asString()) }

    if (internalStringState.value.toDuration() != value) {
        internalStringState.value = value.asString()
    }

    fun updateInternalStringState(newInternalStringState: String) {
        internalStringState.value = newInternalStringState
        onValueChange(newInternalStringState.toDuration())
    }

    fun enterDigit(char: Char): Boolean {
        return if (char.isDigit() && internalStringState.value.first() == '0') {
            updateInternalStringState(internalStringState.value.drop(1) + char)
            true
        } else {
            false
        }
    }

    fun removeDigit(): Boolean {
        updateInternalStringState('0' + internalStringState.value.dropLast(1))
        return true
    }

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

    val inputService = LocalTextInputService.current!!
    val textInputSession: MutableState<TextInputSession?> = remember { mutableStateOf(null) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    var focusRect: Rect? by remember { mutableStateOf(null) }

    if (isFocused && textInputSession.value == null) {
        textInputSession.value = inputService.startInput(
            value = TextFieldValue(),
            imeOptions = ImeOptions(
                singleLine = true,
                autoCorrect = false,
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
    } else if (!isFocused && textInputSession.value != null) {
        textInputSession.value?.let(inputService::stopInput)
        textInputSession.value = null
    }

    DisposableEffect(focusRect, textInputSession.value) {
        val focusRectValue = focusRect
        val textInputSessionValue = textInputSession.value
        if (focusRectValue != null && textInputSessionValue != null) {
            textInputSessionValue.notifyFocusedRect(focusRectValue)
        }
        onDispose {}
    }

    Row(
        modifier = modifier
            .focusableBorder()
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .onKeyEvent {
                // FIXME(https://issuetracker.google.com/issues/188119984): Should keep only onEditCommand
                if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
                if (it.key == Backspace) {
                    removeDigit()
                } else {
                    enterDigit(it.utf16CodePoint.toChar())
                }
            }
            .clickable { focusRequester.requestFocus() }
            .onGloballyPositioned { layoutCoordinates -> focusRect = layoutCoordinates.boundsInWindow() }
            .padding(8.dp)
    ) {
        Text(
            text = formatInternalState(),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1.0f),
            style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        )
        Spacer(Modifier.width(8.dp))
        CloseIcon {
            onValueChange(Duration.ZERO)
        }
    }
}

@Composable
private fun RowScope.CloseIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .size(16.dp, 16.dp)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false)
            )
    ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
    }
}

private val TWO_DIGITS_FORMAT = DecimalFormat("00")
private fun Int.twoDigits() = TWO_DIGITS_FORMAT.format(this)

private const val SECONDS_PER_MINUTE = 60L
private const val MINUTES_PER_HOUR = 60L

@Preview
@Composable
private fun Preview() {
    val sharedState = remember { mutableStateOf(Duration.hours(1) + Duration.minutes(2) + Duration.seconds(3) + Duration.milliseconds(4)) }
    Column {
        DurationPicker(sharedState)
        DurationPicker(sharedState)
        DurationPicker(remember { mutableStateOf(Duration.minutes(1) + Duration.seconds(2) + Duration.milliseconds(3)) })
        DurationPicker(remember { mutableStateOf(Duration.seconds(1) + Duration.milliseconds(2)) })
        DurationPicker(remember { mutableStateOf(Duration.milliseconds(1)) })
    }
}
