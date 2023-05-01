package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.bpm
import me.tatarka.inject.annotations.Inject

@Inject
class BpmValidator() {

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
