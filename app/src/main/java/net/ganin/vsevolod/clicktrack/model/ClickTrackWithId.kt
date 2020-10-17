package net.ganin.vsevolod.clicktrack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Parcelize
data class ClickTrackWithId(
    val id: Long,
    val value: ClickTrack
) : Parcelable
