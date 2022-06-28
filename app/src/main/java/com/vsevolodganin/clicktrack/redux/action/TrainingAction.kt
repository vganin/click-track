package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.redux.EditCueState
import com.vsevolodganin.clicktrack.redux.TrainingMode
import com.vsevolodganin.clicktrack.redux.TrainingState
import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface TrainingAction : Action {

    class EditStartingTempo(val tempo: Int) : TrainingAction

    class EditMode(val mode: TrainingMode) : TrainingAction

    class EditSegmentLength(val segmentLength: CueDuration) : TrainingAction

    class EditSegmentLengthType(val segmentLengthType: EditCueState.DurationType) : TrainingAction

    class EditTempoChange(val tempo: Int) : TrainingAction

    class EditEnding(val ending: TrainingState.Ending) : TrainingAction

    class EditEndingKind(val endingKind: TrainingState.EndingKind) : TrainingAction

    class UpdateErrors(val errors: Set<TrainingState.Error>) : TrainingAction

    object Accept : TrainingAction
}
