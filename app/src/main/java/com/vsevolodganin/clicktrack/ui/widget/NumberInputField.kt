package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.ui.utils.focusableBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun NumberInputField(
    state: MutableState<Int>,
    modifier: Modifier = Modifier,
    maxDigitsCount: Int = DEFAULT_MAX_DIGITS,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    NumberInputField(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        maxDigitsCount = maxDigitsCount,
        visualTransformation = visualTransformation,
    )
}

@Composable
fun NumberInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxDigitsCount: Int = DEFAULT_MAX_DIGITS,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val maxValue = 10.pow(maxDigitsCount) - 1
    val valuesCoerced = if (value > maxValue) {
        onValueChange(maxValue)
        maxValue
    } else {
        value
    }

    var text by remember { mutableStateOf(valuesCoerced.toString()) } // Should not updated by changes
    var selection by remember { mutableStateOf(TextRange.Zero) }

    text = valuesCoerced.toString().also { newText ->
        if (selection.start == 0 && selection.end == text.length) {
            selection = TextRange(0, newText.length)
        }
    }

    fun TextRange.constrain(minimumValue: Int, maximumValue: Int): TextRange {
        val newStart = start.coerceIn(minimumValue, maximumValue)
        val newEnd = end.coerceIn(minimumValue, maximumValue)
        if (newStart != start || newEnd != end) {
            return TextRange(newStart, newEnd)
        }
        return this
    }

    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = TextFieldValue(
            text = text,
            selection = selection.constrain(0, text.length),
        ),
        onValueChange = { newValue ->
            val newText = newValue.text
            val newInt = when {
                newText.isEmpty() -> 0
                newText.length <= maxDigitsCount -> newText.toIntOrNull()
                else -> null
            } ?: return@BasicTextField

            selection = newValue.selection

            if (valuesCoerced != newInt) {
                onValueChange(newInt)
            }
        },
        modifier = modifier
            .focusableBorder()
            .onFocusChanged {
                if (isFocused == it.isFocused) {
                    return@onFocusChanged
                }

                isFocused = it.isFocused

                if (isFocused) {
                    // FIXME: Need to have a better way to set selection on the next frame
                    // because onValueChange rewrites our effort
                    GlobalScope.launch(Dispatchers.Main) {
                        selection = TextRange(0, text.length)
                    }
                }
            }
            .padding(8.dp),
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current
            .copy(
                color = LocalContentColor.current,
                textAlign = TextAlign.Center
            ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        maxLines = 1,
        visualTransformation = visualTransformation,
    )
}

private fun Int.pow(n: Int) = IntArray(n) { this }.fold(1) { lhs, rhs -> lhs * rhs }

private const val DEFAULT_MAX_DIGITS = 6

@Preview
@Composable
private fun Preview() {
    Column {
        NumberInputField(state = remember { mutableStateOf(666) })
        NumberInputField(state = remember { mutableStateOf(1666) }, maxDigitsCount = 3)
    }
}
