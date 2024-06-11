package com.vsevolodganin.clicktrack.list

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import kotlinx.serialization.Serializable

@Serializable
data class ClickTrackListState(
    val items: List<ClickTrackWithDatabaseId>,
)
