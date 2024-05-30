package com.vsevolodganin.clicktrack.utils.time

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DurationSerializer", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeDouble().nanoseconds
    }

    override fun serialize(
        encoder: Encoder,
        value: Duration,
    ) {
        encoder.encodeDouble(value.toDouble(DurationUnit.NANOSECONDS))
    }
}
