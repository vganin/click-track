package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppState(
    val backstack: ScreenBackstack,
) : Parcelable {

    companion object {
        val INITIAL = AppState(
            backstack = ScreenBackstack(
                screens = listOf(Screen.ClickTrackList),
                drawerState = DrawerState(
                    isOpened = false,
                    gesturesEnabled = true,
                    selectedItem = null,
                ),
            ),
        )
    }
}


