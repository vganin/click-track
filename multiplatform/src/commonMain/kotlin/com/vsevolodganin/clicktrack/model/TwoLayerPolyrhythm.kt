package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class TwoLayerPolyrhythm(
    val bpm: BeatsPerMinute,
    val layer1: Int,
    val layer2: Int,
) {
    val durationInTime: Duration by lazy {
        bpm.interval * layer1
    }

    fun isPlayable() = layer1 > 0 && layer2 > 0
}
