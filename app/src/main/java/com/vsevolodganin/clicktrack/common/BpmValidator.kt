package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.bpm
import dagger.Reusable
import javax.inject.Inject

@Reusable
class BpmValidator @Inject constructor() {

    class ValidationResult(
        val coercedBpm: BeatsPerMinute,
        val hasError: Boolean,
    )

    fun validate(value: Int): ValidationResult {
        return if (value !in BeatsPerMinute.VALID_TEMPO_RANGE) {
            ValidationResult(
                coercedBpm = value.coerceIn(BeatsPerMinute.VALID_TEMPO_RANGE).bpm,
                hasError = true
            )
        } else {
            ValidationResult(
                coercedBpm = value.bpm,
                hasError = false
            )
        }
    }
}
