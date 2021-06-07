package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.MetronomeScreenState

sealed class MetronomeAction : Action {

    class SetScreenState(val state: MetronomeScreenState) : MetronomeAction()

    class SetBpm(val bpm: BeatsPerMinute) : MetronomeAction()

    class ChangeBpm(val by: BeatsPerMinuteDiff) : MetronomeAction()

    class SetPattern(val pattern: NotePattern) : MetronomeAction()

    object BpmMeterTap : MetronomeAction()

    object ToggleOptions : MetronomeAction()

    object OpenOptions : MetronomeAction()

    object CloseOptions : MetronomeAction()
}
