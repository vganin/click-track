package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.edit.EditClickTrackState
import com.vsevolodganin.clicktrack.edit.EditCueState
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import me.tatarka.inject.annotations.Inject

@Inject
class ClickTrackValidator(private val bpmValidator: BpmValidator) {

    class ClickTrackValidationResult(
        val validClickTrack: ClickTrack,
        val cueValidationResults: List<CueValidationResult>,
    )

    class CueValidationResult(
        val validCue: Cue,
        val errors: Set<EditCueState.Error>,
    )

    fun validate(editClickTrackState: EditClickTrackState): ClickTrackValidationResult {
        val cueValidationResults = editClickTrackState.cues.map(::validate)

        return ClickTrackValidationResult(
            validClickTrack = ClickTrack(
                name = editClickTrackState.name.trim(),
                loop = editClickTrackState.loop,
                tempoDiff = editClickTrackState.tempoDiff,
                cues = cueValidationResults.map(CueValidationResult::validCue),
            ),
            cueValidationResults = cueValidationResults,
        )
    }

    private fun validate(editCueState: EditCueState): CueValidationResult {
        val name = editCueState.name.trim()
        val errors = mutableSetOf<EditCueState.Error>()

        val bpmValidationResult = bpmValidator.validate(editCueState.bpm)
        if (bpmValidationResult.hasError) {
            errors += EditCueState.Error.BPM
        }

        return CueValidationResult(
            validCue = Cue(
                name = name,
                bpm = bpmValidationResult.coercedBpm,
                timeSignature = editCueState.timeSignature,
                duration = when (editCueState.activeDurationType) {
                    CueDuration.Type.BEATS -> editCueState.beats
                    CueDuration.Type.MEASURES -> editCueState.measures
                    CueDuration.Type.TIME -> editCueState.time
                },
                pattern = editCueState.pattern
            ),
            errors = errors,
        )
    }
}
