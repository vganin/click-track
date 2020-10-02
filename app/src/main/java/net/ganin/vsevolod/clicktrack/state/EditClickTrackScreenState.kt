package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm

@Parcelize
data class EditClickTrackScreenState(
    val clickTrack: ClickTrackWithMeta,
    val defaultCue: CueWithDuration = CueWithDuration(
        duration = CueDuration.Beats(4),
        cue = Cue(60.bpm, TimeSignature(4, 4))
    )
) : Parcelable
