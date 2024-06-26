package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeSignature(
    val noteCount: Int,
    // For backward compatibility
    @SerialName("noteDuration")
    val noteValue: Int,
) {
    init {
        require(noteCount >= 0) { "Note count should be non-negative but was: $noteCount" }
        require(noteValue >= 0) { "Note value should be non-negative but was: $noteValue" }
    }
}
