package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

@Serializable
data class ClickTrackWithSpecificId<Id : ClickTrackId>(
    val id: Id,
    val value: ClickTrack,
)

typealias ClickTrackWithId = ClickTrackWithSpecificId<*>
typealias ClickTrackWithDatabaseId = ClickTrackWithSpecificId<ClickTrackId.Database>
