package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.nanoseconds

@Serializable(with = DurationSerializer::class)
public expect class SerializableDuration(value: Duration) {
    public val value: Duration
}

internal object DurationSerializer : KSerializer<SerializableDuration> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(SerializableDuration::class.simpleName!!, PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): SerializableDuration {
        return SerializableDuration(decoder.decodeDouble().nanoseconds)
    }

    override fun serialize(encoder: Encoder, value: SerializableDuration) {
        encoder.encodeDouble(value.value.inNanoseconds)
    }
}
