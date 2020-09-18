package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration

@Parcelize
data class ClickTrackScreenState(
    val clickTrack: ClickTrack,
    val isPlaying: Boolean,
    val playbackTimestamp: SerializableDuration?
) : Parcelable
