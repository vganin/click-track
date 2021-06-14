package com.vsevolodganin.clicktrack.state.screen

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClickTrackListScreenState(val items: List<ClickTrackWithId>) : Parcelable
