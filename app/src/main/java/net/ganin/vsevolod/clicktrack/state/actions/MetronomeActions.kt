package net.ganin.vsevolod.clicktrack.state.actions

import net.ganin.vsevolod.clicktrack.lib.BeatsPerMinute
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.MetronomeScreenState

sealed class MetronomeActions : Action {
    class UpdateMetronomeState(val state: MetronomeScreenState) : MetronomeActions()
    class ChangeBpm(val bpm: BeatsPerMinute, val progress: Float) : MetronomeActions()
}
