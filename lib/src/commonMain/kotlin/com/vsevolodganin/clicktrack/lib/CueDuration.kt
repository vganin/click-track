@file:UseSerializers(DurationSerializer::class)

package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import com.vsevolodganin.clicktrack.lib.android.AndroidTypeParceler
import com.vsevolodganin.clicktrack.lib.android.parceler.DurationParceler
import com.vsevolodganin.clicktrack.lib.serializer.DurationSerializer
import kotlin.time.Duration
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
public sealed class CueDuration : AndroidParcelable {

    @Serializable
    @AndroidParcelize
    public data class Beats(public val value: Int) : CueDuration() {
        init {
            require(value >= 0) { "Beats count should be non-negative but was: $value" }
        }
    }

    @Serializable
    @AndroidParcelize
    public data class Measures(public val value: Int) : CueDuration() {
        init {
            require(value >= 0) { "Measures count should be non-negative but was: $value" }
        }
    }

    @Serializable
    @AndroidParcelize
    @AndroidTypeParceler<Duration, DurationParceler>
    public data class Time(public val value: Duration) : CueDuration() {
        init {
            require(value >= Duration.ZERO) { "Time should be non-negative but was: $value" }
        }
    }
}

public fun CueDuration.asTimeGiven(tempo: BeatsPerMinute, timeSignature: TimeSignature): Duration {
    return when (this) {
        is CueDuration.Time -> value
        is CueDuration.Beats -> tempo.interval * value
        is CueDuration.Measures -> tempo.interval * timeSignature.noteCount * value
    }
}
