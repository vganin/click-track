package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.android.parceler.DurationParceler
import com.vsevolodganin.clicktrack.lib.serializer.DurationSerializer
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.utils.optionalCast
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
enum class TrainingMode {
    INCREASE_TEMPO, DECREASE_TEMPO
}

@Parcelize
data class TrainingState(
    val startingTempo: Int,
    val mode: TrainingMode,
    val activeSegmentLengthType: EditCueState.DurationType,
    val segmentLengthBeats: CueDuration.Beats,
    val segmentLengthMeasures: CueDuration.Measures,
    val segmentLengthTime: CueDuration.Time,
    val tempoChange: Int,
    val activeEndingKind: EndingKind,
    val endingByTempo: Ending.ByTempo,
    val endingByTime: Ending.ByTime,
    val errors: Set<Error>,
) : Parcelable {

    sealed interface Ending : Parcelable {

        val kind: EndingKind

        @Parcelize
        class ByTempo(val endingTempo: Int) : Ending {
            override val kind: EndingKind
                get() = EndingKind.BY_TEMPO
        }

        @Parcelize
        @TypeParceler<Duration, DurationParceler>
        class ByTime(@Serializable(with = DurationSerializer::class) val duration: Duration) : Ending {
            override val kind: EndingKind
                get() = EndingKind.BY_TIME
        }
    }

    enum class EndingKind {
        BY_TEMPO, BY_TIME
    }

    enum class Error {
        STARTING_TEMPO, TEMPO_CHANGE, ENDING_TEMPO
    }
}

@Serializable
data class TrainingPersistableState(
    val startingTempo: BeatsPerMinute,
    val mode: TrainingMode,
    val segmentLength: CueDuration,
    val tempoChange: BeatsPerMinute,
    val ending: Ending,
) {

    @Serializable
    sealed class Ending {

        @Serializable
        class ByTempo(val endingTempo: BeatsPerMinute) : Ending()

        @Serializable
        class ByTime(@Serializable(with = DurationSerializer::class) val duration: Duration) : Ending()
    }
}

val DefaultEndingByTempo = TrainingState.Ending.ByTempo(160)
val DefaultEndingByTime = TrainingState.Ending.ByTime(4.minutes)

fun TrainingPersistableState.Ending.toCommon(): TrainingState.Ending = when (this) {
    is TrainingPersistableState.Ending.ByTempo -> TrainingState.Ending.ByTempo(endingTempo.value)
    is TrainingPersistableState.Ending.ByTime -> TrainingState.Ending.ByTime(duration)
}

fun TrainingPersistableState.toCommon(): TrainingState {
    val commonEnding = ending.toCommon()
    return TrainingState(
        startingTempo = startingTempo.value,
        mode = mode,
        activeSegmentLengthType = segmentLength.type,
        segmentLengthBeats = segmentLength.optionalCast<CueDuration.Beats>() ?: DefaultBeatsDuration,
        segmentLengthMeasures = segmentLength.optionalCast<CueDuration.Measures>() ?: DefaultMeasuresDuration,
        segmentLengthTime = segmentLength.optionalCast<CueDuration.Time>() ?: DefaultTimeDuration,
        tempoChange = tempoChange.value,
        activeEndingKind = commonEnding.kind,
        endingByTempo = commonEnding.optionalCast<TrainingState.Ending.ByTempo>() ?: DefaultEndingByTempo,
        endingByTime = commonEnding.optionalCast<TrainingState.Ending.ByTime>() ?: DefaultEndingByTime,
        errors = emptySet(),
    )
}
