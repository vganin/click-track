package net.ganin.vsevolod.clicktrack.lib

import kotlin.time.Duration

public class ClickTrack(
    public val cues: List<CueWithDuration>,
    public val loop: Boolean
) {
    public val durationInTime: Duration
        get() = cues.map(CueWithDuration::durationInTime).reduce { acc, duration -> acc + duration }
}

