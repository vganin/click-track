@file:UseSerializers(DurationSerializer::class)

package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import com.vsevolodganin.clicktrack.utils.time.DurationParceler
import com.vsevolodganin.clicktrack.utils.time.DurationSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.time.Duration

@Serializable
sealed class CueDuration : Parcelable {

    @Serializable
    @Parcelize
    @SerialName("com.vsevolodganin.clicktrack.lib.CueDuration.Beats") // For backward compatibility
    data class Beats(val value: Int) : CueDuration() {
        init {
            require(value >= 0) { "Beats count should be non-negative but was: $value" }
        }
    }

    @Serializable
    @Parcelize
    @SerialName("com.vsevolodganin.clicktrack.lib.CueDuration.Measures") // For backward compatibility
    data class Measures(val value: Int) : CueDuration() {
        init {
            require(value >= 0) { "Measures count should be non-negative but was: $value" }
        }
    }

    @Serializable
    @Parcelize
    @TypeParceler<Duration, DurationParceler>
    @SerialName("com.vsevolodganin.clicktrack.lib.CueDuration.Time") // For backward compatibility
    data class Time(val value: Duration) : CueDuration() {
        init {
            require(value >= Duration.ZERO) { "Time should be non-negative but was: $value" }
        }
    }
}

fun CueDuration.asTimeGiven(tempo: BeatsPerMinute, timeSignature: TimeSignature): Duration {
    return when (this) {
        is CueDuration.Time -> value
        is CueDuration.Beats -> tempo.interval * value
        is CueDuration.Measures -> tempo.interval * timeSignature.noteCount * value
    }
}
