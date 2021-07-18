package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.model.ClickTrackProgress

data class MetronomeUiState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val isPlaying: Boolean,
    val progress: ClickTrackProgress?,
    val areOptionsExpanded: Boolean,
)
