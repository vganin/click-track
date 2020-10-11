package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration

@Serializable
@AndroidParcelize
public data class ClickTrack(
    public val cues: List<CueWithDuration>,
    public val loop: Boolean
) : AndroidParcelable {

    public val durationInTime: Duration by lazy {
        cues.map(CueWithDuration::durationInTime).reduce { acc, duration -> acc + duration }
    }
}
