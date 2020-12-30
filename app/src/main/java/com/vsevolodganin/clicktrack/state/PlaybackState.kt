package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaybackState(
    val clickTrack: ClickTrackWithId,
    val progress: Float,
) : Parcelable
