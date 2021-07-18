package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.state.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.state.redux.EditCueState
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface EditClickTrackAction : Action {

    class EditName(
        val name: String,
    ) : EditClickTrackAction

    class EditLoop(
        val loop: Boolean,
    ) : EditClickTrackAction

    object AddNewCue : EditClickTrackAction

    class MoveCue(
        val fromIndex: Int,
        val toIndex: Int,
    ) : EditClickTrackAction

    class RemoveCue(
        val index: Int,
    ) : EditClickTrackAction

    class SetErrors(
        val errors: Set<EditClickTrackState.Error>,
    ) : EditClickTrackAction

    sealed interface EditCueAction : EditClickTrackAction {

        val index: Int

        class EditName(
            override val index: Int,
            val name: String,
        ) : EditCueAction

        class EditBpm(
            override val index: Int,
            val bpm: Int,
        ) : EditCueAction

        class EditTimeSignature(
            override val index: Int,
            val timeSignature: TimeSignature,
        ) : EditCueAction

        class EditDuration(
            override val index: Int,
            val duration: CueDuration,
        ) : EditCueAction

        class EditDurationType(
            override val index: Int,
            val durationType: EditCueState.DurationType,
        ) : EditCueAction

        class EditPattern(
            override val index: Int,
            val pattern: NotePattern,
        ) : EditCueAction

        class SetErrors(
            override val index: Int,
            val errors: Set<EditCueState.Error>,
        ) : EditCueAction
    }
}
