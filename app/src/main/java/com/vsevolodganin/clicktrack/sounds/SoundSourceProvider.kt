package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import kotlinx.coroutines.flow.StateFlow

class SoundSourceProvider(private val sounds: StateFlow<ClickSounds?>) {

    private var alternateStrongBeat = false

    fun provide(type: ClickSoundType): ClickSoundSource? {
        val sounds = sounds.value ?: return null
        return when (type) {
            ClickSoundType.STRONG -> {
                when (alternateStrongBeat) {
                    true -> sounds.strongBeatAlternative ?: sounds.strongBeat
                    false -> sounds.strongBeat
                }.also { alternateStrongBeat = !alternateStrongBeat }
            }
            ClickSoundType.WEAK -> sounds.weakBeat
        }
    }
}
