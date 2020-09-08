package net.ganin.vsevolod.clicktrack.lib

import kotlin.time.Duration

public sealed class CueDuration {
    public class Beats(public val value: Int) : CueDuration()
    public class Time(public val value: Duration) : CueDuration()
}