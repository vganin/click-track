package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId

@Parcelize
data class PlaybackState(
    val clickTrack: ClickTrackWithId,
    val playbackStamp: PlaybackStamp
) : Parcelable