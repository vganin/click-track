package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Parcelize
class ClickTrackListScreenState(val items: List<ClickTrack>) : Parcelable
