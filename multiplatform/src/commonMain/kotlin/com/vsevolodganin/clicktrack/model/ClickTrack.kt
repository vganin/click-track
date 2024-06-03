package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class ClickTrack(
    val name: String,
    val cues: List<Cue>,
    val loop: Boolean,
    @SerialName("tempoDiff") // Legacy name
    val tempoOffset: BeatsPerMinuteOffset = BeatsPerMinuteOffset.ZERO,
) {
    val durationInTime: Duration by lazy {
        cues.map { it.durationAsTimeWithBpmOffset(tempoOffset) }
            .reduceOrNull { acc, duration -> acc + duration }
            ?: Duration.ZERO
    }
}
