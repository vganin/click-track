package com.vsevolodganin.clicktrack.play

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.PlayProgress

data class PlayClickTrackState(
    val clickTrack: ClickTrackWithDatabaseId,
    val isPlaying: Boolean,
    val playProgress: PlayProgress?,
    val playTrackingMode: Boolean,
)
