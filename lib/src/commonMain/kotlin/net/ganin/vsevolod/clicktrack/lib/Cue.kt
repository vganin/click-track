package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable

@Serializable
public class Cue(
    public val bpm: Int,
    public val timeSignature: TimeSignature
) {
    init {
        require(bpm >= 0) { "Bpm should be greater than 0 but was: $bpm"}
    }
}
