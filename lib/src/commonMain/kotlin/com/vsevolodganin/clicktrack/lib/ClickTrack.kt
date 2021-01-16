package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidIgnoredOnParcel
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@AndroidParcelize
public data class ClickTrack(
    public val name: String,
    public val cues: List<CueWithDuration>,
    public val loop: Boolean,
    public val sounds: ClickSounds,
) : AndroidParcelable {

    @AndroidIgnoredOnParcel
    public val durationInTime: Duration by lazy {
        cues.map(CueWithDuration::durationAsTime).reduceOrNull { acc, duration -> acc + duration } ?: Duration.ZERO
    }
}