package com.vsevolodganin.clicktrack.list

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClickTrackListState(
    val items: List<ClickTrackWithDatabaseId>,
) : Parcelable
