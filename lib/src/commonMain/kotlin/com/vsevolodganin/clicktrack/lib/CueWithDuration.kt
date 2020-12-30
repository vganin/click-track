package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@AndroidParcelize
public data class CueWithDuration(
    public val duration: CueDuration,
    public val cue: Cue,
) : AndroidParcelable

public val CueWithDuration.durationAsTime: Duration
    get() {
        return when (duration) {
            is CueDuration.Time -> duration.value.value
            is CueDuration.Beats -> cue.bpm.interval * duration.value
            is CueDuration.Measures -> cue.bpm.interval * cue.timeSignature.noteCount * duration.value
        }
    }
