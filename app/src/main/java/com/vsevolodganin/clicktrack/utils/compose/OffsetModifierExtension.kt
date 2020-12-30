package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset

fun Modifier.offset(x: Density.() -> Int = { 0 }, y: Density.() -> Int = { 0 }): Modifier {
    return offset { IntOffset(x = x(), y = y()) }
}
