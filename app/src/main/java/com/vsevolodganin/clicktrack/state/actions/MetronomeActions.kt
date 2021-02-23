package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.MetronomeScreenState

sealed class MetronomeActions : Action {

    class SetScreenState(val state: MetronomeScreenState) : MetronomeActions()

    class ChangeBpm(val bpm: BeatsPerMinute) : MetronomeActions()

    object BpmMeterTap : MetronomeActions()
}
