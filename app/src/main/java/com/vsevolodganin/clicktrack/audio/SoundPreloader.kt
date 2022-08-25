package com.vsevolodganin.clicktrack.audio

import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSounds
import com.vsevolodganin.clicktrack.model.UserClickSounds
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class SoundPreloader @Inject constructor(
    private val clickSoundsRepository: ClickSoundsRepository,
    private val soundBank: SoundBank,
) {
    fun preload() {
        GlobalScope.launch {
            BuiltinClickSounds.values()
                .asSequence()
                .map(BuiltinClickSounds::sounds)
                .flatMap(ClickSounds::asIterable)
                .forEach(soundBank::get)

            clickSoundsRepository.getAll().firstOrNull()?.also { userSounds ->
                userSounds
                    .asSequence()
                    .map(UserClickSounds::value)
                    .flatMap(ClickSounds::asIterable)
                    .forEach(soundBank::get)
            }
        }
    }
}
