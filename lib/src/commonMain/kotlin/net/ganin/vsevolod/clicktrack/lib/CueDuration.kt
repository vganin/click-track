package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

@Serializable
public sealed class CueDuration : AndroidParcelable {

    @Serializable
    @AndroidParcelize
    public data class Beats(public val value: Int) : CueDuration()

    @Serializable
    @AndroidParcelize
    public data class Time(public val value: SerializableDuration) : CueDuration()
}
