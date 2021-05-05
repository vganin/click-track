package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class Cue(
    public val name: String? = null,
    public val bpm: BeatsPerMinute,
    public val timeSignature: TimeSignature,
    public val duration: CueDuration,
    public val pattern: NotePattern = NotePattern.STRAIGHT_X1,
) : AndroidParcelable {

    val durationAsTime: Duration
        get() {
            return when (duration) {
                is CueDuration.Time -> duration.value.value
                is CueDuration.Beats -> bpm.interval * duration.value
                is CueDuration.Measures -> bpm.interval * timeSignature.noteCount * duration.value
            }
        }
}
