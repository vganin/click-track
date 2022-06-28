package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Parcelize
data class ClickTrack(
    val name: String,
    val cues: List<Cue>,
    val loop: Boolean,
) : Parcelable {

    @IgnoredOnParcel
    val durationInTime: Duration by lazy {
        cues.map(Cue::durationAsTime).reduceOrNull { acc, duration -> acc + duration } ?: Duration.ZERO
    }
}
