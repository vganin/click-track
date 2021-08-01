package com.vsevolodganin.clicktrack.ui.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

fun Modifier.focusableBorder(isError: Boolean = false): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val activeColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)
    val inactiveColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
    val activeErrorColor = MaterialTheme.colors.error.copy(alpha = ContentAlpha.high)
    val inactiveErrorColor = MaterialTheme.colors.error.copy(alpha = ContentAlpha.disabled)
    val borderColor = when {
        isFocused -> when {
            isError -> activeErrorColor
            else -> activeColor
        }
        else -> {
            when {
                isError -> inactiveErrorColor
                else -> inactiveColor
            }
        }
    }
    val borderWidth by animateDpAsState(if (isFocused) 2.dp else 1.dp)

    this
        .onFocusChanged { focusState ->
            if (isFocused == focusState.isFocused) {
                return@onFocusChanged
            }

            isFocused = focusState.isFocused
        }
        .border(
            border = BorderStroke(borderWidth, borderColor),
            shape = MaterialTheme.shapes.small,
        )
}
