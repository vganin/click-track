package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppState(
    val backstack: ScreenBackstack,
    val drawerState: DrawerScreenState,
    val currentlyPlaying: PlaybackState?,
) : Parcelable {

    companion object {
        private val INITIAL_SCREEN = Screen.ClickTrackList(
            state = ClickTrackListScreenState(
                items = emptyList()
            )
        )

        val INITIAL = AppState(
            backstack = ScreenBackstack(
                screens = listOf(INITIAL_SCREEN)
            ),
            drawerState = DrawerScreenState(
                isOpened = false,
                currentScreen = INITIAL_SCREEN,
            ),
            currentlyPlaying = null,
        )
    }
}


