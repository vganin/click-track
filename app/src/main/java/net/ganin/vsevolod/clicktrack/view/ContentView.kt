package net.ganin.vsevolod.clicktrack.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.view.screen.ClickTrackListScreenView
import net.ganin.vsevolod.clicktrack.view.screen.ClickTrackScreenView
import net.ganin.vsevolod.clicktrack.view.screen.EditClickTrackScreenView

@Composable
fun ContentView(screen: Screen, dispatch: Dispatch) {
    MaterialTheme {
        when (screen) {
            is Screen.ClickTrackList -> ClickTrackListScreenView(screen.state, dispatch)
            is Screen.ClickTrack -> ClickTrackScreenView(screen.state, dispatch)
            is Screen.EditClickTrack -> EditClickTrackScreenView(screen.state, dispatch)
        }
    }
}
