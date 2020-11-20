package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppState(
    val backstack: ScreenBackstack,
    val currentlyPlaying: PlaybackState?
) : Parcelable {

    companion object {
        val INITIAL = AppState(
            backstack = ScreenBackstack(
                screens = listOf(
                    Screen.ClickTrackList(
                        state = ClickTrackListScreenState(
                            items = emptyList()
                        )
                    )
                )
            ),
            currentlyPlaying = null
        )
    }
}


