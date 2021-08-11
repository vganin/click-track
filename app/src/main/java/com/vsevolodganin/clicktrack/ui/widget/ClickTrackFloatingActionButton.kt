package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.FloatingActionButtonElevation
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun ClickTrackFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableInsets: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    val backgroundColor = MaterialTheme.colors.secondary
    val contentColor = if (isSystemInDarkTheme()) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary

    InsetsAwareFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        enableInsets = enableInsets,
        interactionSource = interactionSource,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        content = content,
    )
}
