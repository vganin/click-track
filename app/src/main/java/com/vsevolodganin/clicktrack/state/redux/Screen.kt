package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.reducer.reduceEditClickTrackScreen
import com.vsevolodganin.clicktrack.state.redux.reducer.reduceMetronome
import kotlinx.parcelize.Parcelize

sealed interface Screen : Parcelable {

    fun reduce(action: Action): Screen

    @Parcelize
    object ClickTrackList : Screen {
        override fun reduce(action: Action): Screen = this
    }

    @Parcelize
    data class PlayClickTrack(val state: PlayClickTrackState) : Screen {
        override fun reduce(action: Action): Screen = this
    }

    @Parcelize
    data class EditClickTrack(val state: EditClickTrackState) : Screen {
        override fun reduce(action: Action): Screen {
            return reduceEditClickTrackScreen(action)
        }
    }

    @Parcelize
    data class Metronome(val state: MetronomeState) : Screen {
        override fun reduce(action: Action): Screen {
            return reduceMetronome(action)
        }
    }

    @Parcelize
    object Settings : Screen {
        override fun reduce(action: Action): Screen = this
    }

    @Parcelize
    object SoundLibrary : Screen {
        override fun reduce(action: Action): Screen = this
    }
}
