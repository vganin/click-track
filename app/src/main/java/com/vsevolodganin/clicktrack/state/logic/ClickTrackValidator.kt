package com.vsevolodganin.clicktrack.state.logic

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.bpm
import javax.inject.Inject

class ClickTrackValidator @Inject constructor() {

    class ClickTrackValidationResult(
        val adjustedClickTrack: ClickTrack,
        val isErrorInName: Boolean,
    )

    fun validate(clickTrack: ClickTrack): ClickTrackValidationResult {
        val name = clickTrack.name.trim()
        val isErrorInName = name.isEmpty()
        return ClickTrackValidationResult(
            adjustedClickTrack = clickTrack.copy(
                name = name,
                cues = clickTrack.cues.map { it.copy(bpm = limitBpm(it.bpm)) }
            ),
            isErrorInName = isErrorInName,
        )
    }

    fun limitBpm(bpm: BeatsPerMinute): BeatsPerMinute {
        return bpm.coerceIn(Const.BPM_RANGE)
    }

    private object Const {
        val BPM_RANGE = 1.bpm..999.bpm
    }
}
