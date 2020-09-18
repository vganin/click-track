package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration

@Serializable
@AndroidParcelize
public class CueWithDuration(
    public val duration: CueDuration,
    public val cue: Cue
) : AndroidParcelable {

    public val durationInTime: Duration
        get() {
            return when (duration) {
                is CueDuration.Time -> duration.value.value
                is CueDuration.Beats -> cue.bpm.interval * duration.value
            }
        }
}
