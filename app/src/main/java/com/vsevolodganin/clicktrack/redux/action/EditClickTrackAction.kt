package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.redux.EditCueState
import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface EditClickTrackAction : Action {

    class EditName(
        val name: String,
    ) : EditClickTrackAction

    class EditLoop(
        val loop: Boolean,
    ) : EditClickTrackAction

    object IncrementTempoDiff : EditClickTrackAction
    object DecrementTempoDiff : EditClickTrackAction

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
