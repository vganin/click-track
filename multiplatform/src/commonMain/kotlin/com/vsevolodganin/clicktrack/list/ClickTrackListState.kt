package com.vsevolodganin.clicktrack.list

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId

@Parcelize
data class ClickTrackListState(
    val items: List<ClickTrackWithDatabaseId>,
) : Parcelable
