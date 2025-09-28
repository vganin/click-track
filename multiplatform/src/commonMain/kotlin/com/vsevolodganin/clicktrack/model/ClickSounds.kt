package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

@Serializable
data class ClickSounds(
    val strongBeat: ClickSoundSource?,
    val weakBeat: ClickSoundSource?,
) {
    val asIterable: Iterable<ClickSoundSource> get() = listOfNotNull(strongBeat, weakBeat)

    fun beatByType(type: ClickSoundType) = when (type) {
        ClickSoundType.STRONG -> strongBeat
        ClickSoundType.WEAK -> weakBeat
    }
}
