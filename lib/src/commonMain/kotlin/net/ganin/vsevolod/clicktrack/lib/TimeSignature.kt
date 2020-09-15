package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable

@Serializable
public class TimeSignature(
    public val noteCount: Int,
    public val noteDuration: Int
) {
    init {
        require(noteCount >= 0) { "Note count should be greater than 0 but was: $noteCount"}
        require(noteDuration >= 0) { "Note durations should be greater than 0 but was: $noteDuration"}
    }
}
