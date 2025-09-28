package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

sealed class ClickSoundSource {
    @Serializable
    data class Bundled(val resourcePath: String) : ClickSoundSource()

    @Serializable  
    data class Uri(val value: String) : ClickSoundSource()
}
