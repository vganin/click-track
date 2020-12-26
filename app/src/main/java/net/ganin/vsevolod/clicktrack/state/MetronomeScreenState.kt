package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.ganin.vsevolod.clicktrack.lib.BeatsPerMinute

@Parcelize
data class MetronomeScreenState(
    val bpm: BeatsPerMinute,
    val playbackStamp: PlaybackStamp?,
    val isPlaying: Boolean,
) : Parcelable
