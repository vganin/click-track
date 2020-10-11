package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

@Serializable
@AndroidParcelize
public data class Cue(
    public val bpm: BeatsPerMinute,
    public val timeSignature: TimeSignature
) : AndroidParcelable
