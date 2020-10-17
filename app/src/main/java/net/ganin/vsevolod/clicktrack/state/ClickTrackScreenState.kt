package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId

@Parcelize
data class ClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val isPlaying: Boolean,
    val playbackStamp: PlaybackStamp?
) : Parcelable
