package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp

object AddNewClickTrack : Action

class PersistClickTrack(val clickTrack: ClickTrackWithId) : Action
class UpdateClickTrack(val clickTrack: ClickTrackWithId, val isErrorInName: Boolean) : Action

class RemoveClickTrack(val id: Long) : Action

object TogglePlay : Action

object StopPlay : Action

object ResetPlaybackStamp : Action
class UpdatePlaybackStamp(val value: PlaybackStamp) : Action

object ClickTrackListLoadRequestAction : Action
class ClickTrackListDataLoadedAction(val data: List<ClickTrackWithId>) : Action

class ClickTrackLoadRequestAction(val id: Long) : Action
class ClickTrackDataLoadedAction(val data: ClickTrackWithId) : Action
