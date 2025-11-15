package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.selectableBorder(
    isSelected: Boolean,
    isError: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    activeErrorColor: Color = MaterialTheme.colorScheme.error,
    inactiveErrorColor: Color = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
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
