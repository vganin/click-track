package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId

@Parcelize
data class PlayClickTrackScreenState(
    val clickTrack: ClickTrackWithId,
    val playbackStamp: PlaybackStamp?,
    val isPlaying: Boolean
) : Parcelable

fun PlayClickTrackScreenState(
    clickTrack: ClickTrackWithId,
    currentlyPlaying: PlaybackState?
): PlayClickTrackScreenState {
    val playbackStamp: PlaybackStamp?
    val isPlaying: Boolean

    if (currentlyPlaying != null && currentlyPlaying.clickTrack.id == clickTrack.id) {
        playbackStamp = currentlyPlaying.playbackStamp
        isPlaying = true
    } else {
        playbackStamp = null
        isPlaying = false
    }

    return PlayClickTrackScreenState(
        clickTrack = clickTrack,
        isPlaying = isPlaying,
        playbackStamp = playbackStamp
    )
}
