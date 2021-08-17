package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidIgnoredOnParcel
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class TwoLayerPolyrhythm(
    val bpm: BeatsPerMinute,
    val layer1: Int,
    val layer2: Int,
) : AndroidParcelable {

    @AndroidIgnoredOnParcel
    val durationInTime: Duration by lazy {
        bpm.interval * layer1
    }
}
