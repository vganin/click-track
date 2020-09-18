package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp

object TogglePlay : Action

object StopPlay : Action

class UpdatePlaybackStamp(val value: PlaybackStamp) : Action
