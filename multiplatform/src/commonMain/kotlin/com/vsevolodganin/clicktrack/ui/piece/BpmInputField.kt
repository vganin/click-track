package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.vsevolodganin.clicktrack.generated.resources.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BpmInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val bpmSuffix = stringResource(MR.strings.bpm_input_suffix)
    NumberInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        isError = isError,
        maxDigitsCount = 3,
        visualTransformation = { inputText ->
            TransformedText(
                text = inputText + AnnotatedString(bpmSuffix),
                offsetMapping = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int = offset
                    override fun transformedToOriginal(offset: Int): Int = offset.coerceIn(0..inputText.length)
                }
            )
        }
    )
}
