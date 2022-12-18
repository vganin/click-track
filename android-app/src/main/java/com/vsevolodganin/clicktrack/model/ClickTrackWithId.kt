package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClickTrackWithSpecificId<Id : ClickTrackId>(
    val id: Id,
    val value: ClickTrack,
) : Parcelable

typealias ClickTrackWithId = ClickTrackWithSpecificId<*>
typealias ClickTrackWithDatabaseId = ClickTrackWithSpecificId<ClickTrackId.Database>