package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class Cue(
    public val bpm: BeatsPerMinute,
    public val timeSignature: TimeSignature
) : AndroidParcelable
