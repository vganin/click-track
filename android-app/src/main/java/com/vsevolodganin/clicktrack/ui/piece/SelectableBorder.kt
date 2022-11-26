package com.vsevolodganin.clicktrack.ui.piece

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("ComposableModifierFactory") // Need to access material colors in default arguments
@Composable
fun Modifier.selectableBorder(
    isSelected: Boolean,
    isError: Boolean = false,
    activeColor: Color = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
    inactiveColor: Color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
    activeErrorColor: Color = MaterialTheme.colors.error.copy(alpha = ContentAlpha.high),
    inactiveErrorColor: Color = MaterialTheme.colors.error.copy(alpha = ContentAlpha.disabled),
): Modifier {
    val borderColor = when {
        isSelected -> when {
            isError -> activeErrorColor
            else -> activeColor
        }
        else -> when {
            isError -> inactiveErrorColor
            else -> inactiveColor
        }
    }
    val borderWidth by animateDpAsState(if (isSelected) 2.dp else 1.dp)

    val shape = MaterialTheme.shapes.small

    return this
        .border(
            border = BorderStroke(borderWidth, borderColor),
            shape = MaterialTheme.shapes.small,
        )
        .clip(shape)
}
