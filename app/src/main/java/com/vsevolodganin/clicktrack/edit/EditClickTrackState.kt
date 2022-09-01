package com.vsevolodganin.clicktrack.edit

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class EditClickTrackState(
    val id: ClickTrackId.Database,
    val name: String,
    val loop: Boolean,
    val tempoDiff: BeatsPerMinuteDiff,
    val cues: List<EditCueState>,
    val showForwardButton: Boolean,
) : Parcelable

@Parcelize
data class EditCueState(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bpm: Int,
    val timeSignature: TimeSignature,
    val activeDurationType: CueDuration.Type,
    val beats: CueDuration.Beats,
    val measures: CueDuration.Measures,
    val time: CueDuration.Time,
    val pattern: NotePattern,
    val errors: Set<Error>,
) : Parcelable {
    enum class Error {
        BPM
    }

    val duration: CueDuration
        get() = when (activeDurationType) {
            CueDuration.Type.BEATS -> beats
            CueDuration.Type.MEASURES -> measures
            CueDuration.Type.TIME -> time
        }
}
