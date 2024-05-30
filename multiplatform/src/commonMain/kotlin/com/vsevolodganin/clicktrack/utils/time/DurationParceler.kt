@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION")

package com.vsevolodganin.clicktrack.utils.time

import com.vsevolodganin.clicktrack.utils.parcelable.Parceler
import kotlin.time.Duration

expect object DurationParceler : Parceler<Duration>
