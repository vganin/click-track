package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.reducer.reduceClickTrackListScreen
import net.ganin.vsevolod.clicktrack.state.reducer.reduceClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.reducer.reduceEditClickTrackScreen

sealed class Screen : Parcelable {

    abstract fun reduce(action: Action): Screen

    @Parcelize
    class ClickTrackList(val state: ClickTrackListScreenState) : Screen() {
        override fun reduce(action: Action): Screen {
            return reduceClickTrackListScreen(action)
        }
    }

    @Parcelize
    class ClickTrack(val state: ClickTrackScreenState) : Screen() {
        override fun reduce(action: Action): Screen {
            return reduceClickTrackScreen(action)
        }
    }

    @Parcelize
    class EditClickTrack(val state: EditClickTrackScreenState) : Screen() {
        override fun reduce(action: Action): Screen {
            return reduceEditClickTrackScreen(action)
        }
    }
}
