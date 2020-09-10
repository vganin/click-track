package net.ganin.vsevolod.clicktrack.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Composable
fun ContentView(clickTrack: ClickTrack, onPlayToggle: (Boolean) -> Unit) {
    MaterialTheme {
        ClickTrackAndPlayStopView(clickTrack = clickTrack, onPlayToggle = onPlayToggle)
    }
}