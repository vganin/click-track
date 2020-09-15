package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable

@Serializable
public sealed class CueDuration {

    @Serializable
    public class Beats(public val value: Int) : CueDuration()

    @Serializable
    public class Time(public val value: SerializableDuration) : CueDuration()
}
