package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditClickTrackState(
    val clickTrack: ClickTrackWithDatabaseId,
    val hasErrorInName: Boolean,
) : Parcelable
