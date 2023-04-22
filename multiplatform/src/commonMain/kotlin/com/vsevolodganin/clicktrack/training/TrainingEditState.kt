package com.vsevolodganin.clicktrack.training

import com.vsevolodganin.clicktrack.model.CueDuration
import kotlin.time.Duration

data class TrainingEditState(
    val startingTempo: Int,
    val mode: TrainingMode,
    val activeSegmentLengthType: CueDuration.Type,
    val segmentLengthBeats: CueDuration.Beats,
    val segmentLengthMeasures: CueDuration.Measures,
    val segmentLengthTime: CueDuration.Time,
    val tempoChange: Int,
    val activeEndingKind: TrainingEndingKind,
    val endingByTempo: Ending.ByTempo,
    val endingByTime: Ending.ByTime,
    val errors: Set<Error>,
) {

    sealed interface Ending {
        val kind: TrainingEndingKind

        class ByTempo(val endingTempo: Int) : Ending {
            override val kind: TrainingEndingKind
                get() = TrainingEndingKind.BY_TEMPO
        }

        class ByTime(val duration: Duration) : Ending {
            override val kind: TrainingEndingKind
                get() = TrainingEndingKind.BY_TIME
        }
    }

    enum class TrainingMode {
        INCREASE_TEMPO, DECREASE_TEMPO
    }

    enum class Error {
        STARTING_TEMPO, TEMPO_CHANGE, ENDING_TEMPO
    }

    val segmentLength: CueDuration
        get() = when (activeSegmentLengthType) {
            CueDuration.Type.BEATS -> segmentLengthBeats
            CueDuration.Type.MEASURES -> segmentLengthMeasures
            CueDuration.Type.TIME -> segmentLengthTime
        }

    val ending: Ending
        get() = when (activeEndingKind) {
            TrainingEndingKind.BY_TIME -> endingByTime
            TrainingEndingKind.BY_TEMPO -> endingByTempo
        }
}
