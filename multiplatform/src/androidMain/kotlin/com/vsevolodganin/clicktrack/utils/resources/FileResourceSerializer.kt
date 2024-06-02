package com.vsevolodganin.clicktrack.utils.resources

import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

actual class FileResourceSerializer : KSerializer<FileResource> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("dev.icerock.moko.resources.FileResource", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): FileResource {
        return FileResource(decoder.decodeInt())
    }

    override fun serialize(encoder: Encoder, value: FileResource) {
        encoder.encodeInt(value.rawResId)
    }
}
