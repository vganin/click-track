package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.state.screen.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.screen.DrawerScreenState
import com.vsevolodganin.clicktrack.state.screen.Screen
import com.vsevolodganin.clicktrack.state.screen.ScreenBackstack
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppState(
    val backstack: ScreenBackstack,
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
                screens = listOf(INITIAL_SCREEN),
                drawerState = DrawerScreenState(
                    isOpened = false,
                    gesturesEnabled = true,
                    selectedItem = null,
                    displayVersion = BuildConfig.DISPLAY_VERSION
                ),
            ),
            currentlyPlaying = null,
        )
    }
}


