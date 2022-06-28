package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress

data class MetronomeUiState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val isPlaying: Boolean,
    val progress: PlayProgress?,
    val areOptionsExpanded: Boolean,
)
