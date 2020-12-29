package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId

@Parcelize
data class PlayClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val progress: Float?,
    val isPlaying: Boolean,
) : Parcelable
