package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.redux.Action
import kotlin.time.Duration

object TogglePlay : Action

object StopPlay : Action

class UpdatePlaybackTimestamp(val value: Duration) : Action
