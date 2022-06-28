package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@Parcelize
data class Cue(
    val name: String? = null,
    val bpm: BeatsPerMinute,
    val timeSignature: TimeSignature,
    val duration: CueDuration,
    val pattern: NotePattern = NotePattern.STRAIGHT_X1,
) : Parcelable {

    val durationAsTime: Duration
        get() {
            return duration.asTimeGiven(bpm, timeSignature)
        }
}
