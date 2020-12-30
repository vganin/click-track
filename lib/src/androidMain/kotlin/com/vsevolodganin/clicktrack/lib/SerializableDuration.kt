package com.vsevolodganin.clicktrack.lib

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.nanoseconds

@Serializable(with = DurationSerializer::class)
@Parcelize
public actual data class SerializableDuration actual constructor(
    public actual val value: Duration
) : Parcelable {

    public companion object : Parceler<SerializableDuration> {
        override fun SerializableDuration.write(parcel: Parcel, flags: Int) {
            parcel.writeDouble(value.inNanoseconds)
        }

        override fun create(parcel: Parcel): SerializableDuration {
            return SerializableDuration(parcel.readDouble().nanoseconds)
        }
    }
}
