package net.ganin.vsevolod.clicktrack.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Composable
fun ContentView(items: List<ClickTrack>) {
    MaterialTheme {
        ClickTrackListView(
            items,
            modifier = Modifier.fillMaxSize()
        )
    }
}
