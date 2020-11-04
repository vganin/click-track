package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration

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
    public data class Time(public val value: SerializableDuration) : CueDuration() {
        init {
            require(value.value >= Duration.ZERO) { "Time should be non-negative but was: $value" }
        }
    }
}
