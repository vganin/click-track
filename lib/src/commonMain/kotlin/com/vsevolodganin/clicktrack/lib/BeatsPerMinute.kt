package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.minutes

@Serializable
@AndroidParcelize
public data class BeatsPerMinute(
    public val value: Int
) : AndroidParcelable {
    init {
        require(value >= 0) { "Bpm should be greater than 0 but was: $value" }
    }

    public operator fun plus(o: Int): BeatsPerMinute {
        return BeatsPerMinute((value + o).coerceAtLeast(0))
    }

    public operator fun plus(o: BeatsPerMinute): BeatsPerMinute {
        return this + o.value
    }

    public operator fun minus(o: Int): BeatsPerMinute {
        return BeatsPerMinute((value - o).coerceAtLeast(0))
    }

    public operator fun minus(o: BeatsPerMinute): BeatsPerMinute {
        return this - o.value
    }
}

public val Int.bpm: BeatsPerMinute get() = BeatsPerMinute(this)

public val BeatsPerMinute.interval: Duration
    get() {
        return 1.minutes / value
    }
