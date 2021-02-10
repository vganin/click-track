@file:Suppress("FunctionName") // Constructor functions should be named as class

package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.PlaybackState

object StoreAddNewClickTrack : Action
class StoreUpdateClickTrack(val clickTrack: ClickTrackWithId) : Action {
    class Result(val clickTrack: ClickTrackWithId, val isErrorInName: Boolean) : Action
}
class StoreRemoveClickTrack(val id: Long) : Action
class UpdateCurrentlyPlaying(val playbackState: PlaybackState?) : Action
class StartPlay(val clickTrack: ClickTrackWithId, val progress: Double? = null) : Action
object StopPlay : Action
object PausePlay : Action
class UpdateClickTrackList(val data: List<ClickTrackWithId>) : Action
class UpdateClickTrack(val data: ClickTrackWithId) : Action
