package com.vsevolodganin.clicktrack.state.screen

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val isErrorInName: Boolean,
    val defaultCue: Cue = Cue(
        bpm = 60.bpm,
        timeSignature = TimeSignature(4, 4),
        duration = CueDuration.Measures(1),
    ),
) : Parcelable
