package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
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
    fun CharSequence.safeSubSequence(startIndex: Int, endIndex: Int): CharSequence {
        val range = 0..length
        return subSequence(startIndex.coerceIn(range), endIndex.coerceIn(range))
    }

    /** Converts CharSequence in format "ssmmhh*" to Duration */
    fun CharSequence.toDuration(): Duration {
        val secondsSequence = safeSubSequence(0, 2)
        val minutesSequence = safeSubSequence(2, 4)
        val hoursSequence = safeSubSequence(4, length)
        val sequenceToTimeMultiplier = listOf(
            secondsSequence to 1,
            minutesSequence to 60,
            hoursSequence to 60 * 60,
        )

        val radix = 10
        var seconds = 0
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

    /** Converts Duration to String in format "ssmmhh*" */
    fun Duration.asString(): String {
        val twoDigitsFormat = DecimalFormat("00")
        fun Int.twoDigits() = twoDigitsFormat.format(this)
        return toComponents { hours, minutes, seconds, _ ->
            when {
                hours > 0 -> "${seconds.twoDigits()}${minutes.twoDigits()}${hours}"
                minutes > 0 -> "${seconds.twoDigits()}${minutes}"
                else -> "$seconds"
            }
        }
    }

    fun String.hasNonDigitChars(): Boolean {
        return any { !it.isDigit() }
    }

    var text by remember { mutableStateOf(state.value.asString()) }
    var selection by remember { mutableStateOf(TextRange.Zero) }

    val secondsString = stringResource(R.string.duration_picker_seconds)
    val minutesString = stringResource(R.string.duration_picker_minutes)
    val hoursString = stringResource(R.string.duration_picker_hours)

    text = when (text) {
        "", "0" -> "0"
        else -> text.trimStart('0')
    }
    selection = when (text) {
        "", "0" -> TextRange(1)
        else -> selection.constrain(0, text.length)
    }

    TextField(
        value = TextFieldValue(
            text = text,
            selection = selection
        ),
        modifier = Modifier
            .width(with(DensityAmbient.current) { 128.sp.toDp() }),
        onValueChange = { newValue ->
            val newText = newValue.text
            val newSelection = newValue.selection
            if (!newText.hasNonDigitChars() && newText.length < 7) {
                state.value = newText.toDuration()
                text = newText
                selection = newSelection
            }
        },
        keyboardType = KeyboardType.Number,
        visualTransformation = object : VisualTransformation {
            override fun filter(text: AnnotatedString): TransformedText {
                @Suppress("NAME_SHADOWING")
                val text = text.text

                val originalToTransformed = mutableListOf<Int>()
                val transformedToOriginal = mutableListOf<Int>()

                val transformedText = StringBuilder().apply {
                    var originalTextIndex = 0
                    var transformedTextIndex = 0

                    fun appendCharBindingToIndex(char: Char, originalTextIndex: Int) {
                        transformedToOriginal += originalTextIndex
                        append(char)
                        transformedTextIndex = length
                    }

                    fun appendStringBindingToIndex(string: String, originalTextIndex: Int) {
                        repeat(string.length) {
                            transformedToOriginal += originalTextIndex
                        }
                        append(string)
                        transformedTextIndex = length
                    }

                    fun appendNextOriginalChar(transformedTextIndex: Int) {
                        if (originalTextIndex < text.length) {
                            val char = text[originalTextIndex]
                            originalToTransformed += transformedTextIndex
                            appendCharBindingToIndex(char, originalTextIndex)
                            ++originalTextIndex
                        }
                    }

                    appendNextOriginalChar(transformedTextIndex)
                    appendNextOriginalChar(transformedTextIndex)
                    var transformedTextAfterSectionIndex = transformedTextIndex
                    appendCharBindingToIndex(' ', originalTextIndex)
                    appendStringBindingToIndex(secondsString, originalTextIndex)
                    if (text.length > 2) {
                        appendCharBindingToIndex(' ', originalTextIndex)
                        appendNextOriginalChar(transformedTextAfterSectionIndex)
                        appendNextOriginalChar(transformedTextIndex)
                        transformedTextAfterSectionIndex = transformedTextIndex
                        appendCharBindingToIndex(' ', originalTextIndex)
                        appendStringBindingToIndex(minutesString, originalTextIndex)
                    }
                    if (text.length > 4) {
                        appendCharBindingToIndex(' ', originalTextIndex)
                        appendNextOriginalChar(transformedTextAfterSectionIndex)
                        appendNextOriginalChar(transformedTextIndex)
                        transformedTextAfterSectionIndex = transformedTextIndex
                        appendCharBindingToIndex(' ', originalTextIndex)
                        appendStringBindingToIndex(hoursString, originalTextIndex)
                    }

                    originalToTransformed += transformedTextAfterSectionIndex
                    transformedToOriginal += originalTextIndex
                }.toString()

                return TransformedText(
                    transformedText = AnnotatedString(transformedText),
                    offsetMap = object : OffsetMap {
                        override fun originalToTransformed(offset: Int): Int {
                            return originalToTransformed[offset.coerceIn(originalToTransformed.indices)]
                        }

                        override fun transformedToOriginal(offset: Int): Int {
                            return transformedToOriginal[offset.coerceIn(transformedToOriginal.indices)]
                        }
                    }
                )
            }
        }
    )
}

private fun TextRange.constrain(minimumValue: Int, maximumValue: Int): TextRange {
    val newStart = start.coerceIn(minimumValue, maximumValue)
    val newEnd = end.coerceIn(minimumValue, maximumValue)
    if (newStart != start || newEnd != end) {
        return TextRange(newStart, newEnd)
    }
    return this
}

@Preview
@Composable
fun PreviewTimePicker() {
    Column {
        DurationPicker(mutableStateOf(1.hours + 2.minutes + 3.seconds + 4.milliseconds))
        DurationPicker(mutableStateOf(1.minutes + 2.seconds + 3.milliseconds))
        DurationPicker(mutableStateOf(1.seconds + 2.milliseconds))
        DurationPicker(mutableStateOf(1.milliseconds))
    }
}
