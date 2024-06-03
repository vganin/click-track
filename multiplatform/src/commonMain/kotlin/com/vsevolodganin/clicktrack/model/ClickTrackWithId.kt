package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

@Parcelize
data class ClickTrackWithSpecificId<Id : ClickTrackId>(
    val id: Id,
    val value: ClickTrack,
) : Parcelable

typealias ClickTrackWithId = ClickTrackWithSpecificId<*>
typealias ClickTrackWithDatabaseId = ClickTrackWithSpecificId<ClickTrackId.Database>
