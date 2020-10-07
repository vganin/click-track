package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp

object AddNewClickTrack : Action

class RemoveClickTrack(val clickTrack: ClickTrackWithMeta) : Action

class SaveClickTrack(val clickTrack: ClickTrackWithMeta) : Action

object TogglePlay : Action

object StopPlay : Action

object ResetPlaybackStamp : Action
class UpdatePlaybackStamp(val value: PlaybackStamp) : Action
