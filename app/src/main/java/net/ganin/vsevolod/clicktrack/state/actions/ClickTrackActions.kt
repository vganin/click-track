package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlaybackState

object StoreAddNewClickTrack : Action
class StoreUpdateClickTrack(val clickTrack: ClickTrackWithId) : Action {
    class Result(val clickTrack: ClickTrackWithId, val isErrorInName: Boolean) : Action
}
class StoreRemoveClickTrack(val id: Long) : Action

class UpdateCurrentlyPlaying(val playbackState: PlaybackState?) : Action

class StartPlay(val clickTrack: ClickTrackWithId, val progress: Float) : Action
object StopPlay : Action

class UpdateClickTrackList(val data: List<ClickTrackWithId>) : Action
class UpdateClickTrack(val data: ClickTrackWithId) : Action
