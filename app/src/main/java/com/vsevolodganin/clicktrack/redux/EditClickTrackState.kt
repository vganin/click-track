package com.vsevolodganin.clicktrack.redux

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.utils.optionalCast
import kotlinx.parcelize.Parcelize
import java.util.UUID

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
    activeDurationType = duration.type,
    beats = duration.optionalCast<CueDuration.Beats>() ?: DefaultBeatsDuration,
    measures = duration.optionalCast<CueDuration.Measures>() ?: DefaultMeasuresDuration,
    time = duration.optionalCast<CueDuration.Time>() ?: DefaultTimeDuration,
    pattern = pattern,
    errors = emptySet(),
)

val CueDuration.type: EditCueState.DurationType
    get() = when (this) {
        is CueDuration.Beats -> EditCueState.DurationType.BEATS
        is CueDuration.Measures -> EditCueState.DurationType.MEASURES
        is CueDuration.Time -> EditCueState.DurationType.TIME
    }
