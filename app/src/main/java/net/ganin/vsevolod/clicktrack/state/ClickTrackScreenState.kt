package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Parcelize
data class ClickTrackScreenState(
    val clickTrack: ClickTrack,
    val isPlaying: Boolean,
    val playbackStamp: PlaybackStamp?
) : Parcelable
