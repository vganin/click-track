package net.ganin.vsevolod.clicktrack.view

import androidx.compose.runtime.Composable
import net.ganin.vsevolod.clicktrack.redux.Dispatch
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.view.screen.ClickTrackListScreenView
import net.ganin.vsevolod.clicktrack.view.screen.EditClickTrackScreenView
import net.ganin.vsevolod.clicktrack.view.screen.PlayClickTrackScreenView

@Composable
fun ContentView(screen: Screen, dispatch: Dispatch) {
    ClickTrackTheme {
        when (screen) {
            is Screen.ClickTrackList -> ClickTrackListScreenView(screen.state, dispatch)
            is Screen.PlayClickTrack -> PlayClickTrackScreenView(screen.state, dispatch)
            is Screen.EditClickTrack -> EditClickTrackScreenView(screen.state, dispatch)
        }
    }
}
