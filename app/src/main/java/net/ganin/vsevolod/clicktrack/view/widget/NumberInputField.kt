package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.AmbientTextStyle
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.CoreTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focusObserver
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.view.common.focusableBorder

@Composable
fun NumberInputField(
    state: MutableState<Int>,
    modifier: Modifier = Modifier,
    maxDigitsCount: Int = 6
) {
    var text by remember { mutableStateOf(state.value.toString()) }
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
                newText.length <= maxDigitsCount -> newText.toIntOrNull()
                else -> null
            } ?: return@CoreTextField

            text = newText
            selection = newValue.selection
            composition = newValue.composition
            state.value = newInt
        },
        cursorColor = AmbientContentColor.current,
        textStyle = AmbientTextStyle.current
            .copy(
                color = AmbientContentColor.current,
                textAlign = TextAlign.Center
            ),
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
                    text = state.value.toString()
                }
            }
            .padding(8.dp),
        keyboardType = KeyboardType.Number,
        softWrap = false,
        maxLines = 1
    )
}

@Preview
@Composable
fun PreviewNumberInputField() {
    NumberInputField(state = mutableStateOf(666))
}
