package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta

@Parcelize
class ClickTrackListScreenState(val items: List<ClickTrackWithMeta>) : Parcelable
