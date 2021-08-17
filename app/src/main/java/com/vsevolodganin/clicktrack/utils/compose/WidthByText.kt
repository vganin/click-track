package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.MultiParagraphIntrinsics
import androidx.compose.ui.text.TextStyle

fun Modifier.widthByText(
    text: String,
    style: TextStyle,
) = composed {
    val maxIntrinsics = MultiParagraphIntrinsics(
        annotatedString = AnnotatedString(text),
        style = style,
        placeholders = emptyList(),
        density = LocalDensity.current,
        resourceLoader = LocalFontLoader.current
    )

    val widthDp = with(LocalDensity.current) {
        maxIntrinsics.maxIntrinsicWidth.toDp()
    }

    width(widthDp)
}
