package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.reducer.reduceClickTrackListScreen
import com.vsevolodganin.clicktrack.state.reducer.reduceEditClickTrackScreen
import com.vsevolodganin.clicktrack.state.reducer.reduceMetronome
import com.vsevolodganin.clicktrack.state.reducer.reducePlayClickTrackScreen
import kotlinx.parcelize.Parcelize

sealed class Screen : Parcelable {

    abstract fun reduce(action: Action): Screen

    @Parcelize
    data class ClickTrackList(val state: ClickTrackListScreenState) : Screen() {
        override fun reduce(action: Action): Screen {
            return reduceClickTrackListScreen(action)
        }
    }

    @Parcelize
    data class PlayClickTrack(val state: PlayClickTrackScreenState) : Screen() {
        override fun reduce(action: Action): Screen {
            return reducePlayClickTrackScreen(action)
        }
    }

    @Parcelize
    data class EditClickTrack(val state: EditClickTrackScreenState) : Screen() {
        override fun reduce(action: Action): Screen {
            return reduceEditClickTrackScreen(action)
        }
    }

    @Parcelize
    data class Metronome(val state: MetronomeScreenState?) : Screen() {
        override fun reduce(action: Action): Screen {
            return reduceMetronome(action)
        }
    }
}
