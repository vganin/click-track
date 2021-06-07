@file:UseSerializers(DurationSerializer::class)

package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
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
    public data class Time(public val value: Duration) : CueDuration() {
        init {
            require(value >= Duration.ZERO) { "Time should be non-negative but was: $value" }
        }
    }
}
