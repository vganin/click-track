package com.vsevolodganin.clicktrack.training

import com.vsevolodganin.clicktrack.common.BpmValidator
import javax.inject.Inject

class TrainingStateValidator @Inject constructor(
    private val bpmValidator: BpmValidator,
) {

    class ValidationResult(
        val persistableState: TrainingValidState?,
        val errors: Set<TrainingEditState.Error>,
    )

    fun validate(state: TrainingEditState): ValidationResult {
        val startingTempoValidationResult = bpmValidator.validate(state.startingTempo)
        val tempoChangeValidationResult = bpmValidator.validate(state.tempoChange)
        val endingByTempoValidationResult = bpmValidator.validate(state.endingByTempo.endingTempo)

        val errors = buildSet {
            if (startingTempoValidationResult.hasError) {
                this += TrainingEditState.Error.STARTING_TEMPO
            }

            if (tempoChangeValidationResult.hasError) {
                this += TrainingEditState.Error.TEMPO_CHANGE
            }

            if (endingByTempoValidationResult.hasError && state.activeEndingKind == TrainingEndingKind.BY_TEMPO) {
                this += TrainingEditState.Error.ENDING_TEMPO
            }
        }

        return if (errors.isNotEmpty()) {
            ValidationResult(
                persistableState = null,
                errors = errors,
            )
        } else {
            ValidationResult(
                persistableState = TrainingValidState(
                    startingTempo = startingTempoValidationResult.coercedBpm,
                    mode = state.mode,
                    segmentLength = state.segmentLength,
                    tempoChange = tempoChangeValidationResult.coercedBpm,
                    ending = when (state.activeEndingKind) {
                        TrainingEndingKind.BY_TEMPO -> TrainingValidState.Ending.ByTempo(endingByTempoValidationResult.coercedBpm)
                        TrainingEndingKind.BY_TIME -> TrainingValidState.Ending.ByTime(state.endingByTime.duration)
                    }
                ),
                errors = errors,
            )
        }
    }
}
