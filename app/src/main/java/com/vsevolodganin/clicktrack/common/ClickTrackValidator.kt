package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.redux.EditCueState
import javax.inject.Inject

class ClickTrackValidator @Inject constructor(
    private val bpmValidator: BpmValidator,
) {

    class ClickTrackValidationResult(
        val validClickTrack: ClickTrack,
        val errors: Set<EditClickTrackState.Error>,
        val cueValidationResults: List<CueValidationResult>,
    )

    class CueValidationResult(
        val validCue: Cue,
        val errors: Set<EditCueState.Error>,
    )

    fun validate(editClickTrackState: EditClickTrackState): ClickTrackValidationResult {
        val name = editClickTrackState.name.trim()
        val errors = mutableSetOf<EditClickTrackState.Error>()
        if (name.isEmpty()) {
            errors += EditClickTrackState.Error.NAME
        }
        val cueValidationResults = editClickTrackState.cues.map(::validate)

        return ClickTrackValidationResult(
            validClickTrack = ClickTrack(
                name = name,
                loop = editClickTrackState.loop,
                tempoDiff = editClickTrackState.tempoDiff,
                cues = cueValidationResults.map(CueValidationResult::validCue),
            ),
            errors = errors,
            cueValidationResults = cueValidationResults,
        )
    }

    private fun validate(editCueState: EditCueState): CueValidationResult {
        val name = editCueState.name.trim()
        val errors = mutableSetOf<EditCueState.Error>()

        val bpmValidationResult = bpmValidator.validate(editCueState.bpm)
        if (bpmValidationResult.hadError) {
            errors += EditCueState.Error.BPM
        }

        return CueValidationResult(
            validCue = Cue(
                name = name,
                bpm = bpmValidationResult.coercedBpm,
                timeSignature = editCueState.timeSignature,
                duration = when (editCueState.activeDurationType) {
                    EditCueState.DurationType.BEATS -> editCueState.beats
                    EditCueState.DurationType.MEASURES -> editCueState.measures
                    EditCueState.DurationType.TIME -> editCueState.time
                },
                pattern = editCueState.pattern
            ),
            errors = errors,
        )
    }
}
