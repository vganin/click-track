package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface MetronomeAction : Action {

    class SetBpm(val bpm: BeatsPerMinute) : MetronomeAction

    class ChangeBpm(val by: BeatsPerMinuteDiff) : MetronomeAction

    class SetPattern(val pattern: NotePattern) : MetronomeAction

    object BpmMeterTap : MetronomeAction

    object ToggleOptions : MetronomeAction

    object OpenOptions : MetronomeAction

    object CloseOptions : MetronomeAction
}
