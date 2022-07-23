package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.BeatsPerMinuteDiff
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.redux.EditCueState
import java.util.UUID

data class EditClickTrackUiState(
    val id: ClickTrackId.Database,
    val name: String,
    val loop: Boolean,
    val tempoDiff: BeatsPerMinuteDiff,
    val cues: List<EditCueUiState>,
    val errors: Set<EditClickTrackState.Error>,
    val showForwardButton: Boolean,
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
