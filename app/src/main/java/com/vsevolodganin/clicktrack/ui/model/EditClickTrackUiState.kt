package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId

class EditClickTrackUiState(
    val clickTrack: ClickTrackWithDatabaseId,
    val defaultCue: Cue,
    val hasErrorInName: Boolean,
)
