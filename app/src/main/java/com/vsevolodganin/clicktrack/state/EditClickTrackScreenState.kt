package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.CueWithDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val isErrorInName: Boolean,
    val defaultCue: CueWithDuration = CueWithDuration(
        duration = CueDuration.Measures(1),
        cue = Cue(120.bpm, TimeSignature(4, 4))
    ),
) : Parcelable
