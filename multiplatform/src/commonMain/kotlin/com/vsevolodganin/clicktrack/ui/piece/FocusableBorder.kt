package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged

fun Modifier.focusableBorder(isError: Boolean = false): Modifier =
    composed {
        var isFocused by remember { mutableStateOf(false) }

        this
            .onFocusChanged { focusState ->
                if (isFocused == focusState.isFocused) {
                    return@onFocusChanged
                }

                isFocused = focusState.isFocused
            }
            .selectableBorder(isFocused, isError)
    }
