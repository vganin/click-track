package com.vsevolodganin.clicktrack.state.screen

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeScreenState(
    val clickTrack: ClickTrackWithId,
    val progress: ClickTrackProgress?,
    val isPlaying: Boolean,
    val areOptionsExpanded: Boolean,
) : Parcelable
