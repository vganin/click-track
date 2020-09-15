package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.minutes

@Serializable
public class CueWithDuration(
    public val duration: CueDuration,
    public val cue: Cue
) {
    public val durationInTime: Duration
        get() {
            return when (duration) {
                is CueDuration.Time -> duration.value.value
                is CueDuration.Beats -> 1.minutes / cue.bpm * duration.value
            }
        }
}
