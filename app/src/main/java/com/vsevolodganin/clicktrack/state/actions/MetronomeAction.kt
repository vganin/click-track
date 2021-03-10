package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.MetronomeScreenState

sealed class MetronomeAction : Action {

    class SetScreenState(val state: MetronomeScreenState) : MetronomeAction()

    class ChangeBpm(val bpm: BeatsPerMinute) : MetronomeAction()

    object BpmMeterTap : MetronomeAction()
}
