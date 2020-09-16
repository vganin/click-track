package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration

@Serializable
@AndroidParcelize
public class ClickTrack(
    public val cues: List<CueWithDuration>,
    public val loop: Boolean
) : AndroidParcelable {
    public val durationInTime: Duration
        get() = cues.map(CueWithDuration::durationInTime).reduce { acc, duration -> acc + duration }
}
