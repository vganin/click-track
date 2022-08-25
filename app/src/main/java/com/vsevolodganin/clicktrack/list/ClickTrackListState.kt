package com.vsevolodganin.clicktrack.list

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId

data class ClickTrackListState(
    val items: List<ClickTrackWithDatabaseId>,
)
