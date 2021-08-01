package com.vsevolodganin.clicktrack.state.logic

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.DefaultTempoRange
import dagger.Reusable
import javax.inject.Inject

@Reusable
class BpmValidator @Inject constructor() {

    class ValidationResult(
        val coercedBpm: BeatsPerMinute,
        val hadError: Boolean,
    )

    fun validate(value: Int): ValidationResult {
        return if (value !in Const.BPM_INT_RANGE) {
            ValidationResult(
                coercedBpm = value.coerceIn(Const.BPM_INT_RANGE).bpm,
                hadError = true
            )
        } else {
            ValidationResult(
                coercedBpm = value.bpm,
                hadError = false
            )
        }
    }

    fun validate(bpm: BeatsPerMinute): ValidationResult {
        return validate(bpm.value)
    }

    private object Const {
        val BPM_INT_RANGE = DefaultTempoRange.let { it.start.value..it.endInclusive.value }
    }
}
