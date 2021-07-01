package com.vsevolodganin.clicktrack.player

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaybackState(
    val clickTrack: ClickTrackWithId,
    val progress: ClickTrackProgress,
) : Parcelable
