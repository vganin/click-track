package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BpmInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    showSign: Boolean = false,
    allowedNumbersRange: IntRange = BeatsPerMinute.VALID_TEMPO_RANGE,
    fallbackNumber: Int? = 1,
) {
    val bpmSuffix = stringResource(MR.strings.bpm_input_suffix)
    NumberInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        isError = isError,
        showSign = showSign,
        allowedNumbersRange = allowedNumbersRange,
        fallbackNumber = fallbackNumber,
        visualTransformation = { inputText ->
            TransformedText(
                text = inputText + AnnotatedString(bpmSuffix),
                offsetMapping = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int = offset

                    override fun transformedToOriginal(offset: Int): Int = offset.coerceIn(0..inputText.length)
                },
            )
        },
    )
}
