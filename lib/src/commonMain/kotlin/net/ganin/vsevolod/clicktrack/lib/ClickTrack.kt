package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public class ClickTrack(
    public val cues: List<CueWithDuration>,
    public val loop: Boolean
) {
    public val durationInTime: Duration
        get() = cues.map(CueWithDuration::durationInTime).reduce { acc, duration -> acc + duration }
}
