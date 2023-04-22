package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
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

    fun durationAsTimeWithBpmOffset(offset: BeatsPerMinuteDiff): Duration {
        return duration.asTimeGiven(bpm + offset, timeSignature)
    }
}
