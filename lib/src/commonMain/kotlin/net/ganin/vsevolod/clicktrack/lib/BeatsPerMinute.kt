package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration
import kotlin.time.minutes

@Serializable
@AndroidParcelize
public class BeatsPerMinute(
    public val value: Int
) : AndroidParcelable {
    init {
        require(value >= 0) { "Bpm should be greater than 0 but was: $value" }
    }
}

public val Int.bpm: BeatsPerMinute get() = BeatsPerMinute(this)

public val BeatsPerMinute.interval: Duration
    get() {
        return 1.minutes / value
    }
