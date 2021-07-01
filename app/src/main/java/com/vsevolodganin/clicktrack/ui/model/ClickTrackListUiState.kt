package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId

data class ClickTrackListUiState(
    val items: List<ClickTrackWithDatabaseId>,
)
