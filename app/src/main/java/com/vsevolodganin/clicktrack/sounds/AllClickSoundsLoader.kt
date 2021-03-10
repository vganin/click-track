package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.player.PlayerSoundPool
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

class AllClickSoundsLoader @Inject constructor(
    private val clickSoundsRepository: ClickSoundsRepository,
    private val playerSoundPool: PlayerSoundPool,
) {
    suspend fun reload() {
        playerSoundPool.preload(
            BuiltinClickSounds.values()
                .asSequence()
                .map(BuiltinClickSounds::sounds)
                .flatMap(ClickSounds::asIterable)
                .asIterable()
        )

        clickSoundsRepository.getAll().firstOrNull()?.also { userSounds ->
            val userSoundsFlattened = userSounds
                .asSequence()
                .map(UserClickSounds::value)
                .flatMap(ClickSounds::asIterable)
                .asIterable()
            playerSoundPool.preload(userSoundsFlattened)
        }
    }
}