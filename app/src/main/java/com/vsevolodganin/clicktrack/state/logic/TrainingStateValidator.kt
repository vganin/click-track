package com.vsevolodganin.clicktrack.state.logic

import com.vsevolodganin.clicktrack.state.redux.EditCueState
import com.vsevolodganin.clicktrack.state.redux.TrainingPersistableState
import com.vsevolodganin.clicktrack.state.redux.TrainingState
import javax.inject.Inject

class TrainingStateValidator @Inject constructor(
    private val bpmValidator: BpmValidator,
) {

    class ValidationResult(
        val persistableState: TrainingPersistableState?,
        val errors: Set<TrainingState.Error>,
    )

    fun validate(state: TrainingState): ValidationResult {
        val startingTempoValidationResult = bpmValidator.validate(state.startingTempo)
        val tempoChangeValidationResult = bpmValidator.validate(state.tempoChange)
        val endingByTempoValidationResult = bpmValidator.validate(state.endingByTempo.endingTempo)

        val errors = buildSet {
            if (startingTempoValidationResult.hadError) {
                this += TrainingState.Error.STARTING_TEMPO
            }

            if (tempoChangeValidationResult.hadError) {
                this += TrainingState.Error.TEMPO_CHANGE
            }

            if (endingByTempoValidationResult.hadError && state.activeEndingKind == TrainingState.EndingKind.BY_TEMPO) {
                this += TrainingState.Error.ENDING_TEMPO
            }
        }

        return if (errors.isNotEmpty()) {
            ValidationResult(
                persistableState = null,
                errors = errors,
            )
        } else {
            ValidationResult(
                persistableState = TrainingPersistableState(
                    startingTempo = startingTempoValidationResult.coercedBpm,
                    mode = state.mode,
                    segmentLength = when (state.activeSegmentLengthType) {
                        EditCueState.DurationType.BEATS -> state.segmentLengthBeats
                        EditCueState.DurationType.MEASURES -> state.segmentLengthMeasures
                        EditCueState.DurationType.TIME -> state.segmentLengthTime
                    },
                    tempoChange = tempoChangeValidationResult.coercedBpm,
                    ending = when (state.activeEndingKind) {
                        TrainingState.EndingKind.BY_TEMPO -> TrainingPersistableState.Ending.ByTempo(endingByTempoValidationResult.coercedBpm)
                        TrainingState.EndingKind.BY_TIME -> TrainingPersistableState.Ending.ByTime(state.endingByTime.duration)
                    }
                ),
                errors = errors,
            )
        }
    }
}
