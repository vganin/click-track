package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class TimeSignature(
    public val noteCount: Int,
    public val noteDuration: Int
) : AndroidParcelable {
    init {
        require(noteCount >= 0) { "Note count should be non-negative but was: $noteCount" }
        require(noteDuration >= 0) { "Note durations should be non-negative but was: $noteDuration" }
    }
}
