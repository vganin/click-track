package com.vsevolodganin.clicktrack.utils.compose

import android.text.Annotation
import android.text.SpannedString
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.getSpans

fun CharSequence.toAnnotatedString(
    spanStyles: (Annotation) -> SpanStyle? = { null }
): AnnotatedString {
    val spannedString = SpannedString(this)
    return buildAnnotatedString {
        append(spannedString.toString())
        spannedString.getSpans<Annotation>(0, spannedString.length).forEach { annotation ->
            val spanStart = spannedString.getSpanStart(annotation)
            val spanEnd = spannedString.getSpanEnd(annotation)
            addStringAnnotation(
                tag = annotation.key,
                annotation = annotation.value,
                start = spanStart,
                end = spanEnd
            )
            spanStyles(annotation)?.let { addStyle(it, spanStart, spanEnd) }
        }
    }
}
