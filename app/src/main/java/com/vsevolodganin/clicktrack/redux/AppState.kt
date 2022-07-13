package com.vsevolodganin.clicktrack.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppState(
    val backstack: ScreenBackstack,
    val drawerState: DrawerState
) : Parcelable {

    companion object {
        val INITIAL = AppState(
            backstack = ScreenBackstack(
                frontScreen = Screen.ClickTrackList,
                restScreens = emptyList(),
            ),
            drawerState = DrawerState(
                isOpened = false,
                selectedItem = null,
            ),
        )
    }
}
