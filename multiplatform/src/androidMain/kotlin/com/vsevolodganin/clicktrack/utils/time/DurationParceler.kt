package com.vsevolodganin.clicktrack.utils.time

import android.os.Parcel
import com.arkivanov.essenty.parcelable.Parceler
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

actual object DurationParceler : Parceler<Duration> {

    override fun create(parcel: Parcel): Duration {
        return parcel.readDouble().nanoseconds
    }

    override fun Duration.write(parcel: Parcel, flags: Int) {
        parcel.writeDouble(toDouble(DurationUnit.NANOSECONDS))
    }
}
