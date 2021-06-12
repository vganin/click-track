package com.vsevolodganin.clicktrack.lib.android.parceler

import android.os.Parcel
import com.vsevolodganin.clicktrack.lib.android.AndroidParceler
import kotlin.time.Duration
import kotlin.time.DurationUnit

public actual object DurationParceler : AndroidParceler<Duration> {

    override fun create(parcel: Parcel): Duration {
        return Duration.nanoseconds(parcel.readDouble())
    }

    override fun Duration.write(parcel: Parcel, flags: Int) {
        parcel.writeDouble(toDouble(DurationUnit.NANOSECONDS))
    }
}
