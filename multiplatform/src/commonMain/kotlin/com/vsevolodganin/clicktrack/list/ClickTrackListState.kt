package com.vsevolodganin.clicktrack.list

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

@Parcelize
data class ClickTrackListState(
    val items: List<ClickTrackWithDatabaseId>,
) : Parcelable
