package com.vsevolodganin.clicktrack.play

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.PlayProgress
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayClickTrackState(
    val clickTrack: ClickTrackWithDatabaseId,
    val playProgress: PlayProgress?,
    val playTrackingMode: Boolean,
) : Parcelable

val PlayClickTrackState.isPlaying: Boolean get() = playProgress != null
val PlayClickTrackState.isPaused: Boolean get() = playProgress?.isPaused == true
