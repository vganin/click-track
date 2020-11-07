package net.ganin.vsevolod.clicktrack.view.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focusObserver
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.focusableBorder(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val emphasisLevels = AmbientEmphasisLevels.current
    val activeColor = emphasisLevels.high.applyEmphasis(MaterialTheme.colors.primary)
    val inactiveColor = emphasisLevels.disabled.applyEmphasis(MaterialTheme.colors.onSurface)
    val borderColor = if (isFocused) activeColor else inactiveColor
    val borderWidth = if (isFocused) 2.dp else 1.dp

    this
        .focusObserver { focusState ->
            if (isFocused == focusState.isFocused) {
                return@focusObserver
            }

            isFocused = focusState.isFocused
        }
        .border(
            border = BorderStroke(borderWidth, borderColor),
            shape = MaterialTheme.shapes.small,
        )
}
