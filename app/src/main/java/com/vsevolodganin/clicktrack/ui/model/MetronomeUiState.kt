package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.model.PlayableProgress

data class MetronomeUiState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val isPlaying: Boolean,
    val progress: PlayableProgress?,
    val areOptionsExpanded: Boolean,
)
