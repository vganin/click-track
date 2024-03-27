@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION")

package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.IgnoredOnParcel
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Parcelize
data class ClickTrack(
    val name: String,
    val cues: List<Cue>,
    val loop: Boolean,
    val tempoDiff: BeatsPerMinuteDiff = BeatsPerMinuteDiff.ZERO,
) : Parcelable {

    @IgnoredOnParcel
    val durationInTime: Duration by lazy {
        cues.map { it.durationAsTimeWithBpmOffset(tempoDiff) }
            .reduceOrNull { acc, duration -> acc + duration }
            ?: Duration.ZERO
    }
}
