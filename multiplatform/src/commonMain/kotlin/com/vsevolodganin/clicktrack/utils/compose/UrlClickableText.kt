@file:Suppress("DEPRECATION") // FIXME: Use LinkAnnotatation.Url instead of UrlAnnotation

package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@OptIn(ExperimentalTextApi::class)
@Composable
fun UrlClickableText(
    textWithUrls: String,
    onUrlClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val annotatedText = remember(textWithUrls) {
        buildAnnotatedString {
            var index = 0
            PATTERN.findAll(textWithUrls).forEach { matchResult ->
                append(textWithUrls, index, matchResult.range.first)

                val text = matchResult.groupValues[1]
                val url = matchResult.groupValues[2]

                append(text)

                val textBegin = matchResult.range.first
                val textEnd = textBegin + text.length
                addUrlAnnotation(UrlAnnotation(url), textBegin, textEnd)
                addStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline), textBegin, textEnd)

                index = matchResult.range.last + 1
            }

            append(textWithUrls, index, textWithUrls.length)
        }
    }

    MaterialClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getUrlAnnotations(start = offset, end = offset)
                .firstOrNull()
                ?.let { onUrlClick(it.item.url) }
        },
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        inlineContent = inlineContent,
        onTextLayout = onTextLayout,
        style = style,
    )
}

private val PATTERN = """\[(.*?)]\((.*?)\)""".toRegex()
