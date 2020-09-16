package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

@Serializable
@AndroidParcelize
public class Cue(
    public val bpm: Int,
    public val timeSignature: TimeSignature
) : AndroidParcelable {
    init {
        require(bpm >= 0) { "Bpm should be greater than 0 but was: $bpm" }
    }
}
