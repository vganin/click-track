package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress

data class MetronomeUiState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val isPlaying: Boolean,
    val progress: PlayProgress?,
    val areOptionsExpanded: Boolean,
)
