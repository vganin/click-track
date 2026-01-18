package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.compose.animatePathAsState

@Composable
fun PlayPauseButton(isPlaying: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onToggle,
        modifier = modifier,
        shape = CircleShape,
    ) {
        PlayPauseIcon(isPlaying)
    }
}

@Composable
fun PlayPauseIcon(isPlaying: Boolean) {
    val pathData by animatePathAsState(
        if (isPlaying) {
            "M 10 38 L 10 10 L 21.75 10 L 21.75 38 L 10 38 M 26.25 38 L 26.25 10 L 38 10 L 38 38 L 26.25 38"
        } else {
            "M 16 9.85 L 38 23.85 L 38 23.85 L 16 23.957 L 16 9.85 M 16 23.957 L 38 23.85 L 38 23.85 L 16 37.85 L 16 23.957"
        },
    )

    val imageVector by remember {
        derivedStateOf {
            ImageVector.Builder(defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 48f, viewportHeight = 48f)
                .addPath(pathData = pathData, fill = SolidColor(Color.White))
                .build()
        }
    }

    Icon(
        imageVector = imageVector,
        contentDescription = null,
    )
}

@Preview
@Composable
private fun Preview() = ClickTrackTheme {
    var isPlaying by remember { mutableStateOf(false) }
    PlayPauseButton(
        isPlaying = isPlaying,
        onToggle = { isPlaying = !isPlaying },
    )
}
