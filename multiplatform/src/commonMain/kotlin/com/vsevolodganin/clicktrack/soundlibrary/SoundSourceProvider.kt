package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSounds
import kotlinx.coroutines.flow.StateFlow

class SoundSourceProvider(private val sounds: StateFlow<ClickSounds?>) {
    fun provide(type: ClickSoundType): ClickSoundSource? {
        val sounds = sounds.value ?: return null
        return when (type) {
            ClickSoundType.STRONG -> sounds.strongBeat
            ClickSoundType.WEAK -> sounds.weakBeat
        }
    }
}
