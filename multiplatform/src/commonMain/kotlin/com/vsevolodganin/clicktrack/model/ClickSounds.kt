package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

@Serializable
data class GenericClickSounds<T : ClickSoundSource>(
    val strongBeat: T?,
    val weakBeat: T?,
) {
    val asIterable: Iterable<T> get() = listOfNotNull(strongBeat, weakBeat)

    fun beatByType(type: ClickSoundType) =
        when (type) {
            ClickSoundType.STRONG -> strongBeat
            ClickSoundType.WEAK -> weakBeat
        }
}

typealias ClickSounds = GenericClickSounds<*>
typealias UriClickSounds = GenericClickSounds<ClickSoundSource.Uri>
