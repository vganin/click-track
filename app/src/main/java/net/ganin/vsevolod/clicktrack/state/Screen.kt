package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.reducer.reduceClickTrackListScreen
import net.ganin.vsevolod.clicktrack.state.reducer.reduceEditClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.reducer.reduceMetronome
import net.ganin.vsevolod.clicktrack.state.reducer.reducePlayClickTrackScreen

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
