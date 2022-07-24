package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
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
