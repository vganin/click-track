package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.MultiParagraphIntrinsics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

fun Modifier.widthByText(
    text: String,
    style: TextStyle,
) = composed {
    width(dpByText(text, style))
}

fun Modifier.widthInByText(
    minText: String? = null,
    maxText: String? = null,
    style: TextStyle,
) = composed {
    val min = minText?.let { dpByText(it, style) } ?: Dp.Unspecified
    val max = maxText?.let { dpByText(it, style) } ?: Dp.Unspecified
    this.widthIn(min = min, max = max)
}

@Composable
private fun dpByText(
    text: String,
    style: TextStyle,
): Dp {
    val maxIntrinsics = MultiParagraphIntrinsics(
        annotatedString = AnnotatedString(text),
        style = style,
        placeholders = emptyList(),
        density = LocalDensity.current,
        fontFamilyResolver = LocalFontFamilyResolver.current
    )

    return with(LocalDensity.current) {
        maxIntrinsics.maxIntrinsicWidth.toDp()
    }
}
