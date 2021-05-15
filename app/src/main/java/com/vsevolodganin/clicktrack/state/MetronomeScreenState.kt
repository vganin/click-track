package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeScreenState(
    val clickTrack: ClickTrackWithId,
    val progress: Double?,
    val isPlaying: Boolean,
    val areOptionsExpanded: Boolean,
) : Parcelable
