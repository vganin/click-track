package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(38.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = 0.5f),
        )
    }
}
