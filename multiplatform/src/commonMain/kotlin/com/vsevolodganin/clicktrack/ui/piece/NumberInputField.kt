package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.utils.compose.Preview
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun NumberInputField(
    state: MutableState<Int>,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    showSign: Boolean = false,
    allowedNumbersRange: IntRange = DEFAULT_ALLOWED_NUMBERS_RANGE,
    fallbackNumber: Int? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    NumberInputField(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        isError = isError,
        showSign = showSign,
        allowedNumbersRange = allowedNumbersRange,
        fallbackNumber = fallbackNumber,
        visualTransformation = visualTransformation,
    )
}

@Composable
fun NumberInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    showSign: Boolean = false,
    allowedNumbersRange: IntRange = DEFAULT_ALLOWED_NUMBERS_RANGE,
    fallbackNumber: Int? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val valueCoerced = value.coerceIn(allowedNumbersRange)

    var textFieldValue by remember(valueCoerced) {
        val text = valueCoerced.toText(showSign)
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(text.length),
            ),
        )
    }

    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val coroutineDispatcher = rememberCoroutineScope()

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val newText = newValue.text

            val newInt = when {
                isIntermediateEditText(newText, showSign) -> {
                    textFieldValue = newValue.copy(text = newText)
                    return@BasicTextField
                }
                else -> newText.toIntOrNull()
            }

            newInt
                ?.takeIf { it in allowedNumbersRange }
                ?: return@BasicTextField

            textFieldValue = newValue.copy(text = newInt.toText(showSign))

            if (valueCoerced != newInt) {
                onValueChange(newInt)
            }
        },
        modifier = modifier
            .focusableBorder(isError = isError)
            .onFocusChanged {
                if (isFocused == it.isFocused) {
                    return@onFocusChanged
                }

                isFocused = it.isFocused

                if (isFocused) {
                    // FIXME: Need to have a better way to set selection on the next frame
                    // because onValueChange rewrites our effort
                    coroutineDispatcher.launch {
                        textFieldValue = textFieldValue.copy(
                            selection = TextRange(0, textFieldValue.text.length),
                        )
                    }
                } else if (isIntermediateEditText(textFieldValue.text, showSign)) {
                    if (fallbackNumber != null && fallbackNumber != valueCoerced) {
                        onValueChange(fallbackNumber)
                    }
                    textFieldValue = textFieldValue.copy(
                        text = valueCoerced.toText(showSign),
                    )
                }
            }
            .padding(8.dp),
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current
            .copy(
                color = LocalContentColor.current,
                textAlign = TextAlign.Center,
            ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
        ),
        singleLine = true,
        maxLines = 1,
        visualTransformation = visualTransformation,
    )
}

private fun isIntermediateEditText(
    text: String,
    showSign: Boolean,
): Boolean {
    return text in if (showSign) {
        listOf("-", "+", "")
    } else {
        listOf("")
    }
}

private fun Int.toText(showSign: Boolean): String {
    return if (showSign) {
        if (this < 0) {
            "-${this.absoluteValue}"
        } else {
            "+$this"
        }
    } else {
        this.toString()
    }
}

private val DEFAULT_ALLOWED_NUMBERS_RANGE = 0..999999

@Preview
@Composable
private fun Preview() {
    Column {
        NumberInputField(state = remember { mutableStateOf(666) })
        NumberInputField(state = remember { mutableStateOf(1666) }, allowedNumbersRange = -999..999)
    }
}
