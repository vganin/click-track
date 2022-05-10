package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.PlayProgress

data class PlayClickTrackUiState(
    val clickTrack: ClickTrackWithDatabaseId,
    val isPlaying: Boolean,
    val progress: PlayProgress?,
)
