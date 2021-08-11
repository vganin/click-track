package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.FloatingActionButtonElevation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.google.accompanist.insets.navigationBarsPadding

@Composable
fun InsetsAwareFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableInsets: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.run {
            if (enableInsets) {
                navigationBarsPadding()
            } else {
                this
            }
        },
        interactionSource = interactionSource,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        content = content,
    )
}
