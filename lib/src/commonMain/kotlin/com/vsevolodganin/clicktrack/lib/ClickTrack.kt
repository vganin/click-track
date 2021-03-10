package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidIgnoredOnParcel
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class ClickTrack(
    public val name: String,
    public val cues: List<Cue>,
    public val loop: Boolean,
) : AndroidParcelable {

    @AndroidIgnoredOnParcel
    public val durationInTime: Duration by lazy {
        cues.map(Cue::durationAsTime).reduceOrNull { acc, duration -> acc + duration } ?: Duration.ZERO
    }
}
