package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.SerializableDuration

@Parcelize
data class PlaybackStamp(
    val timestamp: SerializableDuration,
    val duration: SerializableDuration,
) : Parcelable
