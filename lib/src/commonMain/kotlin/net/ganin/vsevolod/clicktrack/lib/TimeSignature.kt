package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

@Serializable
@AndroidParcelize
public class TimeSignature(
    public val noteCount: Int,
    public val noteDuration: Int
) : AndroidParcelable {
    init {
        require(noteCount >= 0) { "Note count should be greater than 0 but was: $noteCount" }
        require(noteDuration >= 0) { "Note durations should be greater than 0 but was: $noteDuration" }
    }
}
