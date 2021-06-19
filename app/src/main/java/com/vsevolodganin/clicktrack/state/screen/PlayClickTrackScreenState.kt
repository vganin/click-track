package com.vsevolodganin.clicktrack.state.screen

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val progress: ClickTrackProgress?,
    val isPlaying: Boolean,
) : Parcelable
