package com.vsevolodganin.clicktrack.training

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.CueDuration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class TrainingValidState(
    val startingTempo: BeatsPerMinute,
    val mode: TrainingEditState.TrainingMode,
    val segmentLength: CueDuration,
    val tempoChange: BeatsPerMinute,
    val ending: Ending,
) {
    @Serializable
    sealed interface Ending {
        val kind: TrainingEndingKind

        @Serializable
        @SerialName("com.vsevolodganin.clicktrack.state.redux.TrainingPersistableState.Ending.ByTempo") // For backward compatibility
        class ByTempo(val endingTempo: BeatsPerMinute) : Ending {
            override val kind: TrainingEndingKind
                get() = TrainingEndingKind.BY_TEMPO
        }

        @Serializable
        @SerialName("com.vsevolodganin.clicktrack.state.redux.TrainingPersistableState.Ending.ByTime") // For backward compatibility
        class ByTime(
            @Suppress("DEPRECATION") // Kept legacy serializer for backward compatibility
            @Serializable(with = com.vsevolodganin.clicktrack.utils.time.LegacyDurationSerializer::class)
            val duration: Duration,
        ) : Ending {
            override val kind: TrainingEndingKind
                get() = TrainingEndingKind.BY_TIME
        }
    }
}
