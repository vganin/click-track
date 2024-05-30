@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION")

package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.IgnoredOnParcel
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Parcelize
data class TwoLayerPolyrhythm(
    val bpm: BeatsPerMinute,
    val layer1: Int,
    val layer2: Int,
) : Parcelable {
    @IgnoredOnParcel
    val durationInTime: Duration by lazy {
        bpm.interval * layer1
    }

    fun isPlayable() = layer1 > 0 && layer2 > 0
}
