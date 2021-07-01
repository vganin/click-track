package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.ClickTrackWithId

data class MetronomeUiState(
    val clickTrack: ClickTrackWithId,
    val isPlaying: Boolean,
    val progress: ClickTrackProgress?,
    val areOptionsExpanded: Boolean,
)
