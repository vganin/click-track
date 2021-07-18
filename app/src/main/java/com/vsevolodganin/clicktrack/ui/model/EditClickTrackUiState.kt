package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.state.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.state.redux.EditCueState
import java.util.UUID

data class EditClickTrackUiState(
    val name: String,
    val loop: Boolean,
    val cues: List<EditCueUiState>,
    val errors: Set<EditClickTrackState.Error>,
)

data class EditCueUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bpm: Int,
    val timeSignature: TimeSignature,
    val duration: CueDuration,
    val pattern: NotePattern,
    val errors: Set<EditCueState.Error>,
)
