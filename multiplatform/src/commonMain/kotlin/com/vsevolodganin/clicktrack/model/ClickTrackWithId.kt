package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class ClickTrackWithSpecificId<Id : ClickTrackId>(
    val id: Id,
    val value: ClickTrack,
) : Parcelable

typealias ClickTrackWithId = ClickTrackWithSpecificId<*>
typealias ClickTrackWithDatabaseId = ClickTrackWithSpecificId<ClickTrackId.Database>
