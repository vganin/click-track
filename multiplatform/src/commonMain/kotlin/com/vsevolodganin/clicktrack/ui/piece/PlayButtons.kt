package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.utils.compose.SimpleSpacer
import kotlin.math.min

val PlayStopButtonCenterLine = VerticalAlignmentLine(::min)

@Composable
fun PlayButtons(
    isPlaying: Boolean,
    isPaused: Boolean,
    onTogglePlayStop: () -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(
            visible = isPlaying,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        ) {
            Column {
                PlayPauseButton(
                    isPlaying = !isPaused,
                    onToggle = onTogglePlayPause,
                    modifier = Modifier.size(40.dp),
                )
                SimpleSpacer(height = 8.dp)
            }
        }

        PlayStopButton(
            isPlaying = isPlaying,
            onToggle = onTogglePlayStop,
        )
    }
}
