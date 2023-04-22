package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.TypeParceler
import com.vsevolodganin.clicktrack.utils.time.DurationParceler
import com.vsevolodganin.clicktrack.utils.time.DurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface CueDuration : Parcelable {

    enum class Type {
        BEATS,
        MEASURES,
        TIME,
    }

    val type: Type

    @Serializable
    @Parcelize
    @SerialName("com.vsevolodganin.clicktrack.lib.CueDuration.Beats") // For backward compatibility
    data class Beats(val value: Int) : CueDuration {
        init {
            require(value >= 0) { "Beats count should be non-negative but was: $value" }
        }

        override val type: Type
            get() = Type.BEATS
    }

    @Serializable
    @Parcelize
    @SerialName("com.vsevolodganin.clicktrack.lib.CueDuration.Measures") // For backward compatibility
    data class Measures(val value: Int) : CueDuration {
        init {
            require(value >= 0) { "Measures count should be non-negative but was: $value" }
        }

        override val type: Type
            get() = Type.MEASURES
    }

    @Serializable
    @Parcelize
    @TypeParceler<Duration, DurationParceler>
    @SerialName("com.vsevolodganin.clicktrack.lib.CueDuration.Time") // For backward compatibility
    data class Time(@Serializable(DurationSerializer::class) val value: Duration) : CueDuration {
        init {
            require(value >= Duration.ZERO) { "Time should be non-negative but was: $value" }
        }

        override val type: Type
            get() = Type.TIME
    }
}

fun CueDuration.asTimeGiven(tempo: BeatsPerMinute, timeSignature: TimeSignature): Duration {
    return when (this) {
        is CueDuration.Time -> value
        is CueDuration.Beats -> tempo.interval * value
        is CueDuration.Measures -> tempo.interval * timeSignature.noteCount * value
    }
}
