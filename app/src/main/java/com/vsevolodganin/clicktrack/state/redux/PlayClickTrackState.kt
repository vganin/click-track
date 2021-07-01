package com.vsevolodganin.clicktrack.state.redux

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackId
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayClickTrackState(
    val id: ClickTrackId.Database,
) : Parcelable
