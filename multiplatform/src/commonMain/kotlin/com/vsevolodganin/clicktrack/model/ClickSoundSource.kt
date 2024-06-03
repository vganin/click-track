package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.resources.FileResourceSerializer
import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.Serializable

sealed class ClickSoundSource {
    @Serializable
    data class Bundled(@Serializable(with = FileResourceSerializer::class) val audioResource: FileResource) : ClickSoundSource()

    @Serializable
    data class Uri(val value: String) : ClickSoundSource()
}
