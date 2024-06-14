package com.vsevolodganin.clicktrack.utils.resources

import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import platform.Foundation.NSBundle

@OptIn(ExperimentalSerializationApi::class)
actual class FileResourceSerializer : KSerializer<FileResource> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.icerock.moko.resources.FileResource") {
        element<String>("fileName")
        element<String>("extension")
        element<String?>("bundleIdentifier")
    }

    override fun deserialize(decoder: Decoder): FileResource {
        return with(decoder) {
            FileResource(
                fileName = decodeString(),
                extension = decodeString(),
                bundle = decodeNullableSerializableValue(String.serializer())
                    ?.let(NSBundle.Companion::bundleWithIdentifier)
                    ?: NSBundle.mainBundle,
            )
        }
    }

    override fun serialize(encoder: Encoder, value: FileResource) {
        with(encoder) {
            encodeString(value.fileName)
            encodeString(value.extension)
            encodeNullableSerializableValue(String.serializer(), value.bundle.bundleIdentifier)
        }
    }
}
