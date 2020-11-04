package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

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
