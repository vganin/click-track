package com.vsevolodganin.clicktrack.lib.serializer

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Duration::class)
internal object DurationSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(Duration::class.simpleName!!, PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Duration {
        return Duration.nanoseconds(decoder.decodeDouble())
    }

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeDouble(value.toDouble(DurationUnit.NANOSECONDS))
    }
}
