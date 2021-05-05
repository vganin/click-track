package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class TimeSignature(
    public val noteCount: Int,
    @SerialName("noteDuration")
    public val noteValue: Int
) : AndroidParcelable {
    init {
        require(noteCount >= 0) { "Note count should be non-negative but was: $noteCount" }
        require(noteValue >= 0) { "Note value should be non-negative but was: $noteValue" }
    }
}
