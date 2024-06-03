package com.vsevolodganin.clicktrack.play

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.PlayProgress
import kotlinx.serialization.Serializable

@Serializable
data class PlayClickTrackState(
    val clickTrack: ClickTrackWithDatabaseId,
    val playProgress: PlayProgress?,
    val playTrackingMode: Boolean,
)

val PlayClickTrackState.isPlaying: Boolean get() = playProgress != null
val PlayClickTrackState.isPaused: Boolean get() = playProgress?.isPaused == true
