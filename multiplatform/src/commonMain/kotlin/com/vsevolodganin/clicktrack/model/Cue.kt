package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Cue(
    val name: String? = null,
    val bpm: BeatsPerMinute,
    val timeSignature: TimeSignature,
    val duration: CueDuration,
    val pattern: NotePattern = NotePattern.STRAIGHT_X1,
) {
    fun durationAsTimeWithBpmOffset(offset: BeatsPerMinuteOffset): Duration {
        return duration.asTimeGiven(bpm + offset, timeSignature)
    }
}
