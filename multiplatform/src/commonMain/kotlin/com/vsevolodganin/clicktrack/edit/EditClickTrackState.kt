package com.vsevolodganin.clicktrack.edit

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.benasher44.uuid.uuid4
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature

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
    val id: String = uuid4().toString(),
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
