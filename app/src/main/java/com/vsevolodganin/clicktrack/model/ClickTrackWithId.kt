package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.ClickTrack
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClickTrackWithId(
    val id: ClickTrackId,
    val value: ClickTrack,
) : Parcelable
