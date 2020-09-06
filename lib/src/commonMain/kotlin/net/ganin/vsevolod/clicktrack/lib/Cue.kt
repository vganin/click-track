package net.ganin.vsevolod.clicktrack.lib

public class Cue(
    public val bpm: Int,
    public val timeSignature: TimeSignature
) {
    init {
        require(bpm >= 0) { "Bpm should be greater than 0 but was: $bpm"}
    }
}
