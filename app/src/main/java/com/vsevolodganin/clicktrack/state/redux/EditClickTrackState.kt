package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import java.util.UUID
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditClickTrackState(
    val id: ClickTrackId.Database,
    val name: String,
    val loop: Boolean,
    val cues: List<EditCueState>,
    val errors: Set<Error>,
) : Parcelable {
    enum class Error {
        NAME
    }
}

@Parcelize
data class EditCueState(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bpm: Int,
    val timeSignature: TimeSignature,
    val activeDurationType: DurationType,
    val beats: CueDuration.Beats,
    val measures: CueDuration.Measures,
    val time: CueDuration.Time,
    val pattern: NotePattern,
    val errors: Set<Error>,
) : Parcelable {
    enum class Error {
        BPM
    }

    enum class DurationType {
        BEATS,
        MEASURES,
        TIME,
    }
}

fun ClickTrackWithDatabaseId.toEditState() = EditClickTrackState(
    id = id,
    name = value.name,
    loop = value.loop,
    cues = value.cues.map { it.toEditState() },
    errors = emptySet()
)

fun Cue.toEditState() = EditCueState(
    name = name.orEmpty(),
    bpm = bpm.value,
    timeSignature = timeSignature,
    activeDurationType = when (duration) {
        is CueDuration.Beats -> EditCueState.DurationType.BEATS
        is CueDuration.Measures -> EditCueState.DurationType.MEASURES
        is CueDuration.Time -> EditCueState.DurationType.TIME
    },
    beats = if (duration is CueDuration.Beats) duration as CueDuration.Beats else DefaultBeatsDuration,
    measures = if (duration is CueDuration.Measures) duration as CueDuration.Measures else DefaultMeasuresDuration,
    time = if (duration is CueDuration.Time) duration as CueDuration.Time else DefaultTimeDuration,
    pattern = pattern,
    errors = emptySet(),
)
