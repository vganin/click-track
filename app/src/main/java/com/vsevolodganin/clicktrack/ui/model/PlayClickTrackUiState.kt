package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId

data class PlayClickTrackUiState(
    val clickTrack: ClickTrackWithDatabaseId,
    val isPlaying: Boolean,
    val progress: ClickTrackProgress?,
)
