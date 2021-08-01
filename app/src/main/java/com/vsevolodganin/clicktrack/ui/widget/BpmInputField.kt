package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.vsevolodganin.clicktrack.R

@Composable
fun BpmInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val bpmSuffix = stringResource(R.string.cue_bpm_suffix)
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
