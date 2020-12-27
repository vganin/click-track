package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId

@Parcelize
data class EditClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val isErrorInName: Boolean,
    val defaultCue: CueWithDuration = CueWithDuration(
        duration = CueDuration.Measures(1),
        cue = Cue(120.bpm, TimeSignature(4, 4))
    ),
) : Parcelable
