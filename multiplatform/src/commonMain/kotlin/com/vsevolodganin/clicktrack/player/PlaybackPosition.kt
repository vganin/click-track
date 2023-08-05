package com.vsevolodganin.clicktrack.player

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.TypeParceler
import com.vsevolodganin.clicktrack.utils.time.DurationParceler
import kotlin.time.Duration

@Parcelize
@TypeParceler<Duration, DurationParceler>
data class PlaybackPosition(
    val value: Duration,
    val emissionTime: PlayableProgressTimeMark = PlayableProgressTimeSource.markNow()
) : Parcelable
