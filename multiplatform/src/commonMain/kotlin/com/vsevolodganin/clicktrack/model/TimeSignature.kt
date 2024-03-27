package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TimeSignature(
    val noteCount: Int,
    @SerialName("noteDuration") // For backward compatibility
    val noteValue: Int,
) : Parcelable {
    init {
        require(noteCount >= 0) { "Note count should be non-negative but was: $noteCount" }
        require(noteValue >= 0) { "Note value should be non-negative but was: $noteValue" }
    }
}
