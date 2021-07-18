package com.vsevolodganin.clicktrack.state.logic

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.lib.toBpmRange
import com.vsevolodganin.clicktrack.state.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.state.redux.EditCueState
import javax.inject.Inject

class ClickTrackValidator @Inject constructor() {

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
                cues = cueValidationResults.map(CueValidationResult::validCue),
            ),
            errors = errors,
            cueValidationResults = cueValidationResults,
        )
    }

    private fun validate(editCueState: EditCueState): CueValidationResult {
        val name = editCueState.name.trim()
        val errors = mutableSetOf<EditCueState.Error>()
        if (editCueState.bpm !in Const.BPM_INT_RANGE) {
            errors += EditCueState.Error.BPM
        }

        return CueValidationResult(
            validCue = Cue(
                name = name,
                bpm = limitBpm(editCueState.bpm.coerceIn(Const.BPM_INT_RANGE).bpm),
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

    fun limitBpm(bpm: BeatsPerMinute): BeatsPerMinute {
        return bpm.coerceIn(Const.BPM_RANGE)
    }

    private object Const {
        val BPM_INT_RANGE = 1..999
        val BPM_RANGE = BPM_INT_RANGE.toBpmRange()
    }
}
